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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.query.by.string;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {QueryTestConfiguration.class})
class QuerySortTest
{
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private QueryTestConfiguration configuration;
	
	@Test
	void sortFindByLastNameDefault()
	{
		final Customer customer1 = new Customer("B", TestData.LAST_NAME);
		this.customerRepository.save(customer1);
		final Customer customer2 = new Customer("A", TestData.LAST_NAME);
		this.customerRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Sort sort = Sort.by("firstName");
				final List<Customer> customersPage =
					TestUtil.iterableToList(this.customerRepository.findAllByLastName(TestData.LAST_NAME, sort));
				Assertions.assertEquals(2, customersPage.size());
				Assertions.assertEquals(customer2, customersPage.get(0));
				Assertions.assertEquals(customer1, customersPage.get(1));
			}
		);
	}
	
	@Test
	void sortFindByFirstNameAscending()
	{
		final Customer customer1 = new Customer("B", TestData.LAST_NAME);
		this.customerRepository.save(customer1);
		final Customer customer2 = new Customer("A", TestData.LAST_NAME);
		this.customerRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Sort sort = Sort.by("firstName").ascending();
				final List<Customer> customersPage =
					TestUtil.iterableToList(this.customerRepository.findAllByLastName(TestData.LAST_NAME, sort));
				Assertions.assertEquals(2, customersPage.size());
				Assertions.assertEquals(customer2, customersPage.get(0));
				Assertions.assertEquals(customer1, customersPage.get(1));
			}
		);
	}
	
	@Test
	void sortFindByFirstNameAscendingVariant()
	{
		final Customer customer1 = new Customer(TestData.FIRST_NAME, "B");
		this.customerRepository.save(customer1);
		final Customer customer2 = new Customer(TestData.FIRST_NAME, "A");
		this.customerRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Sort sort = Sort.by(Sort.Direction.ASC, "lastName");
				final List<Customer> customersPage =
					TestUtil.iterableToList(this.customerRepository.findAll(sort));
				Assertions.assertEquals(2, customersPage.size());
				Assertions.assertEquals(customer2, customersPage.get(0));
				Assertions.assertEquals(customer1, customersPage.get(1));
			}
		);
	}
	
	@Test
	void sortFindByLastNameDescending()
	{
		final Customer customer1 = new Customer("B", TestData.LAST_NAME);
		this.customerRepository.save(customer1);
		final Customer customer2 = new Customer("A", TestData.LAST_NAME);
		this.customerRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Sort sort = Sort.by("firstName").descending();
				final List<Customer> customersPage =
					TestUtil.iterableToList(this.customerRepository.findAllByLastName(TestData.LAST_NAME, sort));
				Assertions.assertEquals(2, customersPage.size());
				Assertions.assertEquals(customer1, customersPage.get(0));
				Assertions.assertEquals(customer2, customersPage.get(1));
			}
		);
	}
}
