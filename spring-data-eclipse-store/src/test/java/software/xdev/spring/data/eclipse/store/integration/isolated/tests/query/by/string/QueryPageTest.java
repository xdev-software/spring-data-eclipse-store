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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {QueryTestConfiguration.class})
class QueryPageTest
{
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private QueryTestConfiguration configuration;
	
	@Test
	void pageableFindAllTwoPages()
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
	void pageableFindAllTwoPagesWithNextPageable()
	{
		final Customer customer1 = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.customerRepository.save(customer1);
		final Customer customer2 = new Customer(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		this.customerRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Page<Customer> customersPage1 = this.customerRepository.findAll(PageRequest.of(0, 1));
				Assertions.assertEquals(1, customersPage1.getContent().size());
				
				final List<Customer> customersPage2 =
					TestUtil.iterableToList(this.customerRepository.findAll(customersPage1.nextPageable()));
				Assertions.assertEquals(1, customersPage2.size());
				
				Assertions.assertNotEquals(customersPage1.getContent().get(0), customersPage2.get(0));
			}
		);
	}
	
	@Test
	void pageableFindAllUnpaged()
	{
		final Customer customer1 = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.customerRepository.save(customer1);
		final Customer customer2 = new Customer(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		this.customerRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Pageable pageable = Pageable.unpaged();
				final List<Customer> customersPage =
					TestUtil.iterableToList(this.customerRepository.findAll(pageable));
				Assertions.assertEquals(2, customersPage.size());
				Assertions.assertNotEquals(customersPage.get(0), customersPage.get(1));
			}
		);
	}
	
	@Test
	void pageableFindAllOnePage()
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
	void pageableFindByLastName()
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
	void pageableFindByFirstNameWithList()
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
}
