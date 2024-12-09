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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.TestConfiguration;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.simple.model.Customer;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.simple.model.CustomerAsRecord;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.simple.model.CustomerAsRecordRepository;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.simple.model.CustomerNotCrud;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.simple.model.CustomerNotCrudRepository;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.simple.model.CustomerRepository;


public abstract class SimpleSingleTest
{
	private final CustomerRepository repository;
	private final CustomerAsRecordRepository recordRepository;
	private final CustomerNotCrudRepository notCrudRepository;
	private final TestConfiguration configuration;
	
	public SimpleSingleTest(
		final CustomerRepository repository,
		final CustomerAsRecordRepository recordRepository,
		final CustomerNotCrudRepository notCrudRepository,
		final TestConfiguration configuration)
	{
		this.repository = repository;
		this.recordRepository = recordRepository;
		this.notCrudRepository = notCrudRepository;
		this.configuration = configuration;
	}
	
	@Test
	void testNullFindAll()
	{
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<Customer> customers = TestUtil.iterableToList(this.repository.findAll());
				Assertions.assertTrue(customers.isEmpty());
			}
		);
	}
	
	@Test
	void testBasicSaveAndFindSingleRecords()
	{
		final CustomerAsRecord customer = new CustomerAsRecord(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.recordRepository.save(customer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerAsRecord> customers = TestUtil.iterableToList(this.recordRepository.findAll());
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(customer, customers.get(0));
			}
		);
	}
	
	@Test
	void testBasicSaveAndFindMultipleRecords()
	{
		final CustomerAsRecord customer = new CustomerAsRecord(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.recordRepository.save(customer);
		final CustomerAsRecord customer2 = new CustomerAsRecord(null, null);
		this.recordRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerAsRecord> customers = TestUtil.iterableToList(this.recordRepository.findAll());
				final CustomerAsRecord foundCustomer =
					customers.stream().filter(c -> TestData.FIRST_NAME.equals(c.firstName())).findFirst().get();
				final CustomerAsRecord foundCustomer2 =
					customers.stream().filter(c -> c.firstName() == null).findFirst().get();
				Assertions.assertEquals(2, customers.size());
				Assertions.assertEquals(customer, foundCustomer);
				Assertions.assertEquals(customer2, foundCustomer2);
			}
		);
	}
	
	@Test
	void testBasicSaveAndFindByFirstNameRecords()
	{
		final CustomerAsRecord customer = new CustomerAsRecord(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.recordRepository.save(customer);
		final CustomerAsRecord customer2 =
			new CustomerAsRecord(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		this.recordRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<CustomerAsRecord> foundCustomer =
					this.recordRepository.findByFirstName(TestData.FIRST_NAME);
				Assertions.assertTrue(foundCustomer.isPresent());
				Assertions.assertEquals(customer, foundCustomer.get());
			}
		);
	}
	
	@SuppressWarnings("DataFlowIssue")
	@Test
	void testNullSave()
	{
		Assertions.assertThrows(IllegalArgumentException.class, () -> this.repository.save(null));
	}
	
	@SuppressWarnings("DataFlowIssue")
	@Test
	void testNullSaveAll()
	{
		Assertions.assertThrows(IllegalArgumentException.class, () -> this.repository.saveAll(null));
	}
	
	@Test
	void testNullSaveAllWithList()
	{
		final Customer customer = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		final List<Customer> listWithNullElement = new ArrayList<>();
		listWithNullElement.add(customer);
		listWithNullElement.add(null);
		Assertions.assertDoesNotThrow(() -> this.repository.saveAll(listWithNullElement));
	}
	
	@Test
	void testBasicSaveAndFindAll()
	{
		final Customer customer = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.repository.save(customer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<Customer> customers = TestUtil.iterableToList(this.repository.findAll());
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(customer, customers.get(0));
			}
		);
	}
	
	@Test
	void testDoubleSaveAndFindAll()
	{
		final Customer customer = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.repository.save(customer);
		customer.setFirstName(TestData.FIRST_NAME_ALTERNATIVE);
		this.repository.save(customer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<Customer> foundCustomer =
					this.repository.findByFirstName(TestData.FIRST_NAME_ALTERNATIVE);
				Assertions.assertTrue(foundCustomer.isPresent());
			}
		);
	}
	
	@Test
	void testBasicSaveAndFindAllWithNotCrudRepository()
	{
		final CustomerNotCrud customer = new CustomerNotCrud(TestData.FIRST_NAME, TestData.LAST_NAME);
		notCrudRepository.save(customer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerNotCrud> customers = TestUtil.iterableToList(notCrudRepository.findAll());
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(customer, customers.get(0));
			}
		);
	}
	
	@Test
	void testMultipleSaveAndFindAll()
	{
		final Customer customer1 = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.repository.save(customer1);
		final Customer customer2 = new Customer(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		this.repository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<Customer> customers = TestUtil.iterableToList(this.repository.findAll());
				Assertions.assertEquals(2, customers.size());
				final Optional<Customer> firstCustomer =
					customers.stream().filter(customer -> customer.equals(customer1)).findFirst();
				Assertions.assertTrue(firstCustomer.isPresent());
				final Optional<Customer> secondCustomer =
					customers.stream().filter(customer -> customer.equals(customer2)).findFirst();
				Assertions.assertTrue(secondCustomer.isPresent());
			}
		);
	}
	
	@Test
	void testBasicRemove()
	{
		final Customer customer = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.repository.save(customer);
		this.repository.delete(customer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<Customer> customers = TestUtil.iterableToList(this.repository.findAll());
				Assertions.assertTrue(customers.isEmpty());
			}
		);
	}
	
	@Test
	void testBasicChange()
	{
		// Save default customer
		final Customer customer = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.repository.save(customer);
		// Change customer
		customer.setFirstName(TestData.FIRST_NAME_ALTERNATIVE);
		customer.setLastName(TestData.LAST_NAME_ALTERNATIVE);
		// Save changed customer
		this.repository.save(customer);
		// Check saved customer
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<Customer> customers = TestUtil.iterableToList(this.repository.findAll());
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(TestData.FIRST_NAME_ALTERNATIVE, customers.get(0).getFirstName());
				Assertions.assertEquals(TestData.LAST_NAME_ALTERNATIVE, customers.get(0).getLastName());
			}
		);
	}
	
	@Test
	void testRestart()
	{
		TestUtil.restartDatastore(this.configuration);
		final List<Customer> customers = TestUtil.iterableToList(this.repository.findAll());
		Assertions.assertTrue(customers.isEmpty());
	}
	
	@Test
	void testSaveWithRestart()
	{
		final Customer customer = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.repository.save(customer);
		TestUtil.restartDatastore(this.configuration);
		// Automatically restarts if needed
		final List<Customer> customers = TestUtil.iterableToList(this.repository.findAll());
		Assertions.assertEquals(1, customers.size());
		Assertions.assertEquals(customer, customers.get(0));
	}
}
