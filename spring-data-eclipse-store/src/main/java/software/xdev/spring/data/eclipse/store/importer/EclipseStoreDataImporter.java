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
package software.xdev.spring.data.eclipse.store.importer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.EntityType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;
import software.xdev.spring.data.eclipse.store.repository.SupportedChecker;
import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;
import software.xdev.spring.data.eclipse.store.repository.support.SimpleEclipseStoreRepository;
import software.xdev.spring.data.eclipse.store.repository.support.copier.id.IdManager;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.RecursiveWorkingCopier;
import software.xdev.spring.data.eclipse.store.transactions.EclipseStoreTransactionManager;


/**
 * Imports entities from {@link EntityManagerFactory}s into the EclipseStore storage.
 */
public class EclipseStoreDataImporter
{
	private static final Logger LOG = LoggerFactory.getLogger(EclipseStoreDataImporter.class);
	private final EclipseStoreClientConfiguration configuration;
	
	public EclipseStoreDataImporter(final EclipseStoreClientConfiguration configuration)
	{
		this.configuration = configuration;
	}
	
	/**
	 * Imports entities from all given {@link EntityManagerFactory}s that are available into the EclipseStore storage.
	 * <p>
	 * This should be done only once. Otherwise entities may be imported multiple times.
	 * </p>
	 * <p>
	 * After importing all the entities, the existing repositories should be converted to
	 * {@link software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository}.
	 * </p>
	 *
	 * @param entityManagerFactories which are searched for entities
	 * @return all the newly created {@link SimpleEclipseStoreRepository} for the specific entities.
	 */
	@SuppressWarnings("java:S1452")
	public List<SimpleEclipseStoreRepository<?, ?>> importData(final EntityManagerFactory... entityManagerFactories)
	{
		return this.importData(Arrays.stream(entityManagerFactories));
	}
	
	/**
	 * Imports entities from all given {@link EntityManagerFactory}s that are available into the EclipseStore storage.
	 * <p>
	 * This should be done only once. Otherwise entities may be imported multiple times.
	 * </p>
	 * <p>
	 * After importing all the entities, the existing repositories should be converted to
	 * {@link software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository}.
	 * </p>
	 *
	 * @param entityManagerFactories which are searched for entities
	 * @return all the newly created {@link SimpleEclipseStoreRepository} for the specific entities.
	 */
	@SuppressWarnings("java:S1452")
	public List<SimpleEclipseStoreRepository<?, ?>> importData(
		final Iterable<EntityManagerFactory> entityManagerFactories
	)
	{
		return this.importData(StreamSupport.stream(entityManagerFactories.spliterator(), false));
	}
	
	/**
	 * Imports entities from all given {@link EntityManagerFactory}s that are available into the EclipseStore storage.
	 * <p>
	 * This should be done only once. Otherwise entities may be imported multiple times.
	 * </p>
	 * <p>
	 * After importing all the entities, the existing repositories should be converted to
	 * {@link software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository}.
	 * </p>
	 *
	 * @param entityManagerFactories which are searched for entities
	 * @return all the newly created {@link SimpleEclipseStoreRepository} for the specific entities.
	 */
	@SuppressWarnings({"java:S1452", "java:S6204"})
	public List<SimpleEclipseStoreRepository<?, ?>> importData(
		final Stream<EntityManagerFactory> entityManagerFactories
	)
	{
		LOG.info("Start importing data from JPA Repositories to EclipseStore...");
		
		// First create all repositories to register them in the EclipseStoreStorage
		final List<EntityManagerSupplierRepositoryListPair> allRepositories =
			entityManagerFactories
				.map(this::createEclipseStoreRepositoriesFromEntityManagerFactory)
				.toList();
		LOG.info("Found {} repositories to export data from.", allRepositories.size());
		
		return this.importData(allRepositories);
	}
	
	/**
	 * Imports entities from all given {@link EntityManager}s that are available into the EclipseStore storage.
	 * <p>
	 * This should be done only once. Otherwise entities may be imported multiple times.
	 * </p>
	 * <p>
	 * After importing all the entities, the existing repositories should be converted to
	 * {@link software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository}.
	 * </p>
	 *
	 * @param entityManagers which are searched for entities
	 * @return all the newly created {@link SimpleEclipseStoreRepository} for the specific entities.
	 */
	@SuppressWarnings({"java:S1452", "java:S6204"})
	public List<SimpleEclipseStoreRepository<?, ?>> importData(
		final EntityManager... entityManagers
	)
	{
		LOG.info("Start importing data from JPA Repositories to EclipseStore...");
		
		// First create all repositories to register them in the EclipseStoreStorage
		final List<EntityManagerSupplierRepositoryListPair> allRepositories =
			Arrays.stream(entityManagers)
				.map(this::createEclipseStoreRepositoriesFromEntityManager)
				.toList();
		LOG.info("Found {} repositories to export data from.", allRepositories.size());
		
		return this.importData(allRepositories);
	}
	
