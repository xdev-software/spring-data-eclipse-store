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

import static software.xdev.spring.data.eclipse.store.helper.TestUtil.restartDatastore;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.shared.DefaultTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.shared.SharedTestConfiguration;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.id.CustomerWithIdInt;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.id.CustomerWithIdIntRepository;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.id.CustomerWithIdInteger;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.id.CustomerWithIdIntegerNoAutoGenerate;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.id.CustomerWithIdIntegerNoAutoGenerateRepository;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.id.CustomerWithIdIntegerRepository;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.id.CustomerWithIdLong;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.id.CustomerWithIdLongRepository;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.id.CustomerWithIdString;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.id.CustomerWithIdStringRepository;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.id.CustomerWithPurchase;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.id.CustomerWithPurchaseRepository;
import software.xdev.spring.data.eclipse.store.integration.shared.repositories.id.Purchase;
import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;


@SuppressWarnings("OptionalGetWithoutIsPresent")
@DefaultTestAnnotations
class IdTest
{
	@Autowired
	private SharedTestConfiguration configuration;
	
	@Test
	void testCreateSingleWithAutoIdInteger(@Autowired final CustomerWithIdIntegerRepository customerRepository)
	{
		final CustomerWithIdInteger customer1 = new CustomerWithIdInteger(TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<CustomerWithIdInteger> loadedCustomer = customerRepository.findById(0);
				Assertions.assertTrue(loadedCustomer.isPresent());
				Assertions.assertEquals(customer1, loadedCustomer.get());
			}
		);
	}
	
	/**
	 * In other tests {@link EclipseStoreStorage#clearData} is called. Here the datastore is restarted again to ensure
	 * no previous method is called before the test.
	 */
	@Test
	void testSaveSingleWithoutAnyPreviousCall(@Autowired final CustomerWithIdIntegerRepository customerRepository)
	{
		restartDatastore(this.configuration);
		final CustomerWithIdInteger customer1 = new CustomerWithIdInteger(TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<CustomerWithIdInteger> loadedCustomer = customerRepository.findById(0);
				Assertions.assertTrue(loadedCustomer.isPresent());
				Assertions.assertEquals(customer1, loadedCustomer.get());
			}
		);
	}
	
	@Test
	void testCreateSingleWithAutoIdIntegerWorkingCopyIdSet(
		@Autowired final CustomerWithIdIntegerRepository customerRepository)
	{
		final CustomerWithIdInteger customer1 = new CustomerWithIdInteger(TestData.FIRST_NAME, TestData.LAST_NAME);
		Assertions.assertNull(customer1.getId());
		customerRepository.save(customer1);
		Assertions.assertNotNull(customer1.getId());
	}
	
	@Test
	void testCreateMultipleWithAutoIdInteger(@Autowired final CustomerWithIdIntegerRepository customerRepository)
	{
		final CustomerWithIdInteger customer1 = new CustomerWithIdInteger(TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(customer1);
		final CustomerWithIdInteger customer2 =
			new CustomerWithIdInteger(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		customerRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerWithIdInteger> loadedCustomers =
					TestUtil.iterableToList(customerRepository.findAllById(List.of(0, 1)));
				Assertions.assertEquals(2, loadedCustomers.size());
				Assertions.assertNotEquals(loadedCustomers.get(0), loadedCustomers.get(1));
			}
		);
	}
	
	@Test
	void testCreateMultipleWithAutoIdIntegerSingleFinds(
		@Autowired final CustomerWithIdIntegerRepository customerRepository)
	{
		final CustomerWithIdInteger customer1 = new CustomerWithIdInteger(TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(customer1);
		final CustomerWithIdInteger customer2 =
			new CustomerWithIdInteger(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		customerRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<CustomerWithIdInteger> loadedCustomer1 = customerRepository.findById(0);
				Assertions.assertEquals(customer1, loadedCustomer1.get());
				final Optional<CustomerWithIdInteger> loadedCustomer2 = customerRepository.findById(1);
				Assertions.assertEquals(customer2, loadedCustomer2.get());
			}
		);
	}
	
	@Test
	void testCreateSingleWithAutoIdInt(@Autowired final CustomerWithIdIntRepository customerRepository)
	{
		final CustomerWithIdInt customer1 = new CustomerWithIdInt(TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<CustomerWithIdInt> loadedCustomer = customerRepository.findById(0);
				Assertions.assertTrue(loadedCustomer.isPresent());
				Assertions.assertEquals(customer1, loadedCustomer.get());
			}
		);
	}
	
	@Test
	void testCreateSingleWithAutoIdString(@Autowired final CustomerWithIdStringRepository customerRepository)
	{
		final CustomerWithIdString customer1 = new CustomerWithIdString(TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<CustomerWithIdString> loadedCustomer = customerRepository.findById("0");
				Assertions.assertTrue(loadedCustomer.isPresent());
				Assertions.assertEquals(customer1, loadedCustomer.get());
			}
		);
	}
	
	@Test
	void testCreateMultipleWithAutoIdString(@Autowired final CustomerWithIdStringRepository customerRepository)
	{
		final CustomerWithIdString customer1 = new CustomerWithIdString(TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(customer1);
		final CustomerWithIdString customer2 =
			new CustomerWithIdString(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		customerRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerWithIdString> loadedCustomers =
					TestUtil.iterableToList(customerRepository.findAllById(List.of("0", "1")));
				Assertions.assertEquals(2, loadedCustomers.size());
				Assertions.assertNotEquals(loadedCustomers.get(0), loadedCustomers.get(1));
			}
		);
	}
	
	@Test
	void testCreateSingleWithAutoIdLong(@Autowired final CustomerWithIdLongRepository customerRepository)
	{
		final CustomerWithIdLong customer1 = new CustomerWithIdLong(TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<CustomerWithIdLong> loadedCustomer = customerRepository.findById(0L);
				Assertions.assertTrue(loadedCustomer.isPresent());
				Assertions.assertEquals(customer1, loadedCustomer.get());
				Assertions.assertEquals(0L, loadedCustomer.get().getId());
			}
		);
	}
	
	@Test
	void testCreateMultipleWithAutoIdLong(@Autowired final CustomerWithIdLongRepository customerRepository)
	{
		final CustomerWithIdLong customer1 = new CustomerWithIdLong(TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(customer1);
		final CustomerWithIdLong customer2 =
			new CustomerWithIdLong(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		customerRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerWithIdLong> loadedCustomers =
					TestUtil.iterableToList(customerRepository.findAllById(List.of(0L, 1L)));
				Assertions.assertEquals(2, loadedCustomers.size());
				Assertions.assertNotEquals(loadedCustomers.get(0), loadedCustomers.get(1));
				final List<Long> idList = loadedCustomers.stream().map(CustomerWithIdLong::getId).toList();
				Assertions.assertTrue(idList.contains(0L));
				Assertions.assertTrue(idList.contains(1L));
			}
		);
	}
	
	@Test
	void testCreateSingleWithNoAutoIdInteger(
		@Autowired final CustomerWithIdIntegerNoAutoGenerateRepository customerRepository)
	{
		final CustomerWithIdIntegerNoAutoGenerate customer1 =
			new CustomerWithIdIntegerNoAutoGenerate(0, TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<CustomerWithIdIntegerNoAutoGenerate> loadedCustomer = customerRepository.findById(0);
				Assertions.assertTrue(loadedCustomer.isPresent());
				Assertions.assertEquals(customer1, loadedCustomer.get());
			}
		);
	}
	
	@Test
	void testCreateMultipleWithNoAutoIdInteger(
		@Autowired final CustomerWithIdIntegerNoAutoGenerateRepository customerRepository)
	{
		final CustomerWithIdIntegerNoAutoGenerate customer1 =
			new CustomerWithIdIntegerNoAutoGenerate(0, TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(customer1);
		final CustomerWithIdIntegerNoAutoGenerate customer2 =
			new CustomerWithIdIntegerNoAutoGenerate(1, TestData.FIRST_NAME_ALTERNATIVE,
				TestData.LAST_NAME_ALTERNATIVE);
		customerRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerWithIdIntegerNoAutoGenerate> loadedCustomers =
					TestUtil.iterableToList(customerRepository.findAllById(List.of(0, 1)));
				Assertions.assertEquals(2, loadedCustomers.size());
				Assertions.assertNotEquals(loadedCustomers.get(0), loadedCustomers.get(1));
			}
		);
	}
	
	@Test
	void testSaveSingleWithAutoIdInteger(
		@Autowired final CustomerWithIdIntegerRepository customerRepository)
	{
		final CustomerWithIdInteger customer1 =
			new CustomerWithIdInteger();
		customerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerWithIdInteger> loadedCustomer =
					TestUtil.iterableToList(customerRepository.findAll());
				Assertions.assertEquals(1, loadedCustomer.size());
				Assertions.assertEquals(0, loadedCustomer.get(0).getId());
				Assertions.assertEquals(customer1, loadedCustomer.get(0));
			}
		);
	}
	
	@Test
	void testSaveSingleWithNoAutoIdInteger(
		@Autowired final CustomerWithIdIntegerNoAutoGenerateRepository customerRepository)
	{
		final CustomerWithIdIntegerNoAutoGenerate customer1 =
			new CustomerWithIdIntegerNoAutoGenerate();
		customerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerWithIdIntegerNoAutoGenerate> loadedCustomer =
					TestUtil.iterableToList(customerRepository.findAll());
				Assertions.assertEquals(1, loadedCustomer.size());
				Assertions.assertNull(loadedCustomer.get(0).getId());
				Assertions.assertEquals(customer1, loadedCustomer.get(0));
			}
		);
	}
	
	@Test
	void testAutoIdWithSubnodeWithId(
		@Autowired final CustomerWithPurchaseRepository customerRepository)
	{
		final String purchaseName = "bag";
		final CustomerWithPurchase customer1 =
			new CustomerWithPurchase();
		customer1.addPurchase(new Purchase(purchaseName));
		customerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerWithPurchase> loadedCustomer =
					TestUtil.iterableToList(customerRepository.findAll());
				Assertions.assertEquals(1, loadedCustomer.get(0).getPurchases().size());
				Assertions.assertEquals(purchaseName, loadedCustomer.get(0).getPurchases().get(0).getProductName());
				Assertions.assertEquals(0, loadedCustomer.get(0).getPurchases().get(0).getId());
			}
		);
	}
	
	@Test
	void testAutoIdWithTwoSubnodeWithId(
		@Autowired final CustomerWithPurchaseRepository customerRepository)
	{
		final String purchaseName = "bag";
		final CustomerWithPurchase customer1 =
			new CustomerWithPurchase();
		customer1.addPurchase(new Purchase(purchaseName));
		customer1.addPurchase(new Purchase(purchaseName));
		customerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerWithPurchase> loadedCustomer =
					TestUtil.iterableToList(customerRepository.findAll());
				Assertions.assertEquals(2, loadedCustomer.get(0).getPurchases().size());
				Assertions.assertEquals(0, loadedCustomer.get(0).getPurchases().get(0).getId());
				Assertions.assertEquals(1, loadedCustomer.get(0).getPurchases().get(1).getId());
			}
		);
	}
	
	@Test
	void testAutoIdWithTwoSameSubnodesWithSameIdDifferentNod(
		@Autowired final CustomerWithPurchaseRepository customerRepository)
	{
		final Purchase purchase = new Purchase("bag");
		final CustomerWithPurchase customer1 = new CustomerWithPurchase(TestData.FIRST_NAME, TestData.LAST_NAME);
		customer1.addPurchase(purchase);
		customerRepository.save(customer1);
		final CustomerWithPurchase customer2 =
			new CustomerWithPurchase(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		customer2.addPurchase(purchase);
		customerRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerWithPurchase> loadedCustomer =
					TestUtil.iterableToList(customerRepository.findAll());
				
				final CustomerWithPurchase loadedCustomer1 =
					CustomerWithPurchase.getCustomerWithLastName(loadedCustomer, TestData.LAST_NAME);
				Assertions.assertEquals(1, loadedCustomer1.getPurchases().size());
				Assertions.assertEquals(0, loadedCustomer1.getPurchases().get(0).getId());
				
				TestUtil.iterableToList(customerRepository.findAll());
				final CustomerWithPurchase loadedCustomer2 =
					CustomerWithPurchase.getCustomerWithLastName(loadedCustomer, TestData.LAST_NAME_ALTERNATIVE);
				Assertions.assertEquals(1, loadedCustomer2.getPurchases().size());
				Assertions.assertEquals(0, loadedCustomer2.getPurchases().get(0).getId());
			}
		);
	}
	
	@Test
	void testAutoIdWithTwoSameSubnodesWithSameIdSameNode(
		@Autowired final CustomerWithPurchaseRepository customerRepository)
	{
		final Purchase purchase = new Purchase("bag");
		final CustomerWithPurchase customer1 = new CustomerWithPurchase(TestData.FIRST_NAME, TestData.LAST_NAME);
		customer1.addPurchase(purchase);
		customer1.addPurchase(purchase);
		customerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerWithPurchase> loadedCustomer =
					TestUtil.iterableToList(customerRepository.findAll());
				
				Assertions.assertEquals(2, loadedCustomer.get(0).getPurchases().size());
				Assertions.assertEquals(0, loadedCustomer.get(0).getPurchases().get(0).getId());
				Assertions.assertEquals(0, loadedCustomer.get(0).getPurchases().get(1).getId());
			}
		);
	}
}
