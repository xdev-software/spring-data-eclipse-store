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
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.custom.model.CustomerWithIdCompositeKey;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.custom.model.CustomerWithIdCompositeKeyRepository;
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
	<T, ID> void createSingleWithAutoIdInteger(
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
}
