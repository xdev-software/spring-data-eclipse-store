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
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.shared.DefaultTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.shared.SharedTestConfiguration;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.Customer;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.CustomerRepository;


@DefaultTestAnnotations
class StressTest
{
	@Autowired
	private CustomerRepository repository;
	
	@Autowired
	private SharedTestConfiguration configuration;
	
	/**
	 * @param customerCount beyond 5_000 takes quite a long time. That's why 5_000 is the biggest number for single
	 *                      saving.
	 */
	@ParameterizedTest
	@ValueSource(ints = {100, 1_000, 5_000})
	void testSaveSingleAndRestoreManyCustomers(final int customerCount)
	{
		IntStream.range(0, customerCount).forEach(
			i -> this.repository.save(new Customer("Test" + i, "Test" + i))
		);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<Customer> customers = TestUtil.iterableToList(this.repository.findAll());
				Assertions.assertEquals(customerCount, customers.size());
			}
		);
	}
	
	@ParameterizedTest
	@ValueSource(ints = {100, 1_000, 5_000, 10_000, 50_000, 100_000})
	void testSaveBulkAndRestoreManyCustomers(final int customerCount)
	{
		this.repository.saveAll(
			IntStream.range(0, customerCount).mapToObj(
				i -> new Customer("Test" + i, "Test" + i)
			).toList()
		);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<Customer> customers = TestUtil.iterableToList(this.repository.findAll());
				Assertions.assertEquals(customerCount, customers.size());
			}
		);
	}
	
	@ParameterizedTest
	@ValueSource(ints = {100, 1_000, 5_000, 10_000, 50_000, 100_000})
	void testSaveBulkAndChangeCustomers(final int customerCount)
	{
		this.repository.saveAll(
			IntStream.range(0, customerCount).mapToObj(
				i -> new Customer("Test" + i, "Test" + i)
			).toList()
		);
		TestUtil.restartDatastore(this.configuration);
		
		final List<Customer> customers = TestUtil.iterableToList(this.repository.findAll());
		customers.forEach(
			customer ->
			{
				customer.setFirstName("Another" + customer.getFirstName());
				customer.setLastName("Another" + customer.getLastName());
			});
		
		this.repository.saveAll(customers);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<Customer> loadedCustomers = TestUtil.iterableToList(this.repository.findAll());
				Assertions.assertEquals(customerCount, loadedCustomers.size());
				loadedCustomers.forEach(
					customer ->
					{
						Assertions.assertTrue(customer.getFirstName().startsWith("AnotherTest"));
						Assertions.assertTrue(customer.getLastName().startsWith("AnotherTest"));
					}
				);
			}
		);
	}
}
