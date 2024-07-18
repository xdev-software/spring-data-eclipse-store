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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.version;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import jakarta.persistence.OptimisticLockException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.exceptions.InvalidVersionException;
import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {VersionTestConfiguration.class})
class VersionTest
{
	public static Stream<Arguments> generateData()
	{
		return Stream.of(
			new SingleTestDataset<>(
				VersionedEntityWithInteger::new,
				context -> context.getBean(VersionedEntityWithIntegerRepository.class),
				1,
				2
			).toArguments(),
			new SingleTestDataset<>(
				VersionedEntityWithLong::new,
				context -> context.getBean(VersionedEntityWithLongRepository.class),
				1L,
				2L
			).toArguments(),
			new SingleTestDataset<>(
				VersionedEntityWithPrimitiveInteger::new,
				context -> context.getBean(VersionedEntityWithPrimitiveIntegerRepository.class),
				1,
				2
			).toArguments(),
			new SingleTestDataset<>(
				VersionedEntityWithPrimitiveLong::new,
				context -> context.getBean(VersionedEntityWithPrimitiveLongRepository.class),
				1L,
				2L
			).toArguments(),
			new SingleTestDataset<>(
				VersionedEntityWithString::new,
				context -> context.getBean(VersionedEntityWithStringRepository.class),
				null,
				null
			).toArguments(),
			new SingleTestDataset<>(
				VersionedEntityWithUuid::new,
				context -> context.getBean(VersionedEntityWithUuidRepository.class),
				null,
				null
			).toArguments(),
			new SingleTestDataset<>(
				VersionedEntityWithId::new,
				context -> context.getBean(VersionedEntityWithIdRepository.class),
				1,
				2
			).toArguments()
		);
	}
	
	private record SingleTestDataset<T extends VersionedEntity<?>>(
		Function<String, T> enitityGenerator,
		Function<ApplicationContext, EclipseStoreRepository<T, ?>> repositoryGenerator,
		Object firstVersion,
		Object secondVersion
	)
	{
		public Arguments toArguments()
		{
			return Arguments.of(this);
		}
	}
	
	
	private final VersionTestConfiguration configuration;
	
	@Autowired
	public VersionTest(final VersionTestConfiguration configuration)
	{
		this.configuration = configuration;
	}
	
	@ParameterizedTest
	@MethodSource("generateData")
	<T extends VersionedEntity<?>> void simpleSave(
		final SingleTestDataset<T> data, @Autowired final ApplicationContext context)
	{
		final EclipseStoreRepository<T, ?> repository = data.repositoryGenerator.apply(context);
		final T entity = data.enitityGenerator.apply(TestData.FIRST_NAME);
		repository.save(entity);
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<T> allEntities = repository.findAll();
				Assertions.assertEquals(1, allEntities.size());
				if(data.firstVersion != null)
				{
					Assertions.assertEquals(data.firstVersion, allEntities.get(0).getVersion());
				}
				else
				{
					Assertions.assertNotNull(allEntities.get(0).getVersion());
				}
			}
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateData")
	<T extends VersionedEntity<?>> void doubleSave(
		final SingleTestDataset<T> data, @Autowired final ApplicationContext context)
	{
		final EclipseStoreRepository<T, ?> repository = data.repositoryGenerator.apply(context);
		final T entity = data.enitityGenerator.apply(TestData.FIRST_NAME);
		repository.save(entity);
		repository.save(repository.findAll().get(0));
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<T> allEntities = repository.findAll();
				Assertions.assertEquals(1, allEntities.size());
				if(data.secondVersion != null)
				{
					Assertions.assertEquals(data.secondVersion, allEntities.get(0).getVersion());
				}
				else
				{
					Assertions.assertNotNull(allEntities.get(0).getVersion());
				}
			}
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateData")
	<T extends VersionedEntity<?>> void saveButLocked(
		final SingleTestDataset<T> data, @Autowired final ApplicationContext context)
	{
		final EclipseStoreRepository<T, ?> repository = data.repositoryGenerator.apply(context);
		final T entity = data.enitityGenerator.apply(TestData.FIRST_NAME);
		repository.save(entity);
		
		final T firstLoadedEntry = repository.findAll().get(0);
		final T secondLoadedEntry = repository.findAll().get(0);
		
		firstLoadedEntry.setName(TestData.FIRST_NAME_ALTERNATIVE);
		repository.save(firstLoadedEntry);
		
		Assertions.assertThrows(OptimisticLockException.class, () -> repository.save(secondLoadedEntry));
	}
	
