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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.DefaultTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.repositories.CustomerRepositoryWithHashSet;
import software.xdev.spring.data.eclipse.store.integration.repositories.CustomerWithHashSet;
import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;


@DefaultTestAnnotations
class HashSetTest
{
	@Inject
	private CustomerRepositoryWithHashSet repository;
	
	@Inject
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
	
	// TODO: Fix this.
	@Test
	@Disabled("Is not fixed yet, but will be.")
	void testDoubleSaveAndFindAll()
	{
		final CustomerWithHashSet customer = new CustomerWithHashSet(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.repository.save(customer);
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
