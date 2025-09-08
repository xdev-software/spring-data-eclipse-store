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
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {QueryTestConfiguration.class})
class QueryTest
{
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private CustomerWithChildRepository customerWithChildRepository;
	
	@Autowired
	private QueryTestConfiguration configuration;
	
	@Test
	void basicFindAllByLastNameTwoResults()
	{
		final Customer customer1 = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.customerRepository.save(customer1);
		final Customer customer2 = new Customer(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME);
		this.customerRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<Customer> customers =
					TestUtil.iterableToList(this.customerRepository.findAllByLastName(TestData.LAST_NAME));
				Assertions.assertEquals(2, customers.size());
				Assertions.assertEquals(customer1, Customer.getCustomerWithFirstName(customers, TestData.FIRST_NAME));
				Assertions.assertEquals(
					customer2,
					Customer.getCustomerWithFirstName(customers, TestData.FIRST_NAME_ALTERNATIVE));
			}
		);
	}
	
	@Test
	void basicFindAllByLastNameOneResult()
	{
		final Customer customer1 = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.customerRepository.save(customer1);
		final Customer customer2 = new Customer(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		this.customerRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<Customer> customers =
					TestUtil.iterableToList(this.customerRepository.findAllByLastName(TestData.LAST_NAME_ALTERNATIVE));
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(customer2, customers.get(0));
			}
		);
	}
	
	@Test
	void basicFindByFirstNameOneResult()
	{
		final Customer customer1 = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.customerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<Customer> foundCustomer = this.customerRepository.findByFirstName(TestData.FIRST_NAME);
				Assertions.assertTrue(foundCustomer.isPresent());
				Assertions.assertEquals(TestData.FIRST_NAME, foundCustomer.get().getFirstName());
			}
		);
	}
	
	@Test
	void findByOrderByLastNameAsc()
	{
		final Customer customer1 = new Customer(TestData.FIRST_NAME, "B");
		this.customerRepository.save(customer1);
		final Customer customer2 = new Customer(TestData.FIRST_NAME, "A");
		this.customerRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<Customer> customersPage =
					TestUtil.iterableToList(this.customerRepository.findByOrderByLastNameAsc());
				Assertions.assertEquals(2, customersPage.size());
				Assertions.assertEquals(customer2, customersPage.get(0));
				Assertions.assertEquals(customer1, customersPage.get(1));
			}
		);
	}
	
	@Test
	void findByChildExistingChild()
	{
		final Child child = new Child(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		final CustomerWithChild customer = new CustomerWithChild(TestData.FIRST_NAME, TestData.LAST_NAME, child);
		this.customerWithChildRepository.save(customer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<CustomerWithChild> foundCustomer = this.customerWithChildRepository.findByChild(child);
				Assertions.assertTrue(foundCustomer.isPresent());
			}
		);
	}
	
	@Test
	void findByChildNewChild()
	{
		final Child child = new Child(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		final CustomerWithChild customer = new CustomerWithChild(TestData.FIRST_NAME, TestData.LAST_NAME, child);
		this.customerWithChildRepository.save(customer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Child queryChild = new Child(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
				final Optional<CustomerWithChild> foundCustomer =
					this.customerWithChildRepository.findByChild(queryChild);
				Assertions.assertTrue(foundCustomer.isPresent());
			}
		);
	}
	
	@Test
	void findByChildWhenNullNotExists()
	{
		final Child child = new Child(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		final CustomerWithChild customer = new CustomerWithChild(TestData.FIRST_NAME, TestData.LAST_NAME, child);
		this.customerWithChildRepository.save(customer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<CustomerWithChild> foundCustomer = this.customerWithChildRepository.findByChild(null);
				Assertions.assertTrue(foundCustomer.isEmpty());
			}
		);
	}
	
	@Test
	void findByChildWhenNullExists()
	{
		final CustomerWithChild customer = new CustomerWithChild(TestData.FIRST_NAME, TestData.LAST_NAME, null);
		this.customerWithChildRepository.save(customer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<CustomerWithChild> foundCustomer = this.customerWithChildRepository.findByChild(null);
				Assertions.assertTrue(foundCustomer.isPresent());
			}
		);
	}
}
