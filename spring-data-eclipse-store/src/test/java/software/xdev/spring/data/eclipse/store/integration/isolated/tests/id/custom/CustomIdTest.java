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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.custom;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.IdTestConfiguration;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.custom.model.CompositeKey;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.custom.model.CompositeKeyAsRecord;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.custom.model.CustomerWithIdCompositeKey;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.custom.model.CustomerWithIdCompositeKeyAsRecord;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.custom.model.CustomerWithIdCompositeKeyAsRecordRepository;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.custom.model.CustomerWithIdCompositeKeyEmbeddedId;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.custom.model.CustomerWithIdCompositeKeyEmbeddedIdRepository;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.custom.model.CustomerWithIdCompositeKeyRepository;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.custom.model.CustomerWithIdLocalDate;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.custom.model.CustomerWithIdLocalDateRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository;


@SuppressWarnings("OptionalGetWithoutIsPresent")
@IsolatedTestAnnotations
@ContextConfiguration(classes = {IdTestConfiguration.class})
class CustomIdTest
{
	public static Stream<Arguments> generateData()
	{
		return Stream.of(
			new SingleTestDataset<>(
				id -> new CustomerWithIdCompositeKey(id, TestData.FIRST_NAME),
				context -> context.getBean(CustomerWithIdCompositeKeyRepository.class),
				() -> new CompositeKey(1, 1),
				() -> new CompositeKey(2, 2)
			).toArguments(),
			new SingleTestDataset<>(
				id -> new CustomerWithIdCompositeKeyEmbeddedId(id, TestData.FIRST_NAME),
				context -> context.getBean(CustomerWithIdCompositeKeyEmbeddedIdRepository.class),
				() -> new CompositeKey(1, 1),
				() -> new CompositeKey(2, 2)
			).toArguments(),
			new SingleTestDataset<>(
				id -> new CustomerWithIdCompositeKeyAsRecord(id, TestData.FIRST_NAME),
				context -> context.getBean(CustomerWithIdCompositeKeyAsRecordRepository.class),
				() -> new CompositeKeyAsRecord(1, 1),
				() -> new CompositeKeyAsRecord(2, 2)
			).toArguments(),
			new SingleTestDataset<>(
				id -> new CustomerWithIdLocalDate(id, TestData.FIRST_NAME),
				context -> context.getBean(CustomerWithIdLocalDateRepository.class),
				() -> LocalDate.of(2024, 1, 1),
				() -> LocalDate.of(2024, 1, 2)
			).toArguments()
		);
	}
	
	private final IdTestConfiguration configuration;
	
	@Autowired
	public CustomIdTest(final IdTestConfiguration configuration)
	{
		this.configuration = configuration;
	}
	
	@ParameterizedTest
	@MethodSource("generateData")
	<T, ID> void createSingleWithCustomKey(
		final SingleTestDataset<T, ID> data, @Autowired final ApplicationContext context)
	{
		final EclipseStoreRepository<T, ID> repository = data.repositoryGenerator().apply(context);
		
		final T customer = data.enitityGenerator().apply(data.firstIdSupplier().get());
		repository.save(customer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<T> loadedCustomer = repository.findById(data.firstIdSupplier().get());
				Assertions.assertTrue(loadedCustomer.isPresent());
				Assertions.assertEquals(customer, loadedCustomer.get());
			}
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateData")
	<T, ID> void createDoubleWithCustomKey(
		final SingleTestDataset<T, ID> data, @Autowired final ApplicationContext context)
	{
		final EclipseStoreRepository<T, ID> repository = data.repositoryGenerator().apply(context);
		
		final T customer1 = data.enitityGenerator().apply(data.firstIdSupplier().get());
		repository.save(customer1);
		
		final T customer2 = data.enitityGenerator().apply(data.secondIdSupplier().get());
		repository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				Assertions.assertEquals(2, repository.findAll().size());
				Assertions.assertEquals(
					2,
					repository.findAllById(List.of(data.firstIdSupplier().get(), data.secondIdSupplier().get()))
						.size());
				
				final Optional<T> loadedCustomer1 = repository.findById(data.firstIdSupplier().get());
				Assertions.assertTrue(loadedCustomer1.isPresent());
				Assertions.assertEquals(customer1, loadedCustomer1.get());
				
				final Optional<T> loadedCustomer2 = repository.findById(data.secondIdSupplier().get());
				Assertions.assertTrue(loadedCustomer2.isPresent());
				Assertions.assertEquals(customer2, loadedCustomer2.get());
			}
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateData")
	<T, ID> void createNullWithCustomKey(
		final SingleTestDataset<T, ID> data, @Autowired final ApplicationContext context)
	{
		final EclipseStoreRepository<T, ID> repository = data.repositoryGenerator().apply(context);
		
		final T customer = data.enitityGenerator().apply(null);
		Assertions.assertThrows(IllegalArgumentException.class, () -> repository.save(customer));
	}
	
	@ParameterizedTest
	@MethodSource("generateData")
	<T, ID> void deleteBeforeRestartWithCustomKey(
		final SingleTestDataset<T, ID> data, @Autowired final ApplicationContext context)
	{
		final EclipseStoreRepository<T, ID> repository = data.repositoryGenerator().apply(context);
		
		final T customer = data.enitityGenerator().apply(data.firstIdSupplier().get());
		repository.save(customer);
		
		repository.deleteById(data.firstIdSupplier().get());
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				Assertions.assertTrue(repository.findAll().isEmpty());
				final Optional<T> loadedCustomer = repository.findById(data.firstIdSupplier().get());
				Assertions.assertFalse(loadedCustomer.isPresent());
			}
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateData")
	<T, ID> void deleteAfterRestartWithCustomKey(
		final SingleTestDataset<T, ID> data, @Autowired final ApplicationContext context)
	{
		final EclipseStoreRepository<T, ID> repository = data.repositoryGenerator().apply(context);
		
		final T customer = data.enitityGenerator().apply(data.firstIdSupplier().get());
		repository.save(customer);
		
		TestUtil.restartDatastore(this.configuration);
		
		repository.deleteById(data.firstIdSupplier().get());
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				Assertions.assertTrue(repository.findAll().isEmpty());
				final Optional<T> loadedCustomer = repository.findById(data.firstIdSupplier().get());
				Assertions.assertFalse(loadedCustomer.isPresent());
			}
		);
	}
}
