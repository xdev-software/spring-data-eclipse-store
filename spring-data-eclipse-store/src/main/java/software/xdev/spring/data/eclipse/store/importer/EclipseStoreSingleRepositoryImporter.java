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
package software.xdev.spring.data.eclipse.store.importer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.EntityType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;
import software.xdev.spring.data.eclipse.store.repository.support.SimpleEclipseStoreRepository;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.RecursiveWorkingCopier;


public class EclipseStoreSingleRepositoryImporter
{
	private static final Logger LOG = LoggerFactory.getLogger(EclipseStoreSingleRepositoryImporter.class);
	private final EclipseStoreStorage eclipseStoreStorage;
	private final ApplicationContext applicationContext;
	
	public EclipseStoreSingleRepositoryImporter(
		final EclipseStoreStorage eclipseStoreStorage,
		final ApplicationContext applicationContext)
	{
		this.eclipseStoreStorage = eclipseStoreStorage;
		this.applicationContext = applicationContext;
	}
	
	public void importData()
	{
		LOG.info("Start importing data from JPA Repositories to EclipseStore...");
		final Map<String, EntityManagerFactory> beansOfEms =
			this.applicationContext.getBeansOfType(EntityManagerFactory.class);
		
		// First create all repositories to register them in the EclipseStoreStorage
		final List<EntityManagerFactoryRepositoryListPair> allRepositories =
			beansOfEms
				.values()
				.stream()
				.map(this::createEclipseStoreRepositoriesFromEntityManager)
				.toList();
		LOG.info(String.format("Found %d repositories to export data from.", allRepositories.size()));
		
		// Now copy the data
		allRepositories.forEach(
			entityManagerFactoryRepositoryListPair ->
				entityManagerFactoryRepositoryListPair
					.classRepositoryPairs
					.forEach(
						classRepositoryPair -> copyData(entityManagerFactoryRepositoryListPair, classRepositoryPair)
					)
		);
		LOG.info("Done importing data from JPA Repositories to EclipseStore.");
	}
	
	private static <T> void copyData(
		final EntityManagerFactoryRepositoryListPair entityManagerFactoryRepositoryListPair,
		final ClassRepositoryPair<T> classRepositoryPair)
	{
		final String className = classRepositoryPair.domainClass.getName();
		
		LOG.info(String.format("Loading entities of %s...", className));
		final List<T> existingEntitiesToExport =
			entityManagerFactoryRepositoryListPair.entityManagerFactory.createEntityManager()
				.createQuery("SELECT c FROM " + className + " c")
				.getResultList();
		LOG.info(String.format(
			"Loaded %d entities of type %s to export.",
			existingEntitiesToExport.size(),
			className
		));
		
		LOG.info(String.format(
			"Saving %d entities of type %s to the EclipseStore Repository...",
			existingEntitiesToExport.size(),
			className
		));
		classRepositoryPair.repository.saveAll(existingEntitiesToExport);
		LOG.info(String.format(
			"Done saving entities of type %s. The EclipseStore now holds %d entities of that type.",
			className,
			classRepositoryPair.repository.count()
		));
	}
	
	private EntityManagerFactoryRepositoryListPair createEclipseStoreRepositoriesFromEntityManager(
		final EntityManagerFactory entityManagerFactory)
	{
		final List<ClassRepositoryPair<?>> repositoryList = new ArrayList<>();
		entityManagerFactory.getMetamodel().getEntities().forEach(
			entityType -> this.createRepositoryForType(entityType, repositoryList)
		);
		
		return new EntityManagerFactoryRepositoryListPair(entityManagerFactory, repositoryList);
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
		return new SimpleEclipseStoreRepository<>(
			this.eclipseStoreStorage,
			new RecursiveWorkingCopier<>(
				domainClass,
				this.eclipseStoreStorage.getRegistry(),
				this.eclipseStoreStorage,
				this.eclipseStoreStorage),
			domainClass);
	}
	
	private record EntityManagerFactoryRepositoryListPair(
		EntityManagerFactory entityManagerFactory,
		List<ClassRepositoryPair<?>> classRepositoryPairs
	)
	{
	}
	
	
	private record ClassRepositoryPair<T>(Class<T> domainClass, SimpleEclipseStoreRepository<T, ?> repository)
	{
	}
}
