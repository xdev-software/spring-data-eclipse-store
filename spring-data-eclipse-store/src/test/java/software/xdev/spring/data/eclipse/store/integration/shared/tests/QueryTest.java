/*
 * Copyright © 2023 XDEV Software (https://xdev.software)
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
package software.xdev.spring.data.eclipse.store.integration.shared.tests;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.shared.DefaultTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.shared.TestConfiguration;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.Child;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.Customer;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.CustomerRepository;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.CustomerWithChild;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.CustomerWithChildRepository;


@DefaultTestAnnotations
class QueryTest
{
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private CustomerWithChildRepository customerWithChildRepository;
	
	@Autowired
	private TestConfiguration configuration;
	
	@Test
	void testBasicFindAllByLastNameTwoResults()
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
	void testBasicFindAllByLastNameOneResult()
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
	void testBasicFindByFirstNameOneResult()
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
	void testPageableFindAllTwoPages()
	{
		final Customer customer1 = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.customerRepository.save(customer1);
		final Customer customer2 = new Customer(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		this.customerRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Pageable pageable = PageRequest.of(0, 1);
				final List<Customer> customersPage1 =
					TestUtil.iterableToList(this.customerRepository.findAll(pageable));
				Assertions.assertEquals(1, customersPage1.size());
				
				final List<Customer> customersPage2 =
					TestUtil.iterableToList(this.customerRepository.findAll(pageable.next()));
				Assertions.assertEquals(1, customersPage2.size());
				
				Assertions.assertNotEquals(customersPage1.get(0), customersPage2.get(0));
			}
		);
	}
	
	@Test
	void testPageableFindAllUnpaged()
	{
		final Customer customer1 = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.customerRepository.save(customer1);
		final Customer customer2 = new Customer(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		this.customerRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Pageable pageable = Pageable.unpaged();
				final List<Customer> customersPage = TestUtil.iterableToList(this.customerRepository.findAll(pageable));
				Assertions.assertEquals(2, customersPage.size());
				Assertions.assertNotEquals(customersPage.get(0), customersPage.get(1));
			}
		);
	}
	
	@Test
	void testPageableFindAllOnePage()
	{
		final Customer customer1 = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.customerRepository.save(customer1);
		final Customer customer2 = new Customer(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		this.customerRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Pageable pageable = PageRequest.of(0, 2);
				final List<Customer> customersPage1 =
					TestUtil.iterableToList(this.customerRepository.findAll(pageable));
				Assertions.assertEquals(2, customersPage1.size());
				Assertions.assertEquals(
					customer1,
					Customer.getCustomerWithFirstName(customersPage1, TestData.FIRST_NAME));
				Assertions.assertEquals(
					customer2,
					Customer.getCustomerWithFirstName(customersPage1, TestData.FIRST_NAME_ALTERNATIVE));
			}
		);
	}
	
	@Test
	void testPageableFindByLastName()
	{
		final Customer customer1 = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.customerRepository.save(customer1);
		final Customer customer2 = new Customer(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		this.customerRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Pageable pageable = PageRequest.of(0, 10);
				final List<Customer> customersPage =
					TestUtil.iterableToList(this.customerRepository.findAllByLastName(TestData.LAST_NAME, pageable));
				Assertions.assertEquals(1, customersPage.size());
				Assertions.assertEquals(customer1, customersPage.get(0));
			}
		);
	}
	
	@Test
	void testPageableFindByFirstNameWithList()
	{
		final Customer customer1 = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.customerRepository.save(customer1);
		final Customer customer2 = new Customer(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		this.customerRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Pageable pageable = PageRequest.of(0, 10);
				final List<Customer> customersPage =
					TestUtil.iterableToList(this.customerRepository.findAllByFirstName(TestData.FIRST_NAME, pageable));
				Assertions.assertEquals(1, customersPage.size());
				Assertions.assertEquals(customer1, customersPage.get(0));
			}
		);
	}
	
	@Test
	void testSortFindByLastNameDefault()
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
	void testSortFindByFirstNameAscending()
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
	void testSortFindByFirstNameAscendingVariant()
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
	void testFindByOrderByLastNameAsc()
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
	void testSortFindByLastNameDescending()
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
	
	@Test
	void testFindByChildExistingChild()
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
	void testFindByChildNewChild()
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
	void testFindByChildWhenNullNotExists()
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
	void testFindByChildWhenNullExists()
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
