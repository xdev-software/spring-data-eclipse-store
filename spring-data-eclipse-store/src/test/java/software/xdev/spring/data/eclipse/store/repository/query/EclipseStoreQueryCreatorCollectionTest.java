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


class EclipseStoreQueryCreatorCollectionTest
{
	
	static Stream<Arguments> generateDataWithCountOfIdIn()
	{
		return Stream.of(
			Arguments.of(DATA_CUSTOMERS_EMPTY, 0),
			Arguments.of(DATA_CUSTOMERS_ONE, 0),
			Arguments.of(DATA_CUSTOMERS_TWO, 1),
			Arguments.of(DATA_CUSTOMERS_THREE, 2),
			Arguments.of(DATA_CUSTOMERS_DABC_ABCD, 2)
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithCountOfIdIn")
	void findByIdIn(final EntityProvider<Customer> entities, final int expectedCount)
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByIdIn", Collection.class);
		final Collection<Customer> foundCustomer =
			executeQuery(entities, Customer.class, method, new Object[]{List.of(2, 3)});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	@Test
	void findByIdInNull()
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByIdIn", Collection.class);
		final Collection<Customer> foundCustomer =
			executeQuery(DATA_CUSTOMERS_THREE, Customer.class, method, new Object[]{null});
		Assertions.assertEquals(0, foundCustomer.size());
	}
	
	static Stream<Arguments> generateDataWithCountOfIdNotIn()
	{
		return Stream.of(
			Arguments.of(DATA_CUSTOMERS_EMPTY, 0),
			Arguments.of(DATA_CUSTOMERS_ONE, 1),
			Arguments.of(DATA_CUSTOMERS_TWO, 1),
			Arguments.of(DATA_CUSTOMERS_THREE, 1),
			Arguments.of(DATA_CUSTOMERS_DABC_ABCD, 2)
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithCountOfIdNotIn")
	void findByIdNotIn(final EntityProvider<Customer> entities, final int expectedCount)
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByIdNotIn", Collection.class);
		final Collection<Customer> foundCustomer =
			executeQuery(entities, Customer.class, method, new Object[]{List.of(2, 3)});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	@Test
	void findByIdNotInNull()
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByIdNotIn", Collection.class);
		final Collection<Customer> foundCustomer =
			executeQuery(DATA_CUSTOMERS_THREE, Customer.class, method, new Object[]{null});
		Assertions.assertEquals(3, foundCustomer.size());
	}
	
	private interface CustomerRepository extends Repository<Customer, Void>
	{
		List<Customer> findByIdIn(final Collection<Integer> ids);
		
		List<Customer> findByIdNotIn(final Collection<Integer> ids);
	}
}
