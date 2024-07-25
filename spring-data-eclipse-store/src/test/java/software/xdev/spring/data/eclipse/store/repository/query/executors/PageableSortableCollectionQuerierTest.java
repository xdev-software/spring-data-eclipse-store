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
package software.xdev.spring.data.eclipse.store.repository.query.executors;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import software.xdev.spring.data.eclipse.store.core.EntityProvider;
import software.xdev.spring.data.eclipse.store.helper.DummyEntityProvider;
import software.xdev.spring.data.eclipse.store.helper.DummyWorkingCopier;
import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.repository.query.ReflectedField;
import software.xdev.spring.data.eclipse.store.repository.query.criteria.Criteria;
import software.xdev.spring.data.eclipse.store.repository.query.criteria.CriteriaSingleNode;


@SuppressWarnings("checkstyle:MethodName")
class PageableSortableCollectionQuerierTest
{
	private static final Field CUSTOMER_FIRST_NAME_FIELD;
	
	static
	{
		try
		{
			CUSTOMER_FIRST_NAME_FIELD = Customer.class.getDeclaredField("firstName");
		}
		catch(final NoSuchFieldException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private static final EntityProvider<Customer, Void> DATA_CUSTOMERS_EMPTY = DummyEntityProvider.of();
	private static final EntityProvider<Customer, Void> DATA_CUSTOMERS_ONE =
		DummyEntityProvider.of(new Customer(TestData.FIRST_NAME, TestData.LAST_NAME));
	private static final EntityProvider<Customer, Void> DATA_CUSTOMERS_TWO = DummyEntityProvider.of(
		new Customer(TestData.FIRST_NAME, TestData.LAST_NAME),
		new Customer(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE));
	private static final EntityProvider<Customer, Void> DATA_CUSTOMERS_THREE = DummyEntityProvider.of(
		new Customer(TestData.FIRST_NAME, TestData.LAST_NAME),
		new Customer(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE),
		new Customer(TestData.FIRST_NAME, TestData.LAST_NAME_ALTERNATIVE));
	private static final EntityProvider<Customer, Void> DATA_CUSTOMERS_DABC_ABCD = DummyEntityProvider.of(
		new Customer("D", "A"),
		new Customer("A", "B"),
		new Customer("B", "C"),
		new Customer("C", "D"));
	
	static Stream<Arguments> generateData()
	{
		// Columns of arguments:
		// 1: Collection of Customer
		// 2: Count of Customer with first name = TestData.FIRST_NAME
		return Stream.of(
			Arguments.of(DATA_CUSTOMERS_EMPTY, 0),
			Arguments.of(DATA_CUSTOMERS_ONE, 1),
			Arguments.of(DATA_CUSTOMERS_TWO, 1),
			Arguments.of(DATA_CUSTOMERS_THREE, 2),
			Arguments.of(DATA_CUSTOMERS_DABC_ABCD, 0)
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateData")
	void getEntities_NoCriteria_NoPageable_NoSortable(final EntityProvider<Customer, Void> entities)
	{
		final PageableSortableCollectionQuerier<Customer> querier = new PageableSortableCollectionQuerier<>(
			new DummyWorkingCopier<>(),
			Criteria.createNoCriteria()
		);
		Assertions.assertEquals(entities.size(), querier.getEntities(entities, Customer.class).size());
	}
	
	@ParameterizedTest
	@MethodSource("generateData")
	void getEntities_EmptyCriteria_NoPageable_NoSortable(final EntityProvider<Customer, Void> entities)
	{
		final PageableSortableCollectionQuerier<Customer> querier = new PageableSortableCollectionQuerier<>(
			new DummyWorkingCopier<>(),
			new CriteriaSingleNode<>()
		);
		Assertions.assertEquals(entities.size(), querier.getEntities(entities, Customer.class).size());
	}
	
	@ParameterizedTest
	@MethodSource("generateData")
	void getEntities_EmptyCriteria_NoPageable_Sortable_SameSize(final EntityProvider<Customer, Void> entities)
	{
		final PageableSortableCollectionQuerier<Customer> querier = new PageableSortableCollectionQuerier<>(
			new DummyWorkingCopier<>(),
			new CriteriaSingleNode<>()
		);
		final List<Customer> sortedCustomers =
			querier.getEntities(entities, Pageable.unpaged(), Customer.class, Sort.by("firstName"));
		Assertions.assertEquals(entities.size(), sortedCustomers.size());
	}
	
	@ParameterizedTest
	@MethodSource("generateData")
	void getEntities_EmptyCriteria_Pageable_Sortable_FixedSize(final EntityProvider<Customer, Void> entities)
	{
		final PageableSortableCollectionQuerier<Customer> querier = new PageableSortableCollectionQuerier<>(
			new DummyWorkingCopier<>(),
			new CriteriaSingleNode<>()
		);
		final Pageable pageable = Pageable.ofSize(2);
		final List<Customer> pagedCustomers = querier.getEntities(entities, pageable, Customer.class);
		final int expectedCount = Math.min(pagedCustomers.size(), 2);
		Assertions.assertEquals(expectedCount, pagedCustomers.size());
	}
	
	@Test
	void getEntities_EmptyCriteria_Pageable_Sortable_MultiplePages()
	{
		final PageableSortableCollectionQuerier<Customer> querier = new PageableSortableCollectionQuerier<>(
			new DummyWorkingCopier<>(),
			new CriteriaSingleNode<>()
		);
		final Pageable pageable = Pageable.ofSize(2);
		final List<Customer> pagedCustomersPage1 = querier.getEntities(DATA_CUSTOMERS_THREE, pageable, Customer.class);
		Assertions.assertEquals(2, pagedCustomersPage1.size());
		
		final List<Customer> pagedCustomersPage2 =
			querier.getEntities(DATA_CUSTOMERS_THREE, pageable.next(), Customer.class);
		Assertions.assertEquals(1, pagedCustomersPage2.size());
	}
	
	@Test
	void getEntities_EmptyCriteria_NoPageable_Sortable_IsSortedByFirstName()
	{
		final PageableSortableCollectionQuerier<Customer> querier = new PageableSortableCollectionQuerier<>(
			new DummyWorkingCopier<>(),
			new CriteriaSingleNode<>()
		);
		final List<Customer> sortedCustomers =
			querier.getEntities(DATA_CUSTOMERS_DABC_ABCD, Pageable.unpaged(), Customer.class, Sort.by("firstName"));
		Assertions.assertEquals("A", sortedCustomers.get(0).firstName);
		Assertions.assertEquals("B", sortedCustomers.get(1).firstName);
		Assertions.assertEquals("C", sortedCustomers.get(2).firstName);
		Assertions.assertEquals("D", sortedCustomers.get(3).firstName);
	}
	
	@Test
	void getEntities_EmptyCriteria_NoPageable_Sortable_IsSortedByLastName()
	{
		final PageableSortableCollectionQuerier<Customer> querier = new PageableSortableCollectionQuerier<>(
			new DummyWorkingCopier<>(),
			new CriteriaSingleNode<>()
		);
		final List<Customer> sortedCustomers =
			querier.getEntities(DATA_CUSTOMERS_DABC_ABCD, Pageable.unpaged(), Customer.class, Sort.by("lastName"));
		Assertions.assertEquals("A", sortedCustomers.get(0).lastName);
		Assertions.assertEquals("B", sortedCustomers.get(1).lastName);
		Assertions.assertEquals("C", sortedCustomers.get(2).lastName);
		Assertions.assertEquals("D", sortedCustomers.get(3).lastName);
	}
	
	@Test
	void getEntities_EmptyCriteria_NoPageable_Sortable_IsSortedByInvalidProperty()
	{
		final PageableSortableCollectionQuerier<Customer> querier = new PageableSortableCollectionQuerier<>(
			new DummyWorkingCopier<>(),
			new CriteriaSingleNode<>()
		);
		final Pageable unpaged = Pageable.unpaged();
		final Sort invalid = Sort.by("invalid");
		Assertions.assertThrows(
			RuntimeException.class,
			() -> querier.getEntities(DATA_CUSTOMERS_DABC_ABCD, unpaged, Customer.class, invalid));
	}
	
	@ParameterizedTest
	@MethodSource("generateData")
	void getEntities_CriteriaFirstName_NoPageable_NoSortable(
		final EntityProvider<Customer, Void> entities,
		final int countOfEntitiesWithFirstName)
	{
		final PageableSortableCollectionQuerier<Customer> querier = new PageableSortableCollectionQuerier<>(
			new DummyWorkingCopier<>(),
			new CriteriaSingleNode<>(
				new ReflectedField<Customer, String>(CUSTOMER_FIRST_NAME_FIELD)).is(TestData.FIRST_NAME)
		);
		Assertions.assertEquals(countOfEntitiesWithFirstName, querier.getEntities(entities, Customer.class).size());
	}
	
	@Test
	void getEntities_NoCriteria_NoPageable_NoSortable_Null()
	{
		final PageableSortableCollectionQuerier<Customer> querier = new PageableSortableCollectionQuerier<>(
			new DummyWorkingCopier<>(),
			Criteria.createNoCriteria()
		);
		Assertions.assertThrows(NullPointerException.class, () -> querier.getEntities(null, Customer.class));
	}
	
	private record Customer(String firstName, String lastName)
	{
	}
}
