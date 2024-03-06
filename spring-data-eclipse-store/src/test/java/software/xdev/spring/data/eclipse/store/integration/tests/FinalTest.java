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
import org.springframework.beans.factory.annotation.Autowired;

import software.xdev.spring.data.eclipse.store.exceptions.IdFieldFinalException;
import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.DefaultTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.repositories.immutables.CustomerWithFinal;
import software.xdev.spring.data.eclipse.store.integration.repositories.immutables.CustomerWithFinalChild;
import software.xdev.spring.data.eclipse.store.integration.repositories.immutables.CustomerWithFinalChildRepository;
import software.xdev.spring.data.eclipse.store.integration.repositories.immutables.CustomerWithFinalId;
import software.xdev.spring.data.eclipse.store.integration.repositories.immutables.CustomerWithFinalIdRepository;
import software.xdev.spring.data.eclipse.store.integration.repositories.immutables.CustomerWithFinalRepository;
import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;


@DefaultTestAnnotations
class FinalTest
{
	@Autowired
	CustomerWithFinalRepository repository;
	@Autowired
	CustomerWithFinalChildRepository withChildRepository;
	@Autowired
	CustomerWithFinalIdRepository withFinalIdRepository;
	
	@Autowired
	EclipseStoreClientConfiguration configuration;
	
	@Test
	void testSaveAndFindSingle()
	{
		final CustomerWithFinal customer = new CustomerWithFinal(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.repository.save(customer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
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
			this.configuration,
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
	
	@Test
	void testSaveAndFindSingleWithFinalId()
	{
		final CustomerWithFinalId originalCustomer =
			new CustomerWithFinalId(TestData.FIRST_NAME, TestData.LAST_NAME);
		Assertions.assertThrows(IdFieldFinalException.class, () -> this.withFinalIdRepository.save(originalCustomer));
	}
}
