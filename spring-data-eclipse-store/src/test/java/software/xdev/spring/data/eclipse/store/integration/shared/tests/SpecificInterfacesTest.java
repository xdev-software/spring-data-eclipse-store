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
import org.springframework.data.domain.Sort;

import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.shared.DefaultTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.shared.SharedTestConfiguration;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.interfaces.CustomerCrud;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.interfaces.CustomerEclipseStoreCrudRepository;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.interfaces.CustomerEclipseStoreListCrudRepository;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.interfaces.CustomerEclipseStoreListPagingAndSortingRepository;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.interfaces.CustomerEclipseStorePagingAndSortingRepository;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.interfaces.CustomerEclipseStoreRepository;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.interfaces.CustomerListCrud;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.interfaces.CustomerListPagingAndSorting;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.interfaces.CustomerPagingAndSorting;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.interfaces.CustomerSimple;


@DefaultTestAnnotations
class SpecificInterfacesTest
{
	@Autowired
	private CustomerEclipseStoreRepository repository;
	@Autowired
	private CustomerEclipseStoreCrudRepository crudRepository;
	@Autowired
	private CustomerEclipseStoreListCrudRepository listCrudRepository;
	@Autowired
	private CustomerEclipseStorePagingAndSortingRepository pagingAndSortingRepository;
	@Autowired
	private CustomerEclipseStoreListPagingAndSortingRepository listPagingAndSortingRepository;
	
	@Autowired
	private SharedTestConfiguration configuration;
	
	@Test
	void testSimple()
	{
		final CustomerSimple customer = new CustomerSimple(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.repository.save(customer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerSimple> customers = this.repository.findAll();
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(customer, customers.get(0));
			}
		);
	}
	
	@Test
	void testCrud()
	{
		final CustomerCrud customer = new CustomerCrud(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.crudRepository.save(customer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerCrud> customers = TestUtil.iterableToList(this.crudRepository.findAll());
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(customer, customers.get(0));
			}
		);
	}
	
	@Test
	void testListCrud()
	{
		final CustomerListCrud customer = new CustomerListCrud(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.listCrudRepository.save(customer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerListCrud> customers = this.listCrudRepository.findAll();
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(customer, customers.get(0));
			}
		);
	}
	
	@Test
	void testPagingAndSorting()
	{
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerPagingAndSorting> customers =
					TestUtil.iterableToList(this.pagingAndSortingRepository.findAll(
						Sort.unsorted()));
				Assertions.assertTrue(customers.isEmpty());
			}
		);
	}
	
	@Test
	void testListPagingAndSorting()
	{
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerListPagingAndSorting> customers = this.listPagingAndSortingRepository.findAll(
					Sort.unsorted());
				Assertions.assertTrue(customers.isEmpty());
			}
		);
	}
}
