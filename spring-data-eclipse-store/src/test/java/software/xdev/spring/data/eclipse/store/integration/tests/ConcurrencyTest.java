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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static software.xdev.spring.data.eclipse.store.helper.TestUtil.restartDatastore;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.DefaultTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.repositories.Customer;
import software.xdev.spring.data.eclipse.store.integration.repositories.CustomerRepository;
import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;


@SuppressWarnings("OptionalGetWithoutIsPresent")
@DefaultTestAnnotations
class ConcurrencyTest
{
	public static final String CUSTOMER_NO = "Customer No.";
	@Autowired
	private CustomerRepository repository;
	
	@Autowired
	private EclipseStoreClientConfiguration configuration;
	
	private final List<Customer> testCustomers =
		IntStream.range(1, 100).mapToObj((i) -> new Customer(CUSTOMER_NO + i, "")).toList();
	
	@Test
	void testSaveConcurrently() throws InterruptedException
	{
		final ExecutorService service = Executors.newFixedThreadPool(10);
		final CountDownLatch latch = new CountDownLatch(this.testCustomers.size());
		this.testCustomers.forEach(
			customer ->
				service.execute(() ->
					{
						this.repository.save(customer);
						latch.countDown();
					}
				)
		);
		
		assertTrue(latch.await(5, TimeUnit.SECONDS));
		
		final List<Customer> customers = TestUtil.iterableToList(this.repository.findAll());
		assertEquals(this.testCustomers.size(), customers.size());
		
		restartDatastore(this.configuration);
		final List<Customer> customers2 = TestUtil.iterableToList(this.repository.findAll());
		assertEquals(this.testCustomers.size(), customers2.size());
	}
	
	@Test
	void testDeleteConcurrently() throws InterruptedException
	{
		this.repository.saveAll(this.testCustomers);
		final ExecutorService service = Executors.newFixedThreadPool(10);
		final CountDownLatch latch = new CountDownLatch(this.testCustomers.size());
		this.testCustomers.forEach(
			customer ->
				service.execute(() ->
					{
						this.repository.delete(customer);
						latch.countDown();
					}
				)
		);
		
		assertTrue(latch.await(5, TimeUnit.SECONDS));
		
		final List<Customer> customers2 = TestUtil.iterableToList(this.repository.findAll());
		assertTrue(customers2.isEmpty());
		
		restartDatastore(this.configuration);
		final List<Customer> customers3 = TestUtil.iterableToList(this.repository.findAll());
		assertTrue(customers3.isEmpty());
	}
	
	/**
	 * Concurrent changes may result in unclear results, but this test ensures that there are no errors while doing
	 * that.
	 */
	@Test
	void testChangeConcurrently() throws InterruptedException
	{
		this.repository.saveAll(this.testCustomers);
		final ExecutorService service = Executors.newFixedThreadPool(10);
		final CountDownLatch latch = new CountDownLatch(this.testCustomers.size());
		this.testCustomers.forEach(
			customer ->
				service.execute(() ->
					{
						final Customer existingCustomer = this.repository.findByFirstName(CUSTOMER_NO + 1).get();
						existingCustomer.setLastName("something");
						this.repository.save(existingCustomer);
						latch.countDown();
					}
				)
		);
		
		assertTrue(latch.await(5, TimeUnit.SECONDS));
		
		final Customer customer = this.repository.findByFirstName(CUSTOMER_NO + 1).get();
		assertTrue(Strings.isNotEmpty(customer.getFirstName()));
	}
	
	/**
	 * Here should be tested if different customer can simultaneously be read.
	 */
	@Test
	void testReadConcurrentlyDifferentCustomers() throws InterruptedException
	{
		this.repository.saveAll(this.testCustomers);
		
		final ExecutorService service = Executors.newFixedThreadPool(10);
		final CountDownLatch latch = new CountDownLatch(this.testCustomers.size());
		this.testCustomers.forEach(
			customer ->
				service.execute(() ->
					{
						final Optional<Customer> existingCustomer =
							this.repository.findByFirstName(customer.getFirstName());
						assertTrue(existingCustomer.isPresent());
						latch.countDown();
					}
				)
		);
		assertTrue(latch.await(5, TimeUnit.SECONDS));
	}
	
	/**
	 * Here should be tested if the same customer can simultaneously be read.
	 */
	@Test
	void testReadConcurrentlySameCustomer() throws InterruptedException
	{
		this.repository.saveAll(this.testCustomers);
		final ExecutorService service = Executors.newFixedThreadPool(10);
		final CountDownLatch latch = new CountDownLatch(this.testCustomers.size());
		this.testCustomers.forEach(
			customer ->
				service.execute(() ->
					{
						final Optional<Customer> existingCustomer =
							this.repository.findByFirstName(CUSTOMER_NO + 1);
						assertTrue(existingCustomer.isPresent());
						latch.countDown();
					}
				)
		);
		assertTrue(latch.await(5, TimeUnit.SECONDS));
	}
	
	/**
	 * Here should be tested if the same customer can simultaneously be read.
	 */
	@Test
	void testReadConcurrentlyAllCustomers() throws InterruptedException
	{
		this.repository.saveAll(this.testCustomers);
		
		final ExecutorService service = Executors.newFixedThreadPool(10);
		final CountDownLatch latch = new CountDownLatch(this.testCustomers.size());
		this.testCustomers.forEach(
			customer ->
				service.execute(() ->
					{
						final List<Customer> existingCustomers =
							TestUtil.iterableToList(this.repository.findAll());
						assertEquals(this.testCustomers.size(), existingCustomers.size());
						latch.countDown();
					}
				)
		);
		
		assertTrue(latch.await(5, TimeUnit.SECONDS));
	}
}
