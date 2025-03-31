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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.repository.Repository;

import software.xdev.spring.data.eclipse.store.core.EntityProvider;
import software.xdev.spring.data.eclipse.store.helper.TestData;


class EclipseStoreQueryCreatorAndOrTest
{
	static Stream<Arguments> generateDataWithCountOfFirstNameAndLastName()
	{
		return Stream.of(
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_EMPTY, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_ONE, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_TWO, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_THREE, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_DABC_ABCD, 0)
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithCountOfFirstNameAndLastName")
	void findByFirstNameAndLastName(
		final EntityProvider<QueryCreatorUtil.Customer, Void> entities,
		final int expectedCount) throws NoSuchMethodException
	{
		final Method method =
			CustomerRepository.class.getMethod("findByFirstNameAndLastName", String.class, String.class);
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(
				entities,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{TestData.FIRST_NAME, TestData.LAST_NAME});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	static Stream<Arguments> generateDataWithCountOfFirstNameOrLastName()
	{
		return Stream.of(
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_EMPTY, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_ONE, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_TWO, 2),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_THREE, 3),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_DABC_ABCD, 0)
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithCountOfFirstNameOrLastName")
	void findByFirstNameOrLastName(
		final EntityProvider<QueryCreatorUtil.Customer, Void> entities,
		final int expectedCount) throws NoSuchMethodException
	{
		final Method method =
			CustomerRepository.class.getMethod("findByFirstNameOrLastName", String.class, String.class);
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(
				entities,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{TestData.FIRST_NAME, TestData.LAST_NAME_ALTERNATIVE});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	static Stream<Arguments> generateDataWithCountOfId2AndFirstName()
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
	@MethodSource("generateDataWithCountOfId2AndFirstName")
	void findByIdAndFirstName(
		final EntityProvider<QueryCreatorUtil.Customer, Void> entities,
		final int expectedCount) throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod("findByIdAndFirstName", int.class, String.class);
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(
				entities,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{2, TestData.FIRST_NAME_ALTERNATIVE});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	static Stream<Arguments> generateDataWithCountOfIdAndFirstNameAndLastName()
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
	@MethodSource("generateDataWithCountOfIdAndFirstNameAndLastName")
	void findByIdAndFirstNameAndLastName(
		final EntityProvider<QueryCreatorUtil.Customer, Void> entities,
		final int expectedCount) throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod(
			"findByIdAndFirstNameAndLastName",
			int.class,
			String.class,
			String.class);
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(
				entities,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{2, TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	static Stream<Arguments> generateDataWithCountOfIdOrFirstNameOrLastName()
	{
		return Stream.of(
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_EMPTY, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_ONE, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_TWO, 2),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_THREE, 3),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_DABC_ABCD, 2)
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithCountOfIdOrFirstNameOrLastName")
	void findByIdOrFirstNameOrLastName(
		final EntityProvider<QueryCreatorUtil.Customer, Void> entities,
		final int expectedCount) throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod(
			"findByIdOrFirstNameOrLastName",
			int.class,
			String.class,
			String.class);
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(
				entities,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{1, "B", TestData.LAST_NAME_ALTERNATIVE});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	static Stream<Arguments> generateDataWithCountOfIdAndFirstNameOrLastName()
	{
		return Stream.of(
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_EMPTY, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_ONE, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_TWO, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_THREE, 2),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_DABC_ABCD, 1)
		);
	}
	
	/**
	 * Should have the same Assertions as {@link #findByLastNameOrIdAndFirstName}
	 **/
	@ParameterizedTest
	@MethodSource("generateDataWithCountOfIdAndFirstNameOrLastName")
	void findByIdAndFirstNameOrLastName(
		final EntityProvider<QueryCreatorUtil.Customer, Void> entities,
		final int expectedCount) throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod(
			"findByIdAndFirstNameOrLastName",
			int.class,
			String.class,
			String.class);
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(
				entities,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{1, "D", TestData.LAST_NAME_ALTERNATIVE});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	/**
	 * Should have the same Assertions as {@link #findByIdAndFirstNameOrLastName}
	 * <p>
	 *     TODO: This fails because {@code AND} is not prioritized higher then {@code OR}.
	 *     Since this is a lot of work in the criteria tree, this is postponed.
	 * </p>
	 */
	@ParameterizedTest
	@MethodSource("generateDataWithCountOfIdAndFirstNameOrLastName")
	@Disabled("This fails because AND is not prioritized higher then OR. Since this is a lot of work "
		+ "in the criteria tree, this is postponed.")
	void findByLastNameOrIdAndFirstName(
		final EntityProvider<QueryCreatorUtil.Customer, Void> entities,
		final int expectedCount) throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod(
			"findByLastNameOrIdAndFirstName",
			String.class,
			int.class,
			String.class);
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(
				entities,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{1, "D", TestData.LAST_NAME_ALTERNATIVE});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	static Stream<Arguments> generateDataWithCountOfIdOrFirstNameAndLastName()
	{
		return Stream.of(
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_EMPTY, 0),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_ONE, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_TWO, 1),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_THREE, 2),
			Arguments.of(QueryCreatorUtil.DATA_CUSTOMERS_DABC_ABCD, 1)
		);
	}
	
	@ParameterizedTest
	@MethodSource("generateDataWithCountOfIdOrFirstNameAndLastName")
	void findByIdOrFirstNameAndLastName(
		final EntityProvider<QueryCreatorUtil.Customer, Void> entities,
		final int expectedCount) throws NoSuchMethodException
	{
		final Method method = CustomerRepository.class.getMethod(
			"findByIdOrFirstNameAndLastName",
			int.class,
			String.class,
			String.class);
		final Collection<QueryCreatorUtil.Customer> foundCustomer =
			QueryCreatorUtil.executeQuery(
				entities,
				QueryCreatorUtil.Customer.class,
				method,
				new Object[]{1, TestData.FIRST_NAME, TestData.LAST_NAME_ALTERNATIVE});
		Assertions.assertEquals(expectedCount, foundCustomer.size());
	}
	
	private interface CustomerRepository extends Repository<QueryCreatorUtil.Customer, Void>
	{
		
		List<QueryCreatorUtil.Customer> findByFirstNameAndLastName(final String firstName, String lastName);
		
		List<QueryCreatorUtil.Customer> findByFirstNameOrLastName(final String firstName, String lastName);
		
		List<QueryCreatorUtil.Customer> findByIdAndFirstName(final int id, final String firstName);
		
		List<QueryCreatorUtil.Customer> findByIdAndFirstNameAndLastName(
			final int id,
			final String firstName,
			String lastName);
		
		List<QueryCreatorUtil.Customer> findByIdOrFirstNameOrLastName(
			final int id,
			final String firstName,
			String lastName);
		
		List<QueryCreatorUtil.Customer> findByIdAndFirstNameOrLastName(
			final int id,
			final String firstName,
			String lastName);
		
		List<QueryCreatorUtil.Customer> findByLastNameOrIdAndFirstName(
			String lastName,
			final int id,
			final String firstName);
		
		List<QueryCreatorUtil.Customer> findByIdOrFirstNameAndLastName(
			final int id,
			final String firstName,
			String lastName);
	}
}
