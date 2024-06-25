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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.repository.Repository;

import software.xdev.spring.data.eclipse.store.exceptions.NotComparableException;
import software.xdev.spring.data.eclipse.store.helper.TestData;


class EclipseStoreQueryCreatorGreaterLessTest
{
	static Stream<Arguments> generateDataWithIdLessThan()
	{
		return Stream.of(
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_EMPTY, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_ONE, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_TWO, 2),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_THREE, 2),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_DABC_ABCD, 2)
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithIdLessThan")
	void findByIdLessThan(final Collection<QueryCreatorUtil.Customer> entities, final int expectedCount)
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByIdLessThan", int.class);
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(
				entities,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{3});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	static Stream<Arguments> generateDataWithIdLessThanEqual()
	{
		return Stream.of(
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_EMPTY, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_ONE, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_TWO, 2),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_THREE, 3),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_DABC_ABCD, 3)
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithIdLessThanEqual")
	void findByIdLessThanEqual(final Collection<QueryCreatorUtil.Customer> entities, final int expectedCount)
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByIdLessThanEqual", int.class);
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(
				entities,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{3});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	static Stream<Arguments> generateDataWithIdGreaterThan()
	{
		return Stream.of(
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_EMPTY, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_ONE, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_TWO, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_THREE, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_DABC_ABCD, 2)
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithIdGreaterThan")
	void findByIdGreaterThan(final Collection<QueryCreatorUtil.Customer> entities, final int expectedCount)
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByIdGreaterThan", int.class);
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(
				entities,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{2});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	static Stream<Arguments> generateDataWithIdGreaterThanEqual()
	{
		return Stream.of(
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_EMPTY, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_ONE, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_TWO, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_THREE, 2),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_DABC_ABCD, 3)
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithIdGreaterThanEqual")
	void findByIdGreaterThanEqual(final Collection<QueryCreatorUtil.Customer> entities, final int expectedCount)
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByIdGreaterThanEqual", int.class);
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(
				entities,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{2});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	static Stream<Arguments> generateDataWithIdBetween()
	{
		return Stream.of(
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_EMPTY, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_ONE, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_TWO, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_THREE, 2),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_DABC_ABCD, 3)
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithIdBetween")
	void findByIdBetween(final Collection<QueryCreatorUtil.Customer> entities, final int expectedCount)
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByIdBetween", int.class, int.class);
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(
				entities,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{2, 4});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithIdBetween")
	void findByIdBetweenInvalidRange(final Collection<QueryCreatorUtil.Customer> entities)
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByIdBetween", int.class, int.class);
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(
				entities,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{4, 2});
		Assertions.assertEquals(0, foundCustomer.size());
	}
	
	static Stream<Arguments> generateDataWithFirstNameGreaterThan()
	{
		return Stream.of(
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_EMPTY, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_ONE, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_TWO, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_THREE, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_DABC_ABCD, 0)
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithFirstNameGreaterThan")
	void findByFirstNameGreaterThan(final Collection<QueryCreatorUtil.Customer> entities, final int expectedCount)
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByFirstNameGreaterThan", String.class);
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(
				entities,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{TestData.FIRST_NAME});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	static Stream<Arguments> generateDataWithFirstNameLessThan()
	{
		return Stream.of(
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_EMPTY, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_ONE, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_TWO, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_THREE, 2),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_DABC_ABCD, 4)
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithFirstNameLessThan")
	void findByFirstNameLessThan(final Collection<QueryCreatorUtil.Customer> entities, final int expectedCount)
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByFirstNameLessThan", String.class);
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(
				entities,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{TestData.FIRST_NAME_ALTERNATIVE});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	@Test
	void findByIncomparableLessThan()
		throws NoSuchMethodException
	{
		final Method method =
			CustomerRepository.class.getMethod("findByIncomparableLessThan", QueryCreatorUtil.Incomparable.class);
		final QueryCreatorUtil.Incomparable incomparable = new QueryCreatorUtil.Incomparable("");
		Assertions.assertThrows(NotComparableException.class, () ->
			QueryCreatorUtil.executeQuery(
				QueryCreatorUtil.DATA_CUSTOMERS_THREE,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{incomparable}));
	}
	
	private interface CustomerRepository extends Repository<QueryCreatorUtil.Customer, Void>
	{
		List<QueryCreatorUtil.Customer> findByIdLessThan(final int maxIdExcluding);
		
		List<QueryCreatorUtil.Customer> findByIdLessThanEqual(final int maxIdIncluding);
		
		List<QueryCreatorUtil.Customer> findByIdGreaterThan(final int minIdExcluding);
		
		List<QueryCreatorUtil.Customer> findByIdGreaterThanEqual(final int minIdIncluding);
		
		List<QueryCreatorUtil.Customer> findByFirstNameGreaterThan(final String minStringExcluding);
		
		List<QueryCreatorUtil.Customer> findByFirstNameLessThan(final String maxStringExcluding);
		
		List<QueryCreatorUtil.Customer> findByIncomparableLessThan(
			final QueryCreatorUtil.Incomparable lessIncomparable);
		
		List<QueryCreatorUtil.Customer> findByIdBetween(final int minIdIncluding, final int maxIdIncluding);
	}
}