	private List<SimpleEclipseStoreRepository<?, ?>> importData(
		final List<EntityManagerSupplierRepositoryListPair> allRepositories
	)
	{
		allRepositories.forEach(
			entityManagerSupplierRepositoryListPair ->
				entityManagerSupplierRepositoryListPair
					.classRepositoryPairs
					.forEach(
						classRepositoryPair ->
							this.copyData(entityManagerSupplierRepositoryListPair, classRepositoryPair)
					)
		);
		LOG.info("Done importing data from JPA Repositories to EclipseStore.");
		
		return allRepositories
			.stream()
			.map(EntityManagerSupplierRepositoryListPair::classRepositoryPairs)
			.flatMap(List::stream)
			.map(ClassRepositoryPair::repository)
			.collect(Collectors.toList());
	}
	
	private <T> void copyData(
		final EntityManagerSupplierRepositoryListPair entityManagerSupplierRepositoryListPair,
		final ClassRepositoryPair<T> classRepositoryPair)
	{
		try(final EntityManager entityManager = entityManagerSupplierRepositoryListPair.entityManagerSupplier().get())
		{
			this.copyData(entityManager, classRepositoryPair);
		}
	}
	
	private <T> void copyData(
		final EntityManager entityManager,
		final ClassRepositoryPair<T> classRepositoryPair
	)
	{
		final String className = classRepositoryPair.domainClass.getName();
		
		LOG.info("Loading entities of {}...", className);
		final List<T> existingEntitiesToExport =
			entityManager
				.createQuery(
					"SELECT c FROM " + className + " c",
					classRepositoryPair.domainClass
				)
				.getResultList();
		
		LOG.info(
			"Loaded {} entities of type {} to export.",
			existingEntitiesToExport.size(),
			className
		);
		
		LOG.info(
			"Saving {} entities of type {} to the EclipseStore Repository...",
			existingEntitiesToExport.size(),
			className
		);
		classRepositoryPair.repository.saveAll(existingEntitiesToExport);
		LOG.info(
			"Done saving entities of type {}. The EclipseStore now holds {} entities of that type.",
			className,
			classRepositoryPair.repository.count()
		);
	}
	
	private EntityManagerSupplierRepositoryListPair createEclipseStoreRepositoriesFromEntityManagerFactory(
		final EntityManagerFactory entityManagerFactory)
	{
		final List<ClassRepositoryPair<?>> repositoryList = new ArrayList<>();
		entityManagerFactory.getMetamodel().getEntities().forEach(
			entityType -> this.createRepositoryForType(entityType, repositoryList)
		);
		
		return new EntityManagerSupplierRepositoryListPair(
			() -> entityManagerFactory.createEntityManager(),
			repositoryList);
	}
	
	private EntityManagerSupplierRepositoryListPair createEclipseStoreRepositoriesFromEntityManager(
		final EntityManager entityManager)
	{
		final List<ClassRepositoryPair<?>> repositoryList = new ArrayList<>();
		entityManager.getMetamodel().getEntities().forEach(
			entityType -> this.createRepositoryForType(entityType, repositoryList)
		);
		
		return new EntityManagerSupplierRepositoryListPair(()->entityManager, repositoryList);
	}
	
	private <T> void createRepositoryForType(
		final EntityType<T> entityType,
		final List<ClassRepositoryPair<?>> repositoryList)
	{
		final Class<T> javaType = entityType.getJavaType();
		repositoryList.add(new ClassRepositoryPair<>(javaType, this.createEclipseStoreRepo(javaType)));
	}
	
	private <T> SimpleEclipseStoreRepository<T, ?> createEclipseStoreRepo(final Class<T> domainClass)
	{
		final EclipseStoreStorage storageInstance = this.configuration.getStorageInstance();
		final IdManager<T, Object> idManager = storageInstance.ensureIdManager(domainClass);
		return new SimpleEclipseStoreRepository<>(
			storageInstance,
			new RecursiveWorkingCopier<>(
				domainClass,
				storageInstance.getRegistry(),
				storageInstance,
				storageInstance,
				storageInstance,
				new SupportedChecker.Implementation(),
				storageInstance
			),
			domainClass,
			new EclipseStoreTransactionManager(),
			idManager
		);
	}
	
	private record EntityManagerSupplierRepositoryListPair(
		Supplier<EntityManager> entityManagerSupplier,
		List<ClassRepositoryPair<?>> classRepositoryPairs
	)
	{
	}
	
	
	private record ClassRepositoryPair<T>(Class<T> domainClass, SimpleEclipseStoreRepository<T, ?> repository)
	{
	}
}
