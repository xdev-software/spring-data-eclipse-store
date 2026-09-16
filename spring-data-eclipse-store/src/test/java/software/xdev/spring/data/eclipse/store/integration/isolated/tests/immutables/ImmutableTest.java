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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.immutables;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {ImmutableTestConfiguration.class})
class ImmutableTest
{
	@Autowired
	ImmutableTestConfiguration configuration;
	
	@Test
	void testSaveAndFindSingle(@Autowired final CustomerWithFinalRepository repository)
	{
		final CustomerWithFinal customer = new CustomerWithFinal(TestData.FIRST_NAME, TestData.LAST_NAME);
		repository.save(customer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerWithFinal> customers = TestUtil.iterableToList(repository.findAll());
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(TestData.FIRST_NAME, customers.get(0).getFirstName());
			}
		);
	}
	
	@Test
	void testSaveAndFindSingleWithChild(@Autowired final CustomerWithFinalChildRepository withChildRepository)
	{
		final CustomerWithFinalChild originalCustomer =
			new CustomerWithFinalChild(TestData.FIRST_NAME, TestData.LAST_NAME);
		withChildRepository.save(originalCustomer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerWithFinalChild> customers =
					TestUtil.iterableToList(withChildRepository.findAll());
				Assertions.assertEquals(1, customers.size());
				Assertions.assertEquals(TestData.FIRST_NAME, customers.get(0).getFirstName());
				Assertions.assertEquals(
					originalCustomer.getChild().getFirstName(),
					customers.get(0).getChild().getFirstName());
			}
		);
	}
}
