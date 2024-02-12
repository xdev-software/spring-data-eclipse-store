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
package software.xdev.spring.data.eclipse.store.integration.isolatet.tests.special.types;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolatet.tests.IsolatedTestAnnotations;
import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository;


@IsolatedTestAnnotations
@TestPropertySource(
	properties =
		"org.eclipse.store.storage-directory=./target/isolated-tests-storage"
)
class TypesTest
{
	public static final String TYPES_DATA_SOURCE =
		"software.xdev.spring.data.eclipse.store.integration.isolatet.tests.special.types"
			+ ".TypesData#generateData";
	
	@Autowired
	private EclipseStoreStorage storage;
	
	@ParameterizedTest
	@MethodSource(TYPES_DATA_SOURCE)
	@Disabled("Should get fixed")
	<T> void simpleStoreAndRead(
		final Class<? extends EclipseStoreRepository<T, Integer>> repositoryClass,
		final Function<Integer, T> objectCreator,
		final Consumer<T> objectChanger,
		@Autowired final ApplicationContext context)
	{
		final EclipseStoreRepository<T, Integer> repository = context.getBean(repositoryClass);
		final T objectToStore = objectCreator.apply(1);
		repository.save(objectToStore);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final List<T> customers = TestUtil.iterableToList(repository.findAll());
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(objectToStore, customers.get(0));
			}
		);
	}
	
	@ParameterizedTest
	@MethodSource(TYPES_DATA_SOURCE)
	@Disabled("Should get fixed")
	<T extends ComplexObject> void simpleChangeToNullAfterStore(
		final Class<? extends EclipseStoreRepository<T, Integer>> repositoryClass,
		final Function<Integer, T> objectCreator,
		final Consumer<T> objectChanger,
		@Autowired final ApplicationContext context)
	{
		this.simpleChangeAfterStore(repositoryClass, objectCreator, object -> object.setValue(null), context);
	}
	
	@ParameterizedTest
	@MethodSource(TYPES_DATA_SOURCE)
	@Disabled("Should get fixed")
	<T extends ComplexObject> void simpleChangeAfterStore(
		final Class<? extends EclipseStoreRepository<T, Integer>> repositoryClass,
		final Function<Integer, T> objectCreator,
		final Consumer<T> objectChanger,
		@Autowired final ApplicationContext context)
	{
		final EclipseStoreRepository<T, Integer> repository = context.getBean(repositoryClass);
		final T objectToStore = objectCreator.apply(2);
		repository.save(objectToStore);
		
		final Optional<T> storedObject = repository.findById(2);
		Assertions.assertTrue(storedObject.isPresent());
		
		objectChanger.accept(storedObject.get());
		repository.save(storedObject.get());
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final Optional<T> storedObject2 = repository.findById(2);
				Assertions.assertTrue(storedObject2.isPresent());
				Assertions.assertEquals(storedObject, storedObject2);
			}
		);
	}
	
	@ParameterizedTest
	@MethodSource(TYPES_DATA_SOURCE)
	@Disabled("Should get fixed")
	<T extends ComplexObject> void simpleChangeBeforeStore(
		final Class<? extends EclipseStoreRepository<T, Integer>> repositoryClass,
		final Function<Integer, T> objectCreator,
		final Consumer<T> objectChanger,
		@Autowired final ApplicationContext context)
	{
		final EclipseStoreRepository<T, Integer> repository = context.getBean(repositoryClass);
		final T objectToStore = objectCreator.apply(3);
		objectChanger.accept(objectToStore);
		repository.save(objectToStore);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final Optional<T> storedObject2 = repository.findById(3);
				Assertions.assertTrue(storedObject2.isPresent());
				Assertions.assertEquals(objectToStore, storedObject2.get());
			}
		);
	}
}
