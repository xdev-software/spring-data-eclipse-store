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
package software.xdev.spring.data.eclipse.store.repository.query;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.repository.Repository;

import software.xdev.spring.data.eclipse.store.core.EntityProvider;
import software.xdev.spring.data.eclipse.store.helper.TestData;


class EclipseStoreQueryCreatorIsIsNotTest
{
	
	static Stream<Arguments> generateDataWithCountOfFirstName()
	{
		return Stream.of(
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_EMPTY, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_ONE, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_TWO, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_THREE, 2),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_DABC_ABCD, 0)
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithCountOfFirstName")
	void findByFirstName(final EntityProvider<QueryCreatorUtil.Customer> entities, final int expectedCount)
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByFirstName", String.class);
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(
				entities,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{TestData.FIRST_NAME});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithCountOfFirstName")
	void findByFirstNameIsNull(final EntityProvider<QueryCreatorUtil.Customer> entities)
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByFirstName", String.class);
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(entities, QueryCreatorUtil.Customer.class, method, new Object[]{null});
		Assertions.assertEquals(0, foundCustomer.size());
	}
	
	static Stream<Arguments> generateDataWithCountOfId1()
	{
		return Stream.of(
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_EMPTY, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_ONE, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_TWO, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_THREE, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_DABC_ABCD, 1)
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithCountOfId1")
	void findById(
		final EntityProvider<QueryCreatorUtil.Customer> entities,
		final int expectedCount) throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findById", int.class);
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(
				entities,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{1});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	static Stream<Arguments> generateDataWithIncomparable()
	{
		return Stream.of(
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_EMPTY, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_ONE, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_TWO, 2),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_THREE, 2),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_DABC_ABCD, 3)
		);
	}
	
	/**
	 * Test to see if {@link Object#equals(Object)} is used for the {@code is} implementation instead of the {@code ==}
	 */
	@ParameterizedTest
	@MethodSource("generateDataWithIncomparable")
	void findByIncomparable(
		final EntityProvider<QueryCreatorUtil.Customer> entities,
		final int expectedCount) throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod(
			"findByIncomparable",
			QueryCreatorUtil.Incomparable.class);
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(
				entities,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{new QueryCreatorUtil.Incomparable("")});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithIncomparable")
	void findByIncomparableIsNotNull(
		final EntityProvider<QueryCreatorUtil.Customer> entities,
		final int expectedCount) throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod(
			"findByIncomparableIsNotNull");
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(
				entities,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	static Stream<Arguments> generateDataWithIncomparableIsNull()
	{
		return Stream.of(
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_EMPTY, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_ONE, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_TWO, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_THREE, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_DABC_ABCD, 1)
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithIncomparableIsNull")
	void findByIncomparableIsNullDynamic(
		final EntityProvider<QueryCreatorUtil.Customer> entities,
		final int expectedCount) throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod(
			"findByIncomparable",
			QueryCreatorUtil.Incomparable.class);
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(
				entities,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{null});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithIncomparableIsNull")
	void findByIncomparableIsNull(
		final EntityProvider<QueryCreatorUtil.Customer> entities,
		final int expectedCount) throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByIncomparableIsNull");
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(
				entities,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	private interface CustomerRepository extends Repository<QueryCreatorUtil.Customer, Void>
	{
		List<QueryCreatorUtil.Customer> findByFirstName(final String firstName);
		
		List<QueryCreatorUtil.Customer> findById(final int id);
		
		List<QueryCreatorUtil.Customer> findByIncomparable(final QueryCreatorUtil.Incomparable incomparableObject);
		
		List<QueryCreatorUtil.Customer> findByIncomparableIsNull();
		
		List<QueryCreatorUtil.Customer> findByIncomparableIsNotNull();
	}
}
