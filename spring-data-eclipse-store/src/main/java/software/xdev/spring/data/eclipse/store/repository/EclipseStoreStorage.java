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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.serializer.persistence.binary.jdk17.java.util.BinaryHandlerImmutableCollectionsList12;
import org.eclipse.serializer.persistence.binary.jdk17.java.util.BinaryHandlerImmutableCollectionsSet12;
import org.eclipse.serializer.persistence.types.Storer;
import org.eclipse.serializer.reference.ObjectSwizzling;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageFoundation;
import org.eclipse.store.storage.types.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.xdev.spring.data.eclipse.store.core.EntityListProvider;
import software.xdev.spring.data.eclipse.store.core.EntityProvider;
import software.xdev.spring.data.eclipse.store.core.IdentitySet;
import software.xdev.spring.data.eclipse.store.exceptions.AlreadyRegisteredException;
import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;
import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreStorageFoundationProvider;
import software.xdev.spring.data.eclipse.store.repository.support.SimpleEclipseStoreRepository;
import software.xdev.spring.data.eclipse.store.repository.support.concurrency.ReadWriteLock;
import software.xdev.spring.data.eclipse.store.repository.support.concurrency.ReentrantJavaReadWriteLock;
import software.xdev.spring.data.eclipse.store.repository.support.copier.id.IdManager;
import software.xdev.spring.data.eclipse.store.repository.support.copier.id.IdManagerProvider;
import software.xdev.spring.data.eclipse.store.repository.support.copier.id.IdSetter;
import software.xdev.spring.data.eclipse.store.repository.support.copier.version.VersionManager;
import software.xdev.spring.data.eclipse.store.repository.support.copier.version.VersionManagerProvider;
import software.xdev.spring.data.eclipse.store.repository.support.copier.version.VersionSetter;
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
	private final Map<Class<?>, VersionManager<?, ?>> versionManagers = new ConcurrentHashMap<>();
	private final EclipseStoreStorageFoundationProvider foundationProvider;
	private EntitySetCollector entitySetCollector;
	private PersistableChecker persistenceChecker;
	private StorageManager storageManager;
	private Root root;
	
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
			LOG.info("Starting storage...");
			this.root = new Root();
			final EmbeddedStorageFoundation<?> embeddedStorageFoundation =
				this.foundationProvider.createEmbeddedStorageFoundation();
			embeddedStorageFoundation.registerTypeHandler(BinaryHandlerImmutableCollectionsSet12.New());
			embeddedStorageFoundation.registerTypeHandler(BinaryHandlerImmutableCollectionsList12.New());
			this.storageManager = embeddedStorageFoundation.start(this.root);
			this.persistenceChecker = new RelayedPersistenceChecker(embeddedStorageFoundation);
			this.initRoot();
			final Integer entitySum =
				this.root.getEntityLists().values().stream().map(IdentitySet::size).reduce(0, Integer::sum);
			LOG.info(
				"Storage started with {} entity lists and {} entities.",
				this.root.getEntityLists().size(),
				entitySum);
		}
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
			new SimpleRepositorySynchronizer(this.root);
		boolean entityListMustGetStored = false;
		for(final Class<?> entityClass : this.entityClassToRepository.keySet())
		{
			if(this.root.getEntityList(entityClass) == null)
			{
				this.root.createNewEntityList(entityClass);
				entityListMustGetStored = true;
			}
		}
		if(entityListMustGetStored)
		{
			this.storageManager.store(this.root.getEntityLists());
		}
		this.entitySetCollector =
			new EntitySetCollector(this.root::getEntityList, this.entityClassToRepository.keySet());
		if(LOG.isDebugEnabled())
		{
			LOG.debug("Done initializing entity lists.");
		}
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
	
	private <T> IdentitySet<T> getEntityList(final Class<T> clazz)
	{
		this.ensureEntitiesInRoot();
		return this.readWriteLock.read(
			() -> this.root.getEntityList(clazz)
		);
	}
	
	@Override
	public <T> EntityProvider<T> getEntityProvider(final Class<T> clazz)
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
				final IdentitySet<T> entityList = this.root.getEntityList(clazz);
				return entityList == null ? 0 : entityList.size();
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
	private <T> Collection<Object> collectRootEntitiesToStore(final Class<T> clazz, final Iterable<T> entitiesToStore)
	{
		final IdentitySet<T> identitySet = this.getEntityList(clazz);
		final Collection<Object> objectsToStore = new ArrayList<>();
		for(final T entityToStore : entitiesToStore)
		{
			if(!identitySet.contains(entityToStore))
			{
				identitySet.add(entityToStore);
				objectsToStore.add(identitySet.getInternalMap());
			}
			objectsToStore.add(entityToStore);
			// Add the separate lists of entities to store.
			this.repositorySynchronizer.syncAndReturnChangedObjectLists(entityToStore).forEach(
				changedEntityList -> objectsToStore.add(changedEntityList.getInternalMap())
			);
		}
		return objectsToStore;
	}
	
	public <T> void delete(final Class<T> clazz, final T objectToRemove)
	{
		this.ensureEntitiesInRoot();
		this.readWriteLock.write(
			() ->
			{
				final IdentitySet<T> entityList = this.getEntityList(clazz);
				entityList.remove(objectToRemove);
				this.storageManager.store(entityList.getInternalMap());
				if(LOG.isDebugEnabled())
				{
					LOG.debug("Deleted single entity of class {}.", clazz.getSimpleName());
				}
			}
		);
	}
	
	public <T> void deleteAll(final Class<T> clazz)
	{
		this.ensureEntitiesInRoot();
		this.readWriteLock.write(
			() ->
			{
				final IdentitySet<T> entities = this.getEntityList(clazz);
				final int oldSize = entities.size();
				final List<T> entitiesToRemove = entities.stream().toList();
				entities.removeAll(entitiesToRemove);
				this.storageManager.store(entities.getInternalMap());
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
				this.root = new Root();
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
	public <T, VERSION> VersionManager<T, VERSION> ensureVersionManager(final Class<T> possiblyVersionedClass)
	{
		return (VersionManager<T, VERSION>)this.versionManagers.computeIfAbsent(
			possiblyVersionedClass,
			clazz ->
				new VersionManager<T, VERSION>(
					possiblyVersionedClass,
					VersionSetter.createVersionSetter(possiblyVersionedClass)
				)
		);
	}
	
	public Object getLastId(final Class<?> entityClass)
	{
		return this.readWriteLock.read(() -> this.root.getLastId(entityClass));
	}
	
	public void setLastId(final Class<?> entityClass, final Object lastId)
	{
		this.readWriteLock.write(
			() ->
			{
				this.root.setLastId(entityClass, lastId);
				this.storageManager.store(this.root.getLastIds());
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
