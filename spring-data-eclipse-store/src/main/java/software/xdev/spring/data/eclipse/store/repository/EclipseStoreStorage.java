/*
 * Copyright © 2024 XDEV Software (https://xdev.software)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package software.xdev.spring.data.eclipse.store.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.serializer.persistence.binary.jdk17.java.util.BinaryHandlerImmutableCollectionsList12;
import org.eclipse.serializer.persistence.binary.jdk17.java.util.BinaryHandlerImmutableCollectionsSet12;
import org.eclipse.serializer.persistence.types.Storer;
import org.eclipse.serializer.reference.ObjectSwizzling;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageFoundation;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.eclipse.store.storage.types.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.xdev.spring.data.eclipse.store.core.EntityListProvider;
import software.xdev.spring.data.eclipse.store.core.EntityProvider;
import software.xdev.spring.data.eclipse.store.exceptions.AlreadyRegisteredException;
import software.xdev.spring.data.eclipse.store.exceptions.InvalidRootException;
import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;
import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreStorageFoundationProvider;
import software.xdev.spring.data.eclipse.store.repository.root.EntityData;
import software.xdev.spring.data.eclipse.store.repository.root.VersionedRoot;
import software.xdev.spring.data.eclipse.store.repository.support.SimpleEclipseStoreRepository;
import software.xdev.spring.data.eclipse.store.repository.support.concurrency.ReadWriteLock;
import software.xdev.spring.data.eclipse.store.repository.support.concurrency.ReentrantJavaReadWriteLock;
import software.xdev.spring.data.eclipse.store.repository.support.copier.version.EntityVersionIncrementer;
import software.xdev.spring.data.eclipse.store.repository.support.copier.version.VersionManager;
import software.xdev.spring.data.eclipse.store.repository.support.copier.version.VersionManagerProvider;
import software.xdev.spring.data.eclipse.store.repository.support.id.IdManager;
import software.xdev.spring.data.eclipse.store.repository.support.id.IdManagerProvider;
import software.xdev.spring.data.eclipse.store.repository.support.id.IdSetter;
import software.xdev.spring.data.eclipse.store.repository.support.reposyncer.RepositorySynchronizer;
import software.xdev.spring.data.eclipse.store.repository.support.reposyncer.SimpleRepositorySynchronizer;


public class EclipseStoreStorage
	implements EntityListProvider, IdManagerProvider, VersionManagerProvider, PersistableChecker, ObjectSwizzling
{
	private static final Logger LOG = LoggerFactory.getLogger(EclipseStoreStorage.class);
	private final Map<Class<?>, SimpleEclipseStoreRepository<?, ?>> entityClassToRepository = new HashMap<>();
	/**
	 * "Why are the IdManagers seperated from the repositories?" - Because there might be entities for which there are
	 * no repositories, but they still have IDs.
	 */
	private final Map<Class<?>, IdManager<?, ?>> idManagers = new ConcurrentHashMap<>();
	/**
	 * "Why are the VersionManagers seperated from the repositories?" - Because there might be entities for which there
	 * are no repositories, but they still have Versions.
	 */
	private final Map<Class<?>, VersionManager<?>> versionManagers = new ConcurrentHashMap<>();
	private final EclipseStoreStorageFoundationProvider foundationProvider;
	private EntitySetCollector entitySetCollector;
	private PersistableChecker persistenceChecker;
	private EmbeddedStorageManager storageManager;
	private VersionedRoot root;
	
	private final WorkingCopyRegistry registry = new WorkingCopyRegistry();
	private final ReadWriteLock readWriteLock = new ReentrantJavaReadWriteLock();
	private RepositorySynchronizer repositorySynchronizer;
	
	public EclipseStoreStorage(final EclipseStoreClientConfiguration storeConfiguration)
	{
		this.foundationProvider = storeConfiguration;
	}
	
	private StorageManager getInstanceOfStorageManager()
	{
		this.ensureEntitiesInRoot();
		return this.storageManager;
	}
	
	public WorkingCopyRegistry getRegistry()
	{
		return this.registry;
	}
	
	private synchronized void ensureEntitiesInRoot()
	{
		if(this.storageManager == null)
		{
			final EmbeddedStorageFoundation<?> embeddedStorageFoundation =
				this.startStorageManager();
			this.persistenceChecker = new RelayedPersistenceChecker(embeddedStorageFoundation);
			this.initRoot();
			LOG.info(
				"Storage started with {} entity lists and {} entities.",
				this.root.getCurrentRootData().getEntityTypesCount(),
				this.root.getCurrentRootData().getEntityCount()
			);
			EclipseStoreMigrator.migrate(this.root, this.storageManager);
		}
	}
	
	private EmbeddedStorageFoundation<?> startStorageManager()
	{
		LOG.info("Starting storage...");
		final EmbeddedStorageFoundation<?> embeddedStorageFoundation =
			this.foundationProvider.createEmbeddedStorageFoundation();
		embeddedStorageFoundation.registerTypeHandler(BinaryHandlerImmutableCollectionsSet12.New());
		embeddedStorageFoundation.registerTypeHandler(BinaryHandlerImmutableCollectionsList12.New());
		final EmbeddedStorageManager embeddedStorageManager = embeddedStorageFoundation.start();
		if(embeddedStorageManager.root() != null)
		{
			if(embeddedStorageManager.root() instanceof final Root oldRoot)
			{
				embeddedStorageManager.setRoot(new VersionedRoot(oldRoot));
				embeddedStorageManager.storeRoot();
			}
			else if(!(embeddedStorageManager.root() instanceof VersionedRoot))
			{
				throw new InvalidRootException(
					"Root object of type %s is invalid."
						.formatted(embeddedStorageManager.root()
							.getClass()
							.getName()
						)
				);
			}
		}
		else
		{
			embeddedStorageManager.setRoot(new VersionedRoot());
			embeddedStorageManager.storeRoot();
		}
		this.root = (VersionedRoot)embeddedStorageManager.root();
		this.storageManager = embeddedStorageManager;
		return embeddedStorageFoundation;
	}
	
	public <T> SimpleEclipseStoreRepository<T, ?> getRepository(final Class<T> entityClass)
	{
		return (SimpleEclipseStoreRepository<T, ?>)this.entityClassToRepository.get(entityClass);
	}
	
	private void initRoot()
	{
		if(LOG.isDebugEnabled())
		{
			LOG.debug("Initializing entity lists...");
		}
		this.repositorySynchronizer =
			new SimpleRepositorySynchronizer(this.root.getCurrentRootData());
		boolean entityListMustGetStored = false;
		for(final Class<?> entityClass : this.entityClassToRepository.keySet())
		{
			if(this.root.getCurrentRootData().getEntityData(entityClass) == null)
			{
				this.createNewEntityList(entityClass);
				entityListMustGetStored = true;
			}
		}
		if(entityListMustGetStored)
		{
			this.storageManager.store(this.root.getCurrentRootData().getEntityLists());
		}
		this.entitySetCollector =
			new EntitySetCollector(
				this.root.getCurrentRootData()::getEntityData,
				this.entityClassToRepository.keySet());
		if(LOG.isDebugEnabled())
		{
			LOG.debug("Done initializing entity lists.");
		}
	}
	
	private <T, ID> void createNewEntityList(final Class<T> entityClass)
	{
		final IdManager<T, ID> idManager = this.ensureIdManager(entityClass);
		this.root.getCurrentRootData().createNewEntityList(entityClass, e -> idManager.getId(e));
	}
	
	public synchronized <T> void registerEntity(
		final Class<T> classToRegister,
		final SimpleEclipseStoreRepository<T, ?> repository)
	{
		if(this.entityClassToRepository.containsKey(classToRegister))
		{
			throw new AlreadyRegisteredException(classToRegister.getSimpleName());
		}
		this.entityClassToRepository.put(classToRegister, repository);
		
		// If the storage is running and a new entity is registered, we need to stop the storage to restart
		// again with the registered entity.
		if(this.storageManager != null)
		{
			this.stop();
		}
	}
	
	private <T, ID> EntityData<T, ID> getEntityData(final Class<T> clazz)
	{
		this.ensureEntitiesInRoot();
		return this.readWriteLock.read(
			() -> this.root.getCurrentRootData().getEntityData(clazz)
		);
	}
	
	@Override
	public <T, ID> EntityProvider<T, ID> getEntityProvider(final Class<T> clazz)
	{
		this.ensureEntitiesInRoot();
		return this.entitySetCollector.getRelatedIdentitySets(clazz);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> long getEntityCount(final Class<T> clazz)
	{
		this.ensureEntitiesInRoot();
		return this.readWriteLock.read(
			() ->
			{
				final EntityData<T, Object> entityData = this.root.getCurrentRootData().getEntityData(clazz);
				return entityData == null ? 0 : entityData.getEntityCount();
			}
		);
	}
	
	public <T> void store(
		final Collection<Object> nonEntitiesToStore,
		final Class<T> clazz,
		final Iterable<T> entitiesToStore)
	{
		this.ensureEntitiesInRoot();
		this.readWriteLock.write(
			() ->
			{
				final Collection<Object> entitiesAndPossiblyNonEntitiesToStore =
					this.collectRootEntitiesToStore(clazz, entitiesToStore);
				entitiesAndPossiblyNonEntitiesToStore.addAll(nonEntitiesToStore);
				if(LOG.isDebugEnabled())
				{
					LOG.debug("Collected {} objects store in total.", entitiesAndPossiblyNonEntitiesToStore.size());
				}
				final Storer storer = this.storageManager.createLazyStorer();
				storer.storeAll(entitiesAndPossiblyNonEntitiesToStore);
				storer.commit();
				if(LOG.isDebugEnabled())
				{
					LOG.debug("Done storing {} entities...", entitiesAndPossiblyNonEntitiesToStore.size());
				}
			}
		);
	}
	
	/**
	 * Also collects the object-list to store, if necessary.
	 */
	private <T, ID> Collection<Object> collectRootEntitiesToStore(
		final Class<T> clazz,
		final Iterable<T> entitiesToStore)
	{
		final EntityData<T, ID> entityData = this.getEntityData(clazz);
		final Collection<Object> objectsToStore = new ArrayList<>();
		for(final T entityToStore : entitiesToStore)
		{
			objectsToStore.addAll(entityData.ensureEntityAndReturnObjectsToStore(entityToStore));
			objectsToStore.add(entityToStore);
			// Add the separate lists of entities to store.
			this.repositorySynchronizer.syncAndReturnChangedObjectLists(entityToStore).forEach(
				changedEntityList -> objectsToStore.addAll(changedEntityList.getObjectsToStore())
			);
		}
		return objectsToStore;
	}
	
	public <T, ID> void delete(final Class<T> clazz, final T entityToRemove)
	{
		this.ensureEntitiesInRoot();
		this.readWriteLock.write(
			() ->
			{
				final EntityData<T, ID> entityData = this.getEntityData(clazz);
				this.storageManager.storeAll(entityData.removeEntityAndReturnObjectsToStore(entityToRemove));
				if(LOG.isDebugEnabled())
				{
					LOG.debug("Deleted single entity of class {}.", clazz.getSimpleName());
				}
			}
		);
	}
	
	public <T, ID> void deleteAll(final Class<T> clazz)
	{
		this.ensureEntitiesInRoot();
		this.readWriteLock.write(
			() ->
			{
				final EntityData<T, ID> entityData = this.getEntityData(clazz);
				final long oldSize = entityData.getEntityCount();
				this.storageManager.storeAll(entityData.removeAllEntitiesAndReturnObjectsToStore());
				if(LOG.isDebugEnabled())
				{
					LOG.debug("Deleted {} entities of class {}.", oldSize, clazz.getSimpleName());
				}
			}
		);
	}
	
	public void clearData()
	{
		this.ensureEntitiesInRoot();
		this.readWriteLock.write(
			() ->
			{
				this.root = new VersionedRoot();
				final StorageManager instanceOfstorageManager = this.getInstanceOfStorageManager();
				this.initRoot();
				
				instanceOfstorageManager.setRoot(this.root);
				instanceOfstorageManager.storeRoot();
				if(LOG.isDebugEnabled())
				{
					LOG.debug("Cleared all entities.");
				}
			}
		);
	}
	
	/**
	 * Starts the storage.
	 */
	public void start()
	{
		this.ensureEntitiesInRoot();
	}
	
	/**
	 * Stops the storage.
	 */
	public synchronized void stop()
	{
		this.readWriteLock.write(
			() ->
			{
				LOG.info("Stopping storage...");
				if(this.storageManager != null)
				{
					this.storageManager.shutdown();
					this.storageManager = null;
					this.root = null;
					this.registry.reset();
					this.idManagers.clear();
					this.versionManagers.clear();
					LOG.info("Stopped storage.");
				}
				else
				{
					LOG.info("No storage is running. Nothing to stop.");
				}
			}
		);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T, ID> IdManager<T, ID> ensureIdManager(final Class<T> classPossiblyWithId)
	{
		return (IdManager<T, ID>)this.idManagers.computeIfAbsent(
			classPossiblyWithId,
			clazz ->
				new IdManager<>(
					classPossiblyWithId,
					(IdSetter<T>)IdSetter.createIdSetter(
						clazz,
						id -> this.setLastId(classPossiblyWithId, id),
						() -> this.getLastId(classPossiblyWithId)
					),
					this
				)
		);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> VersionManager<T> ensureVersionManager(final Class<T> possiblyVersionedClass)
	{
		return (VersionManager<T>)this.versionManagers.computeIfAbsent(
			possiblyVersionedClass,
			clazz ->
				new VersionManager<>(
					possiblyVersionedClass,
					EntityVersionIncrementer.createVersionSetter(possiblyVersionedClass)
				)
		);
	}
	
	public Object getLastId(final Class<?> entityClass)
	{
		return this.readWriteLock.read(() -> this.root.getCurrentRootData().getLastId(entityClass));
	}
	
	public void setLastId(final Class<?> entityClass, final Object lastId)
	{
		this.readWriteLock.write(
			() ->
			{
				final EntityData<?, Object> entityData = this.root.getCurrentRootData().getEntityData(entityClass);
				if(entityData == null)
				{
					this.createNewEntityList(entityClass);
					this.storageManager.store(this.root.getCurrentRootData().getEntityLists());
				}
				this.root.getCurrentRootData().setLastId(entityClass, lastId);
				this.storageManager.store(this.root.getCurrentRootData().getObjectsToStoreAfterNewLastId(entityClass));
			}
		);
	}
	
	@Override
	public boolean isPersistable(final Class<?> clazz)
	{
		this.ensureEntitiesInRoot();
		return this.persistenceChecker.isPersistable(clazz);
	}
	
	@Override
	public Object getObject(final long objectId)
	{
		this.ensureEntitiesInRoot();
		return this.storageManager.getObject(objectId);
	}
	
	public ReadWriteLock getReadWriteLock()
	{
		return this.readWriteLock;
	}
}
