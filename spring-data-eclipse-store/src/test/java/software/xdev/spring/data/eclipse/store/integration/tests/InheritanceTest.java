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
import software.xdev.spring.data.eclipse.store.integration.repositories.ChildCustomer;
import software.xdev.spring.data.eclipse.store.integration.repositories.ChildCustomerRepository;
import software.xdev.spring.data.eclipse.store.integration.repositories.ParentCustomer;
import software.xdev.spring.data.eclipse.store.integration.repositories.ParentCustomerRepository;
import software.xdev.spring.data.eclipse.store.integration.repositories.SubCustomer;
import software.xdev.spring.data.eclipse.store.integration.repositories.SubCustomerRepository;
import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;


@DefaultTestAnnotations
class InheritanceTest
{
	@Inject
	private ParentCustomerRepository parentCustomerRepository;
	@Inject
	private ChildCustomerRepository childCustomerRepository;
	@Inject
	private SubCustomerRepository subCustomerRepository;
	@Inject
	private EclipseStoreStorage storage;
	
	@Test
	void testSaveChildFindParent()
	{
		final ChildCustomer customer1 =
			new ChildCustomer(TestData.FIRST_NAME, TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME);
		this.childCustomerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final List<ParentCustomer> parentCustomers =
					TestUtil.iterableToList(this.parentCustomerRepository.findAll());
				Assertions.assertEquals(1, parentCustomers.size());
				Assertions.assertEquals(parentCustomers.get(0).getFirstName(), TestData.FIRST_NAME);
				
				final List<ChildCustomer> childCustomers =
					TestUtil.iterableToList(this.childCustomerRepository.findAll());
				Assertions.assertEquals(1, childCustomers.size());
				Assertions.assertEquals(childCustomers.get(0).getFirstName(), TestData.FIRST_NAME);
			}
		);
	}
	
	@Test
	void testRemoveChildFindParent()
	{
		final ChildCustomer customer1 =
			new ChildCustomer(TestData.FIRST_NAME, TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME);
		this.childCustomerRepository.save(customer1);
		this.childCustomerRepository.delete(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final List<ParentCustomer> parentCustomers =
					TestUtil.iterableToList(this.parentCustomerRepository.findAll());
				Assertions.assertTrue(parentCustomers.isEmpty());
				
				final List<ChildCustomer> childCustomers =
					TestUtil.iterableToList(this.childCustomerRepository.findAll());
				Assertions.assertTrue(childCustomers.isEmpty());
			}
		);
	}
	
	@Test
	void testChangeChildFindParent()
	{
		// Save customer
		final ChildCustomer customer1 =
			new ChildCustomer(TestData.FIRST_NAME, TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME);
		this.childCustomerRepository.save(customer1);
		// Change customer
		final ChildCustomer foundCustomer =
			TestUtil.iterableToList(this.childCustomerRepository.findAll()).stream().findFirst().get();
		foundCustomer.setFirstName(TestData.FIRST_NAME_ALTERNATIVE);
		this.childCustomerRepository.save(foundCustomer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final List<ParentCustomer> parentCustomers =
					TestUtil.iterableToList(this.parentCustomerRepository.findAll());
				Assertions.assertEquals(1, parentCustomers.size());
				Assertions.assertEquals(parentCustomers.get(0).getFirstName(), TestData.FIRST_NAME_ALTERNATIVE);
				
				final List<ChildCustomer> childCustomers =
					TestUtil.iterableToList(this.childCustomerRepository.findAll());
				Assertions.assertEquals(1, childCustomers.size());
				Assertions.assertEquals(childCustomers.get(0).getFirstName(), TestData.FIRST_NAME_ALTERNATIVE);
			}
		);
	}
	
	@Test
	void testRecursiveFindByFirstNameOneResult()
	{
		final SubCustomer customer1 = new SubCustomer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.subCustomerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final SubCustomer foundCustomer = this.subCustomerRepository.findByFirstName(TestData.FIRST_NAME);
				Assertions.assertNotNull(foundCustomer);
				Assertions.assertEquals(customer1, foundCustomer);
			}
		);
	}
}
