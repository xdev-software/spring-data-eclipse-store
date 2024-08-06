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

import static software.xdev.spring.data.eclipse.store.repository.query.QueryCreatorUtil.Customer;
import static software.xdev.spring.data.eclipse.store.repository.query.QueryCreatorUtil.DATA_CUSTOMERS_DABC_ABCD;
import static software.xdev.spring.data.eclipse.store.repository.query.QueryCreatorUtil.DATA_CUSTOMERS_EMPTY;
import static software.xdev.spring.data.eclipse.store.repository.query.QueryCreatorUtil.DATA_CUSTOMERS_ONE;
import static software.xdev.spring.data.eclipse.store.repository.query.QueryCreatorUtil.DATA_CUSTOMERS_THREE;
import static software.xdev.spring.data.eclipse.store.repository.query.QueryCreatorUtil.DATA_CUSTOMERS_TWO;
import static software.xdev.spring.data.eclipse.store.repository.query.QueryCreatorUtil.DATA_CUSTOMERS_WITH_NULL_STRING;
import static software.xdev.spring.data.eclipse.store.repository.query.QueryCreatorUtil.executeQuery;

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

import software.xdev.spring.data.eclipse.store.core.EntityProvider;
import software.xdev.spring.data.eclipse.store.helper.TestData;


class EclipseStoreQueryCreatorStringTest
{
	
	static Stream<Arguments> generateDataWithCountOfFirstNameLike()
	{
		return Stream.of(
			Arguments.of("%", 3),
			Arguments.of("M%", 2),
			Arguments.of("m%", 0),
			Arguments.of("m_ck", 0),
			Arguments.of("%ick", 2),
			Arguments.of("%k", 2),
			Arguments.of("%ck%", 2),
			Arguments.of("%ev%", 1),
			Arguments.of("Mick", 2),
			Arguments.of("M%k", 2),
			Arguments.of("M_k", 0),
			Arguments.of("M__k", 2),
			Arguments.of("Mic_", 2),
			Arguments.of("_ick", 2),
			Arguments.of("____", 2),
			Arguments.of("_", 0),
			Arguments.of("", 0),
			Arguments.of("Mic", 0)
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithCountOfFirstNameLike")
	void findByFirstNameLike(final String likeString, final int expectedCount)
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByFirstNameLike", String.class);
		final Collection<Customer> foundCustomer =
			executeQuery(DATA_CUSTOMERS_THREE, Customer.class, method, new Object[]{likeString});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	@Test
	void findByFirstNameLikeNullStrings()
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByFirstNameLike", String.class);
		final Collection<Customer> foundCustomer =
			executeQuery(DATA_CUSTOMERS_WITH_NULL_STRING, Customer.class, method, new Object[]{"%"});
		Assertions.assertEquals(3, foundCustomer.size());
	}
	
	static Stream<Arguments> generateDataWithCountOfFirstNameNotLike()
	{
		return Stream.of(
			Arguments.of("%", 0),
			Arguments.of("M%", 1),
			Arguments.of("m%", 3),
			Arguments.of("m_ck", 3),
			Arguments.of("%ick", 1),
			Arguments.of("%k", 1),
			Arguments.of("%ck%", 1),
			Arguments.of("%ev%", 2),
			Arguments.of("Mick", 1),
			Arguments.of("M%k", 1),
			Arguments.of("M_k", 3),
			Arguments.of("M__k", 1),
			Arguments.of("Mic_", 1),
			Arguments.of("_ick", 1),
			Arguments.of("____", 1),
			Arguments.of("_", 3),
			Arguments.of("", 3),
			Arguments.of("Mic", 3)
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithCountOfFirstNameNotLike")
	void findByFirstNameNotLike(final String likeString, final int expectedCount)
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByFirstNameNotLike", String.class);
		final Collection<Customer> foundCustomer =
			executeQuery(DATA_CUSTOMERS_THREE, Customer.class, method, new Object[]{likeString});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	@Test
	void findByFirstNameNotLikeNullStrings()
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByFirstNameNotLike", String.class);
		final Collection<Customer> foundCustomer =
			executeQuery(DATA_CUSTOMERS_WITH_NULL_STRING, Customer.class, method, new Object[]{"%"});
		Assertions.assertEquals(0, foundCustomer.size());
	}
	
	static Stream<Arguments> generateDataWithFirstNameStartingWith()
	{
		return Stream.of(
			Arguments.of(DATA_CUSTOMERS_EMPTY, 0),
			Arguments.of(DATA_CUSTOMERS_ONE, 1),
			Arguments.of(DATA_CUSTOMERS_TWO, 1),
			Arguments.of(DATA_CUSTOMERS_THREE, 2),
			Arguments.of(DATA_CUSTOMERS_DABC_ABCD, 0),
			Arguments.of(DATA_CUSTOMERS_WITH_NULL_STRING, 1)
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithFirstNameStartingWith")
	void findByFirstNameStartingWith(final EntityProvider<Customer, Void> entities, final int expectedCount)
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByFirstNameStartingWith", String.class);
		final Collection<Customer> foundCustomer = executeQuery(
			entities,
			Customer.class,
			method,
			new Object[]{TestData.FIRST_NAME.substring(0, 1)});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithFirstNameStartingWith")
	void findByFirstNameEndingWith(final EntityProvider<Customer, Void> entities, final int expectedCount)
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByFirstNameEndingWith", String.class);
		final Collection<Customer> foundCustomer = executeQuery(
			entities,
			Customer.class,
			method,
			new Object[]{TestData.FIRST_NAME.substring(2, 4)});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithFirstNameStartingWith")
	void findByFirstNameContainingWith(final EntityProvider<Customer, Void> entities, final int expectedCount)
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByFirstNameContaining", String.class);
		final Collection<Customer> foundCustomer = executeQuery(
			entities,
			Customer.class,
			method,
			new Object[]{TestData.FIRST_NAME.substring(1, 3)});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	private interface CustomerRepository extends Repository<Customer, Void>
	{
		List<Customer> findByFirstNameLike(final String regexFirstName);
		
		List<Customer> findByFirstNameNotLike(final String regexFirstName);
		
		List<Customer> findByFirstNameStartingWith(final String startingFirstName);
		
		List<Customer> findByFirstNameEndingWith(final String endingFirstName);
		
		List<Customer> findByFirstNameContaining(final String containingFirstName);
	}
}