	@Test
	void simpleSaveWithChild(@Autowired final VersionedEntityWithIntegerAndVersionedChildRepository repository)
	{
		final VersionedChildEntityWithLong child = new VersionedChildEntityWithLong(TestData.FIRST_NAME);
		final VersionedEntityWithIntegerAndVersionedChild entity =
			new VersionedEntityWithIntegerAndVersionedChild(TestData.FIRST_NAME_ALTERNATIVE, child);
		repository.save(entity);
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<VersionedEntityWithIntegerAndVersionedChild> allEntities = repository.findAll();
				Assertions.assertEquals(1, allEntities.size());
				Assertions.assertEquals(1, allEntities.get(0).getVersion());
				Assertions.assertEquals(1L, allEntities.get(0).getChild().getVersion());
			}
		);
	}
	
	@Test
	void doubleSaveWithChild(@Autowired final VersionedEntityWithIntegerAndVersionedChildRepository repository)
	{
		final VersionedChildEntityWithLong child = new VersionedChildEntityWithLong(TestData.FIRST_NAME);
		final VersionedEntityWithIntegerAndVersionedChild entity =
			new VersionedEntityWithIntegerAndVersionedChild(TestData.FIRST_NAME_ALTERNATIVE, child);
		repository.save(entity);
		repository.save(repository.findAll().get(0));
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<VersionedEntityWithIntegerAndVersionedChild> allEntities = repository.findAll();
				Assertions.assertEquals(1, allEntities.size());
				Assertions.assertEquals(2, allEntities.get(0).getVersion());
				Assertions.assertEquals(2L, allEntities.get(0).getChild().getVersion());
			}
		);
	}
	
	@Test
	void saveButLockedWithChild(@Autowired final VersionedEntityWithIntegerAndVersionedChildRepository repository)
	{
		final VersionedChildEntityWithLong child = new VersionedChildEntityWithLong(TestData.FIRST_NAME);
		final VersionedEntityWithIntegerAndVersionedChild entity =
			new VersionedEntityWithIntegerAndVersionedChild(TestData.FIRST_NAME_ALTERNATIVE, child);
		repository.save(entity);
		
		final VersionedEntityWithIntegerAndVersionedChild firstLoadedEntry = repository.findAll().get(0);
		final VersionedEntityWithIntegerAndVersionedChild secondLoadedEntry = repository.findAll().get(0);
		
		firstLoadedEntry.getChild().setName(TestData.FIRST_NAME_ALTERNATIVE);
		repository.save(firstLoadedEntry);
		
		Assertions.assertThrows(OptimisticLockException.class, () -> repository.save(secondLoadedEntry));
	}
	
	@Test
	void saveButLockedWithSameChild(@Autowired final VersionedEntityWithIntegerAndVersionedChildRepository repository)
	{
		final VersionedChildEntityWithLong child = new VersionedChildEntityWithLong(TestData.FIRST_NAME);
		final VersionedEntityWithIntegerAndVersionedChild entity =
			new VersionedEntityWithIntegerAndVersionedChild(TestData.FIRST_NAME_ALTERNATIVE, child);
		repository.save(entity);
		
		final VersionedEntityWithIntegerAndVersionedChild firstLoadedEntry = repository.findAll().get(0);
		final VersionedEntityWithIntegerAndVersionedChild secondLoadedEntry = repository.findAll().get(0);
		
		firstLoadedEntry.setChild(secondLoadedEntry.getChild());
		repository.save(firstLoadedEntry);
		
		Assertions.assertThrows(OptimisticLockException.class, () -> repository.save(secondLoadedEntry));
	}
	
	@Test
	void replaceWithIdWithNull(@Autowired final VersionedEntityWithIdRepository repository)
	{
		final VersionedEntityWithId existingEntity = new VersionedEntityWithId(TestData.FIRST_NAME);
		repository.save(existingEntity);
		
		final int existingId = repository.findAll().get(0).getId();
		final VersionedEntityWithId nextEntity = new VersionedEntityWithId(
			existingId,
			TestData.FIRST_NAME_ALTERNATIVE);
		
		Assertions.assertThrows(InvalidVersionException.class, () -> repository.save(nextEntity));
	}
	
	@Test
	void replaceWithIdWithDifferentVersion(@Autowired final VersionedEntityWithIdRepository repository)
	{
		final VersionedEntityWithId existingEntity = new VersionedEntityWithId(TestData.FIRST_NAME);
		repository.save(existingEntity);
		
		final VersionedEntityWithId foundEntity = repository.findAll().get(0);
		final VersionedEntityWithId nextEntity = new VersionedEntityWithId(
			foundEntity.getId(),
			TestData.FIRST_NAME_ALTERNATIVE);
		nextEntity.setVersion(foundEntity.getVersion() + 1);
		
		Assertions.assertThrows(OptimisticLockException.class, () -> repository.save(nextEntity));
	}
	
	@Test
	void replaceWithIdWithSameVersion(@Autowired final VersionedEntityWithIdRepository repository)
	{
		final VersionedEntityWithId existingEntity = new VersionedEntityWithId(TestData.FIRST_NAME);
		repository.save(existingEntity);
		
		final VersionedEntityWithId foundEntity = repository.findAll().get(0);
		final VersionedEntityWithId nextEntity = new VersionedEntityWithId(
			foundEntity.getId(),
			TestData.FIRST_NAME_ALTERNATIVE);
		nextEntity.setVersion(foundEntity.getVersion());
		repository.save(nextEntity);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<VersionedEntityWithId> allEntities = repository.findAll();
				Assertions.assertEquals(1, allEntities.size());
				Assertions.assertEquals(2, allEntities.get(0).getVersion());
			}
		);
	}
	
	@Test
	void findById(@Autowired final VersionedEntityWithIdRepository repository)
	{
		final VersionedEntityWithId existingEntity = new VersionedEntityWithId(TestData.FIRST_NAME);
		repository.save(existingEntity);
		
		final int existingId = repository.findAll().get(0).getId();
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				Assertions.assertTrue(repository.findById(existingId).isPresent());
				Assertions.assertEquals(1, repository.findById(existingId).get().getVersion());
				Assertions.assertEquals(existingId, repository.findById(existingId).get().getId());
			}
		);
	}
}
