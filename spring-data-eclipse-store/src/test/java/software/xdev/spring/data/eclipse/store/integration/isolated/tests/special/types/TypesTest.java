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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.special.types;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.exceptions.DataTypeNotSupportedException;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {SpecialTypesTestConfiguration.class})
class TypesTest
{
	public static final String TYPES_DATA_SOURCE =
		"software.xdev.spring.data.eclipse.store.integration.isolated.tests.special.types"
			+ ".TypesData#generateData";
	public static final String TYPES_NOT_WORKING_DATA_SOURCE =
		"software.xdev.spring.data.eclipse.store.integration.isolated.tests.special.types"
			+ ".TypesData#generateNotWorkingData";
	
	@Autowired
	private SpecialTypesTestConfiguration configuration;
	
	@ParameterizedTest
	@MethodSource(TYPES_DATA_SOURCE)
	<T extends ComplexObject<?>> void simpleStoreAndRead(
		final Class<? extends EclipseStoreRepository<T, Integer>> repositoryClass,
		final Function<Integer, T> objectCreator,
		final Consumer<T> ignoredObjectChanger,
		@Autowired final ApplicationContext context)
	{
		final EclipseStoreRepository<T, Integer> repository = context.getBean(repositoryClass);
		final T objectToStore = objectCreator.apply(1);
		repository.save(objectToStore);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> this.dynamicAssertEquals(1, repository, objectToStore)
		);
	}
	
	@ParameterizedTest
	@MethodSource(TYPES_NOT_WORKING_DATA_SOURCE)
	<T extends ComplexObject<?>> void simpleStoreAndReadForNotWorkingTypes(
		final Class<? extends EclipseStoreRepository<T, Integer>> repositoryClass,
		final Function<Integer, T> objectCreator,
		final Consumer<T> ignoredObjectChanger,
		@Autowired final ApplicationContext context)
	{
		Assertions.assertThrows(
			DataTypeNotSupportedException.class,
			() -> this.simpleChangeAfterStore(repositoryClass, objectCreator, ignoredObjectChanger, context));
	}
	
	@ParameterizedTest
	@MethodSource(TYPES_DATA_SOURCE)
	<T extends ComplexObject<?>> void simpleChangeToNullAfterStore(
		final Class<? extends EclipseStoreRepository<T, Integer>> repositoryClass,
		final Function<Integer, T> objectCreator,
		final Consumer<T> ignoredObjectChanger,
		@Autowired final ApplicationContext context)
	{
		this.simpleChangeAfterStore(repositoryClass, objectCreator, object -> object.setValue(null), context);
	}
	
	@ParameterizedTest
	@MethodSource(TYPES_DATA_SOURCE)
	<T extends ComplexObject<?>> void simpleChangeAfterStore(
		final Class<? extends EclipseStoreRepository<T, Integer>> repositoryClass,
		final Function<Integer, T> objectCreator,
		final Consumer<T> objectChanger,
		@Autowired final ApplicationContext context)
	{
		final EclipseStoreRepository<T, Integer> repository = context.getBean(repositoryClass);
		final T objectToStore = objectCreator.apply(1);
		repository.save(objectToStore);
		
		final Optional<T> storedObject = repository.findById(1);
		Assertions.assertTrue(storedObject.isPresent());
		
		objectChanger.accept(storedObject.get());
		repository.save(storedObject.get());
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> this.dynamicAssertEquals(1, repository, storedObject.get())
		);
	}
	
	@ParameterizedTest
	@MethodSource(TYPES_DATA_SOURCE)
	<T extends ComplexObject<?>> void doubleStoreSameEntityWithChange(
		final Class<? extends EclipseStoreRepository<T, Integer>> repositoryClass,
		final Function<Integer, T> objectCreator,
		final Consumer<T> objectChanger,
		@Autowired final ApplicationContext context)
	{
		final EclipseStoreRepository<T, Integer> repository = context.getBean(repositoryClass);
		final T objectToStore = objectCreator.apply(1);
		repository.save(objectToStore);
		
		objectChanger.accept(objectToStore);
		repository.save(objectToStore);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> this.dynamicAssertEquals(1, repository, objectToStore)
		);
	}
	
	@ParameterizedTest
	@MethodSource(TYPES_DATA_SOURCE)
	<T extends ComplexObject<?>> void simpleChangeBeforeStore(
		final Class<? extends EclipseStoreRepository<T, Integer>> repositoryClass,
		final Function<Integer, T> objectCreator,
		final Consumer<T> objectChanger,
		@Autowired final ApplicationContext context)
	{
		final EclipseStoreRepository<T, Integer> repository = context.getBean(repositoryClass);
		final T objectToStore = objectCreator.apply(1);
		objectChanger.accept(objectToStore);
		repository.save(objectToStore);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> this.dynamicAssertEquals(1, repository, objectToStore)
		);
	}
	
	private <T extends ComplexObject<?>> void dynamicAssertEquals(
		final int id,
		final EclipseStoreRepository<T, Integer> repository,
		final T objectToStore)
	{
		final Optional<T> storedObject2 = repository.findById(id);
		Assertions.assertTrue(storedObject2.isPresent());
		if(storedObject2.get().getValue() instanceof final Map<?, ?> storedMap)
		{
			Assertions.assertEquals(((Map<?, ?>)objectToStore.getValue()).size(), storedMap.size());
		}
		else if(storedObject2.get().getValue() instanceof final Collection<?> storedList)
		{
			Assertions.assertEquals(((Collection<?>)objectToStore.getValue()).size(), storedList.size());
		}
		else
		{
			Assertions.assertEquals(objectToStore, storedObject2.get());
		}
	}
}
