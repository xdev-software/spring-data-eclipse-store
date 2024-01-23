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
package software.xdev.spring.data.eclipse.store.integration.tests;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.DefaultTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.repositories.Customer;
import software.xdev.spring.data.eclipse.store.integration.repositories.CustomerRepository;
import software.xdev.spring.data.eclipse.store.integration.repositories.Owner;
import software.xdev.spring.data.eclipse.store.integration.repositories.OwnerRepository;
import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;


@DefaultTestAnnotations
class SimpleMultipleTest
{
	@Inject
	private CustomerRepository customerRepository;
	@Inject
	private OwnerRepository ownerRepository;
	@Inject
	private EclipseStoreStorage storage;
	
	@Test
	void testBasicSaveAndFindAll()
	{
		final Customer
			customer = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.customerRepository.save(customer);
		
		final Owner
			owner = new Owner(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.ownerRepository.save(owner);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final List<Customer> customers = TestUtil.iterableToList(this.customerRepository.findAll());
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(customer, customers.get(0));
				
				final List<Owner> owners = TestUtil.iterableToList(this.ownerRepository.findAll());
				Assertions.assertEquals(1, owners.size());
				Assertions.assertEquals(owner, owners.get(0));
			}
		);
	}
	
	@Test
	void testMultipleSaveAndFindAll()
	{
		final Customer
			customer1 = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.customerRepository.save(customer1);
		final Customer
			customer2 = new Customer(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		this.customerRepository.save(customer2);
		
		final Owner
			owner1 = new Owner(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.ownerRepository.save(owner1);
		final Owner
			owner2 = new Owner(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		this.ownerRepository.save(owner2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final List<Customer> customers = TestUtil.iterableToList(this.customerRepository.findAll());
				Assertions.assertEquals(2, customers.size());
				Assertions.assertEquals(customer1, Customer.getCustomerWithFirstName(customers, TestData.FIRST_NAME));
				Assertions.assertEquals(
					customer2,
					Customer.getCustomerWithFirstName(customers, TestData.FIRST_NAME_ALTERNATIVE));
				
				final List<Owner> owners = TestUtil.iterableToList(this.ownerRepository.findAll());
				Assertions.assertEquals(2, owners.size());
				Assertions.assertEquals(owner1, Owner.getOwnerWithFirstName(owners, TestData.FIRST_NAME));
				Assertions.assertEquals(owner2, Owner.getOwnerWithFirstName(owners, TestData.FIRST_NAME_ALTERNATIVE));
			}
		);
	}
	
	@Test
	void testBasicRemove()
	{
		final Customer
			customer = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.customerRepository.save(customer);
		this.customerRepository.delete(customer);
		
		final Owner
			owner = new Owner(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.ownerRepository.save(owner);
		this.ownerRepository.delete(owner);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final List<Customer> customers = TestUtil.iterableToList(this.customerRepository.findAll());
				Assertions.assertTrue(customers.isEmpty());
				
				final List<Owner> owners = TestUtil.iterableToList(this.ownerRepository.findAll());
				Assertions.assertTrue(owners.isEmpty());
			}
		);
	}
	
	@Test
	void testBasicChange()
	{
		// Save default customer
		final Customer
			customer = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.customerRepository.save(customer);
		// Change customer
		customer.setFirstName(TestData.FIRST_NAME_ALTERNATIVE);
		customer.setLastName(TestData.LAST_NAME_ALTERNATIVE);
		// Save changed customer
		this.customerRepository.save(customer);
		
		// Save default customer
		final Owner
			owner = new Owner(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.ownerRepository.save(owner);
		// Change customer
		owner.setFirstName(TestData.FIRST_NAME_ALTERNATIVE);
		owner.setLastName(TestData.LAST_NAME_ALTERNATIVE);
		// Save changed customer
		this.ownerRepository.save(owner);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				// Check saved customer
				final List<Customer> customers = TestUtil.iterableToList(this.customerRepository.findAll());
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(TestData.FIRST_NAME_ALTERNATIVE, customers.get(0).getFirstName());
				Assertions.assertEquals(TestData.LAST_NAME_ALTERNATIVE, customers.get(0).getLastName());
				
				// Check saved customer
				final List<Owner> owners = TestUtil.iterableToList(this.ownerRepository.findAll());
				Assertions.assertEquals(1, owners.size());
				Assertions.assertEquals(TestData.FIRST_NAME_ALTERNATIVE, owners.get(0).getFirstName());
				Assertions.assertEquals(TestData.LAST_NAME_ALTERNATIVE, owners.get(0).getLastName());
			}
		);
	}
}
