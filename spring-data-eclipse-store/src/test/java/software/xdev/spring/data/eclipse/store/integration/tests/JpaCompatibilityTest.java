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
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.DefaultTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.repositories.CustomerRepositoryWithQuery;
import software.xdev.spring.data.eclipse.store.integration.repositories.CustomerWithQuery;
import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;


@DefaultTestAnnotations
class JpaCompatibilityTest
{
	@Autowired
	private CustomerRepositoryWithQuery customerRepository;
	
	@Autowired
	private EclipseStoreStorage storage;
	
	@Test
	void testUseQueryAnnotationFindAll()
	{
		final CustomerWithQuery customer1 = new CustomerWithQuery(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.customerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final List<CustomerWithQuery> customers = TestUtil.iterableToList(this.customerRepository.findAll());
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(customer1, customers.get(0));
				Assertions.assertNotSame(customer1, customers.get(0));
			}
		);
	}
	
	@Test
	void testUseQueryAnnotationFindByFirstName()
	{
		final CustomerWithQuery customer1 = new CustomerWithQuery(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.customerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final Optional<CustomerWithQuery> foundCustomer =
					this.customerRepository.findByFirstName(TestData.FIRST_NAME);
				Assertions.assertTrue(foundCustomer.isPresent());
				Assertions.assertEquals(customer1, foundCustomer.get());
				Assertions.assertNotSame(customer1, foundCustomer.get());
			}
		);
	}
	
	@Test
	void testUseQueryAnnotationFindAllByLastName()
	{
		final CustomerWithQuery customer1 = new CustomerWithQuery(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.customerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final List<CustomerWithQuery> customers =
					TestUtil.iterableToList(this.customerRepository.findAllByLastName(TestData.LAST_NAME));
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(customer1, customers.get(0));
				Assertions.assertNotSame(customer1, customers.get(0));
			}
		);
	}
}