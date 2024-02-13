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
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.DefaultTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.repositories.CustomerRepositoryWithHashSet;
import software.xdev.spring.data.eclipse.store.integration.repositories.CustomerRepositoryWithNonFinalHashSet;
import software.xdev.spring.data.eclipse.store.integration.repositories.CustomerWithHashSet;
import software.xdev.spring.data.eclipse.store.integration.repositories.CustomerWithNonFinalHashSet;
import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;


@DefaultTestAnnotations
class HashSetTest
{
	@Autowired
	private CustomerRepositoryWithHashSet repository;
	@Autowired
	private CustomerRepositoryWithNonFinalHashSet nonFinalRepository;
	
	@Autowired
	private EclipseStoreStorage storage;
	
	@Test
	void testSaveAndFindAll()
	{
		final CustomerWithHashSet customer = new CustomerWithHashSet(TestData.FIRST_NAME, TestData.LAST_NAME);
		customer.getValues().add("Test");
		this.repository.save(customer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final List<CustomerWithHashSet> customers = TestUtil.iterableToList(this.repository.findAll());
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(1, customers.get(0).getValues().size());
			}
		);
	}
	
	@Test
	void testSaveAndResaveFindAll()
	{
		final CustomerWithHashSet customer = new CustomerWithHashSet(TestData.FIRST_NAME, TestData.LAST_NAME);
		customer.getValues().add("Test");
		this.repository.save(customer);
		
		final CustomerWithHashSet customerWithHashSet =
			TestUtil.iterableToList(this.repository.findAll()).stream().findFirst().get();
		customerWithHashSet.getValues().add("Test2");
		this.repository.save(customerWithHashSet);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final List<CustomerWithHashSet> customers = TestUtil.iterableToList(this.repository.findAll());
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(2, customers.get(0).getValues().size());
			}
		);
	}
	
	@Test
	void testSaveAndFindAllNonFinal()
	{
		final CustomerWithNonFinalHashSet customer =
			new CustomerWithNonFinalHashSet(TestData.FIRST_NAME, TestData.LAST_NAME);
		customer.getValues().add("Test");
		this.nonFinalRepository.save(customer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final List<CustomerWithNonFinalHashSet> customers =
					TestUtil.iterableToList(this.nonFinalRepository.findAll());
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(1, customers.get(0).getValues().size());
			}
		);
	}
	
	@Test
	void testSaveAndSetAndFindAllNonFinal()
	{
		final CustomerWithNonFinalHashSet customer =
			new CustomerWithNonFinalHashSet(TestData.FIRST_NAME, TestData.LAST_NAME);
		customer.setValues(Set.of("Test"));
		this.nonFinalRepository.save(customer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final List<CustomerWithNonFinalHashSet> customers =
					TestUtil.iterableToList(this.nonFinalRepository.findAll());
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(1, customers.get(0).getValues().size());
			}
		);
	}
	
	@Test
	void testSaveAndResetAndFindAllNonFinal()
	{
		final CustomerWithNonFinalHashSet customer =
			new CustomerWithNonFinalHashSet(TestData.FIRST_NAME, TestData.LAST_NAME);
		customer.setValues(Set.of("Test"));
		this.nonFinalRepository.save(customer);
		
		final CustomerWithNonFinalHashSet customerWithHashSet =
			TestUtil.iterableToList(this.nonFinalRepository.findAll()).stream().findFirst().get();
		customerWithHashSet.setValues(Set.of("Test", "Test2"));
		this.nonFinalRepository.save(customerWithHashSet);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final List<CustomerWithNonFinalHashSet> customers =
					TestUtil.iterableToList(this.nonFinalRepository.findAll());
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(2, customers.get(0).getValues().size());
			}
		);
	}
	
	@Test
	void testSaveAndSetNullAndFindAllNonFinal()
	{
		final CustomerWithNonFinalHashSet customer =
			new CustomerWithNonFinalHashSet(TestData.FIRST_NAME, TestData.LAST_NAME);
		customer.setValues(null);
		this.nonFinalRepository.save(customer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final List<CustomerWithNonFinalHashSet> customers =
					TestUtil.iterableToList(this.nonFinalRepository.findAll());
				Assertions.assertEquals(1, customers.size());
				Assertions.assertNull(customers.get(0).getValues());
			}
		);
	}
	
	@Test
	void testDoubleSaveWithNewInstanceAndFindAll()
	{
		final CustomerWithHashSet customer = new CustomerWithHashSet(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.repository.save(customer);
		
		final CustomerWithHashSet reloadedCustomer = TestUtil.iterableToList(this.repository.findAll()).get(0);
		reloadedCustomer.getValues().add("Test");
		this.repository.save(reloadedCustomer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final List<CustomerWithHashSet> customers = TestUtil.iterableToList(this.repository.findAll());
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(1, customers.get(0).getValues().size());
			}
		);
	}
}
