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


class EclipseStoreQueryCreatorBooleanTest
{
	
	static Stream<Arguments> generateDataWithCountOfEnabled()
	{
		return Stream.of(
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_EMPTY, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_ONE, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_TWO, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_THREE, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_DABC_ABCD, 2)
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithCountOfEnabled")
	void findByEnabledTrue(final EntityProvider<QueryCreatorUtil.Customer, Void> entities, final int expectedCount)
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByEnabledTrue");
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(entities, QueryCreatorUtil.Customer.class, method, new Object[]{});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	static Stream<Arguments> generateDataWithCountOfEnabledFalse()
	{
		return Stream.of(
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_EMPTY, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_ONE, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_TWO, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_THREE, 2),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_DABC_ABCD, 2)
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithCountOfEnabledFalse")
	void findByEnabledFalse(final EntityProvider<QueryCreatorUtil.Customer, Void> entities, final int expectedCount)
		throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByEnabledFalse");
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(entities, QueryCreatorUtil.Customer.class, method, new Object[]{});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	private interface CustomerRepository extends Repository<QueryCreatorUtil.Customer, Void>
	{
		List<QueryCreatorUtil.Customer> findByEnabledTrue();
		
		List<QueryCreatorUtil.Customer> findByEnabledFalse();
	}
}
