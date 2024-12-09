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
package software.xdev.spring.data.eclipse.store.integration.shared.tests;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.shared.DefaultTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.shared.SharedTestConfiguration;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.ChildCustomer;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.ChildCustomerRepository;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.ParentCustomer;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.ParentCustomerRepository;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.SubCustomer;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.SubCustomerRepository;


@DefaultTestAnnotations
class InheritanceTest
{
	private final ParentCustomerRepository parentCustomerRepository;
	private final ChildCustomerRepository childCustomerRepository;
	private final SubCustomerRepository subCustomerRepository;
	private final SharedTestConfiguration configuration;
	
	@Autowired
	public InheritanceTest(
		final ParentCustomerRepository parentCustomerRepository,
		final ChildCustomerRepository childCustomerRepository,
		final SubCustomerRepository subCustomerRepository,
		final SharedTestConfiguration configuration)
	{
		this.parentCustomerRepository = parentCustomerRepository;
		this.childCustomerRepository = childCustomerRepository;
		this.subCustomerRepository = subCustomerRepository;
		this.configuration = configuration;
	}
	
	@Test
	void testSaveChildFindParent()
	{
		final ChildCustomer customer1 =
			new ChildCustomer(TestData.FIRST_NAME, TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME);
		this.childCustomerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<ParentCustomer> parentCustomers =
					TestUtil.iterableToList(this.parentCustomerRepository.findAll());
				Assertions.assertEquals(1, parentCustomers.size());
				Assertions.assertEquals(TestData.FIRST_NAME, parentCustomers.get(0).getFirstName());
				
				final List<ChildCustomer> childCustomers =
					TestUtil.iterableToList(this.childCustomerRepository.findAll());
				Assertions.assertEquals(1, childCustomers.size());
				Assertions.assertEquals(TestData.FIRST_NAME, childCustomers.get(0).getFirstName());
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
			this.configuration,
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
	void testAddChildAndParent()
	{
		final ChildCustomer customer1 =
			new ChildCustomer(TestData.FIRST_NAME, TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME);
		this.childCustomerRepository.save(customer1);
		
		final ParentCustomer parentCustomer =
			new ParentCustomer(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		this.parentCustomerRepository.save(parentCustomer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<ParentCustomer> parentCustomers =
					TestUtil.iterableToList(this.parentCustomerRepository.findAll());
				Assertions.assertEquals(2, parentCustomers.size());
			}
		);
	}
	
	@Test
	void testRemoveOnlyChildFindParent()
	{
		final ChildCustomer customer1 =
			new ChildCustomer(TestData.FIRST_NAME, TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME);
		this.childCustomerRepository.save(customer1);
		
		final ParentCustomer parentCustomer =
			new ParentCustomer(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		this.parentCustomerRepository.save(parentCustomer);
		
		this.childCustomerRepository.delete(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<ParentCustomer> parentCustomers =
					TestUtil.iterableToList(this.parentCustomerRepository.findAll());
				Assertions.assertEquals(1, parentCustomers.size());
				
				final List<ChildCustomer> childCustomers =
					TestUtil.iterableToList(this.childCustomerRepository.findAll());
				Assertions.assertTrue(childCustomers.isEmpty());
			}
		);
	}
	
	@Test
	void testRemoveOnlyParentFindParent()
	{
		final ChildCustomer customer1 =
			new ChildCustomer(TestData.FIRST_NAME, TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME);
		this.childCustomerRepository.save(customer1);
		
		final ParentCustomer parentCustomer =
			new ParentCustomer(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		this.parentCustomerRepository.save(parentCustomer);
		
		this.parentCustomerRepository.delete(parentCustomer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<ParentCustomer> parentCustomers =
					TestUtil.iterableToList(this.parentCustomerRepository.findAll());
				Assertions.assertEquals(1, parentCustomers.size());
				
				final List<ChildCustomer> childCustomers =
					TestUtil.iterableToList(this.childCustomerRepository.findAll());
				Assertions.assertEquals(1, childCustomers.size());
			}
		);
	}
	
	@Test
	void testRemoveAllChildren()
	{
		final ChildCustomer customer1 =
			new ChildCustomer(TestData.FIRST_NAME, TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME);
		this.childCustomerRepository.save(customer1);
		
		final ParentCustomer parentCustomer =
			new ParentCustomer(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		this.parentCustomerRepository.save(parentCustomer);
		
		this.childCustomerRepository.deleteAll();
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<ParentCustomer> parentCustomers =
					TestUtil.iterableToList(this.parentCustomerRepository.findAll());
				Assertions.assertEquals(1, parentCustomers.size());
				
				final List<ChildCustomer> childCustomers =
					TestUtil.iterableToList(this.childCustomerRepository.findAll());
				Assertions.assertTrue(childCustomers.isEmpty());
			}
		);
	}
	
	@Test
	void testRemoveAllParents()
	{
		final ChildCustomer customer1 =
			new ChildCustomer(TestData.FIRST_NAME, TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME);
		this.childCustomerRepository.save(customer1);
		
		final ParentCustomer parentCustomer =
			new ParentCustomer(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		this.parentCustomerRepository.save(parentCustomer);
		
		this.parentCustomerRepository.deleteAll();
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<ParentCustomer> parentCustomers =
					TestUtil.iterableToList(this.parentCustomerRepository.findAll());
				Assertions.assertEquals(1, parentCustomers.size());
				
				final List<ChildCustomer> childCustomers =
					TestUtil.iterableToList(this.childCustomerRepository.findAll());
				Assertions.assertEquals(1, childCustomers.size());
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
			this.configuration,
			() -> {
				final List<ParentCustomer> parentCustomers =
					TestUtil.iterableToList(this.parentCustomerRepository.findAll());
				Assertions.assertEquals(1, parentCustomers.size());
				Assertions.assertEquals(TestData.FIRST_NAME_ALTERNATIVE, parentCustomers.get(0).getFirstName());
				
				final List<ChildCustomer> childCustomers =
					TestUtil.iterableToList(this.childCustomerRepository.findAll());
				Assertions.assertEquals(1, childCustomers.size());
				Assertions.assertEquals(TestData.FIRST_NAME_ALTERNATIVE, childCustomers.get(0).getFirstName());
			}
		);
	}
	
	@Test
	void testRecursiveFindByFirstNameOneResult()
	{
		final SubCustomer customer1 = new SubCustomer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.subCustomerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final SubCustomer foundCustomer = this.subCustomerRepository.findByFirstName(TestData.FIRST_NAME);
				Assertions.assertNotNull(foundCustomer);
				Assertions.assertEquals(customer1, foundCustomer);
			}
		);
	}
}
