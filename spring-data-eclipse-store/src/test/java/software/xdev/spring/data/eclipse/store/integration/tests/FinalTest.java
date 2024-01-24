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

import jakarta.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.DefaultTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.repositories.CustomerWithFinal;
import software.xdev.spring.data.eclipse.store.integration.repositories.CustomerWithFinalChild;
import software.xdev.spring.data.eclipse.store.integration.repositories.CustomerWithFinalChildRepository;
import software.xdev.spring.data.eclipse.store.integration.repositories.CustomerWithFinalRepository;
import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;


@DefaultTestAnnotations
class FinalTest
{
	@Inject
	private CustomerWithFinalRepository repository;
	@Inject
	private CustomerWithFinalChildRepository withChildRepository;
	
	@Inject
	private EclipseStoreStorage storage;
	
	@Test
	void testSaveAndFindSingle()
	{
		final CustomerWithFinal customer = new CustomerWithFinal(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.repository.save(customer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final List<CustomerWithFinal> customers = TestUtil.iterableToList(this.repository.findAll());
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(TestData.FIRST_NAME, customers.get(0).getFirstName());
			}
		);
	}
	
	@Test
	void testSaveAndFindSingleWithChild()
	{
		final CustomerWithFinalChild originalCustomer =
			new CustomerWithFinalChild(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.withChildRepository.save(originalCustomer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final List<CustomerWithFinalChild> customers =
					TestUtil.iterableToList(this.withChildRepository.findAll());
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(TestData.FIRST_NAME, customers.get(0).getFirstName());
				Assertions.assertEquals(
					originalCustomer.getChild().getFirstName(),
					customers.get(0).getChild().getFirstName());
			}
		);
	}
}
