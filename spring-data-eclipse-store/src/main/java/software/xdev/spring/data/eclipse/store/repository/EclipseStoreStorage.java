/*
 * Copyright © 2023 XDEV Software (https://xdev.software)
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
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.serializer.persistence.binary.jdk17.java.util.BinaryHandlerImmutableCollectionsList12;
import org.eclipse.serializer.persistence.binary.jdk17.java.util.BinaryHandlerImmutableCollectionsSet12;
import org.eclipse.serializer.persistence.types.Storer;
import org.eclipse.store.integrations.spring.boot.types.EclipseStoreProvider;
import org.eclipse.store.integrations.spring.boot.types.configuration.EclipseStoreProperties;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageFoundation;
import org.eclipse.store.storage.types.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import software.xdev.spring.data.eclipse.store.core.IdentitySet;
import software.xdev.spring.data.eclipse.store.exceptions.AlreadyRegisteredException;
import software.xdev.spring.data.eclipse.store.repository.support.copier.id.IdSetter;
import software.xdev.spring.data.eclipse.store.repository.support.reposyncer.RepositorySynchronizer;
import software.xdev.spring.data.eclipse.store.repository.support.reposyncer.SimpleRepositorySynchronizer;


@Component
public class EclipseStoreStorage
	implements EntityListProvider, IdSetterProvider, PersistableChecker
{
	private static final Logger LOG = LoggerFactory.getLogger(EclipseStoreStorage.class);
	private final Map<Class<?>, String> entityClassToRepositoryName = new HashMap<>();
	private final Map<Class<?>, IdSetter<?>> entityClassToIdSetter = new HashMap<>();
	private EntitySetCollector entitySetCollector;
	private PersistableChecker persistenceChecker;
	private final EclipseStoreProperties storeConfiguration;
	private final EclipseStoreProvider storeProvider;
	
	private StorageManager storageManager;
	private Root root;
	
	private final WorkingCopyRegistry registry = new WorkingCopyRegistry();
	private RepositorySynchronizer repositorySynchronizer;
	
	@Autowired
	public EclipseStoreStorage(
		final EclipseStoreProperties storeConfiguration,
		final EclipseStoreProvider storeProvider)
	{
		this.storeConfiguration = storeConfiguration;
		this.storeProvider = storeProvider;
	}
	
	private synchronized StorageManager getInstanceOfStorageManager()
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
				this.storeProvider.createStorageFoundation(this.storeConfiguration);
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
	
	private void initRoot()
	{
		if(LOG.isDebugEnabled())
		{
			LOG.debug("Initializing entity lists...");
		}
		this.repositorySynchronizer =
			new SimpleRepositorySynchronizer(this.entityClassToRepositoryName, this.root);
		boolean entityListMustGetStored = false;
		for(final String entityName : this.entityClassToRepositoryName.values())
		{
			if(!this.root.getEntityLists().containsKey(entityName))
			{
				this.root.getEntityLists().put(entityName, new IdentitySet<>());
				entityListMustGetStored = true;
			}
		}
		if(entityListMustGetStored)
		{
			this.storageManager.store(this.root.getEntityLists());
		}
		this.entitySetCollector = new EntitySetCollector(this.root.getEntityLists(), this.entityClassToRepositoryName);
		if(LOG.isDebugEnabled())
		{
			LOG.debug("Done initializing entity lists.");
		}
	}
	
	public synchronized <T> void registerEntity(final Class<T> classToRegister)
	{
		final String entityName = this.getEntityName(classToRegister);
		if(this.entityClassToRepositoryName.containsKey(classToRegister))
		{
			throw new AlreadyRegisteredException(entityName);
		}
		this.entityClassToRepositoryName.put(classToRegister, entityName);
	}
	
	private <T> String getEntityName(final Class<T> classToRegister)
	{
		return classToRegister.getName();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public synchronized <T> IdentitySet<T> getEntityList(final Class<T> clazz)
	{
		this.ensureEntitiesInRoot();
		return (IdentitySet<T>)this.root.getEntityLists().get(this.getEntityName(clazz));
	}
	
	public synchronized <T> void store(
		final Collection<Object> nonEntitiesToStore,
		final Class<T> clazz,
		final Iterable<T> entitiesToStore)
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
	
	/**
	 * Also collects the object-list to store, if necessary.
	 */
	private <T> Collection<Object> collectRootEntitiesToStore(final Class<T> clazz, final Iterable<T> entitiesToStore)
	{
		final List<IdentitySet<Object>> entityLists =
			this.entitySetCollector.getRelatedIdentitySets(clazz);
		final Collection<Object> objectsToStore = new ArrayList<>();
		for(final T entityToStore : entitiesToStore)
		{
			entityLists.forEach(
				relatedIdentitySet ->
				{
					if(!relatedIdentitySet.contains(entityToStore))
					{
						relatedIdentitySet.add(entityToStore);
						objectsToStore.add(relatedIdentitySet.getInternalMap());
					}
				}
			);
			objectsToStore.add(entityToStore);
			// Add the separate lists of entities to store.
			this.repositorySynchronizer.syncAndReturnChangedObjectLists(entityToStore).forEach(
				changedEntityList -> objectsToStore.add(changedEntityList.getInternalMap())
			);
		}
		return objectsToStore;
	}
	
	public synchronized <T> void delete(final Class<T> clazz, final T objectToRemove)
	{
		final List<IdentitySet<Object>> entityLists =
			this.entitySetCollector.getRelatedIdentitySets(clazz);
		entityLists.forEach(entityList ->
		{
			entityList.remove(objectToRemove);
			this.storageManager.store(entityList.getInternalMap());
		});
		if(LOG.isDebugEnabled())
		{
			LOG.debug("Deleted single entity of class {}.", clazz.getSimpleName());
		}
	}
	
	public synchronized <T> void deleteAll(final Class<T> clazz)
	{
		this.ensureEntitiesInRoot();
		final IdentitySet<?> entities = this.root.getEntityLists().get(this.getEntityName(clazz));
		final int oldSize = entities.size();
		final List<?> entitiesToRemove = entities.stream().toList();
		final List<IdentitySet<Object>> entityLists =
			this.entitySetCollector.getRelatedIdentitySets(clazz);
		entityLists.forEach(entityList ->
		{
			entityList.removeAll(entitiesToRemove);
			this.storageManager.store(entityList.getInternalMap());
		});
		if(LOG.isDebugEnabled())
		{
			LOG.debug("Deleted {} entities of class {}.", oldSize, clazz.getSimpleName());
		}
	}
	
	public synchronized void clearData()
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
	
	public synchronized void stop()
	{
		LOG.info("Stopping storage...");
		if(this.storageManager != null)
		{
			this.storageManager.shutdown();
			this.storageManager = null;
			this.root = null;
			this.registry.reset();
			this.entityClassToIdSetter.clear();
			LOG.info("Stopped storage.");
		}
		else
		{
			LOG.info("No storage is running. Nothing to stop.");
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> IdSetter<T> ensureIdSetter(final Class<T> domainClass)
	{
		return (IdSetter<T>)this.entityClassToIdSetter.computeIfAbsent(
			domainClass,
			clazz ->
			{
				final String entityName = this.getEntityName(domainClass);
				final Consumer<Object> idSetter = id ->
				{
					this.ensureEntitiesInRoot();
					this.root.getLastIds().put(entityName, id);
					this.storageManager.store(this.root.getLastIds());
				};
				final Supplier<Object> idGetter = () ->
				{
					this.ensureEntitiesInRoot();
					return this.root.getLastIds().get(entityName);
				};
				return IdSetter.createIdSetter(domainClass, idSetter, idGetter);
			}
		);
	}
	
	@Override
	public boolean isPersistable(final Class<?> clazz)
	{
		this.ensureEntitiesInRoot();
		return this.persistenceChecker.isPersistable(clazz);
	}
}
