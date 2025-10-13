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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.id;

import static software.xdev.spring.data.eclipse.store.helper.TestUtil.restartDatastore;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.model.CustomerWithIdInt;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.model.CustomerWithIdIntRepository;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.model.CustomerWithIdInteger;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.model.CustomerWithIdIntegerNoAutoGenerate;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.model.CustomerWithIdIntegerNoAutoGenerateRepository;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.model.CustomerWithIdIntegerRepository;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.model.CustomerWithIdLong;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.model.CustomerWithIdLongRepository;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.model.CustomerWithIdString;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.model.CustomerWithIdStringNoAutoGenerate;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.model.CustomerWithIdStringNoAutoGenerateRepository;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.model.CustomerWithIdStringRepository;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.model.CustomerWithIdUuid;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.model.CustomerWithIdUuidRepository;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.model.CustomerWithPurchase;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.model.CustomerWithPurchaseRepository;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.model.Purchase;
import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;


@SuppressWarnings("OptionalGetWithoutIsPresent")
@IsolatedTestAnnotations
@ContextConfiguration(classes = {IdTestConfiguration.class})
class IdTest
{
	private final IdTestConfiguration configuration;
	
	@Autowired
	public IdTest(final IdTestConfiguration configuration)
	{
		this.configuration = configuration;
	}
	
	@Test
	void createSingleWithAutoIdInteger(@Autowired final CustomerWithIdIntegerRepository customerRepository)
	{
		final CustomerWithIdInteger customer1 = new CustomerWithIdInteger(TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<CustomerWithIdInteger> loadedCustomer = customerRepository.findById(1);
				Assertions.assertTrue(loadedCustomer.isPresent());
				Assertions.assertEquals(customer1, loadedCustomer.get());
			}
		);
	}
	
	@Test
	void saveBulkWithAutoIdInteger(@Autowired final CustomerWithIdIntegerRepository customerRepository)
	{
		final CustomerWithIdInteger customer1 = new CustomerWithIdInteger(TestData.FIRST_NAME, TestData.LAST_NAME);
		final CustomerWithIdInteger customer2 = new CustomerWithIdInteger(TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.saveAll(List.of(customer1, customer2));
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				Assertions.assertEquals(2, customerRepository.count());
				
				final Optional<CustomerWithIdInteger> loadedCustomer1 = customerRepository.findById(1);
				Assertions.assertTrue(loadedCustomer1.isPresent());
				Assertions.assertEquals(customer1, loadedCustomer1.get());
				
				final Optional<CustomerWithIdInteger> loadedCustomer2 = customerRepository.findById(2);
				Assertions.assertTrue(loadedCustomer2.isPresent());
				Assertions.assertEquals(customer2, loadedCustomer2.get());
			}
		);
	}
	
	@Test
	void saveBulkWithAutoIdInt(@Autowired final CustomerWithIdIntRepository customerRepository)
	{
		final CustomerWithIdInt customer1 = new CustomerWithIdInt(TestData.FIRST_NAME, TestData.LAST_NAME);
		final CustomerWithIdInt customer2 = new CustomerWithIdInt(TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.saveAll(List.of(customer1, customer2));
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				Assertions.assertEquals(2, customerRepository.count());
				customerRepository.findAll();
				
				final Optional<CustomerWithIdInt> loadedCustomer1 = customerRepository.findById(1);
				Assertions.assertTrue(loadedCustomer1.isPresent());
				Assertions.assertEquals(customer1, loadedCustomer1.get());
				
				final Optional<CustomerWithIdInt> loadedCustomer2 = customerRepository.findById(2);
				Assertions.assertTrue(loadedCustomer2.isPresent());
				Assertions.assertEquals(customer2, loadedCustomer2.get());
			}
		);
	}
	
	@Test
	void saveBulkWithAutoIdIntAndHardcodedId(@Autowired final CustomerWithIdIntRepository customerRepository)
	{
		final CustomerWithIdInt customer1 = new CustomerWithIdInt(1, TestData.FIRST_NAME, TestData.LAST_NAME);
		final CustomerWithIdInt customer2 = new CustomerWithIdInt(1, TestData.FIRST_NAME, TestData.LAST_NAME);
		Assertions.assertThrows(
			IllegalArgumentException.class,
			() -> customerRepository.saveAll(List.of(customer1, customer2))
		);
	}
	
	@Test
	void saveBulkWithNoAutoIdIntSameId(
		@Autowired final CustomerWithIdIntegerNoAutoGenerateRepository customerRepository)
	{
		final CustomerWithIdIntegerNoAutoGenerate customer1 =
			new CustomerWithIdIntegerNoAutoGenerate(1, TestData.FIRST_NAME, TestData.LAST_NAME);
		final CustomerWithIdIntegerNoAutoGenerate customer2 =
			new CustomerWithIdIntegerNoAutoGenerate(1, TestData.FIRST_NAME, TestData.LAST_NAME);
		Assertions.assertThrows(
			IllegalArgumentException.class,
			() -> customerRepository.saveAll(List.of(customer1, customer2))
		);
	}
	
	@Test
	void saveBulkWithNoAutoIdStringSameId(
		@Autowired final CustomerWithIdStringNoAutoGenerateRepository customerRepository)
	{
		final CustomerWithIdStringNoAutoGenerate customer1 =
			new CustomerWithIdStringNoAutoGenerate("1", TestData.FIRST_NAME, TestData.LAST_NAME);
		final CustomerWithIdStringNoAutoGenerate customer2 =
			new CustomerWithIdStringNoAutoGenerate("1", TestData.FIRST_NAME, TestData.LAST_NAME);
		Assertions.assertThrows(
			IllegalArgumentException.class,
			() -> customerRepository.saveAll(List.of(customer1, customer2))
		);
	}
	
	@Test
	void saveBulkWithNoAutoIdStringDifferentId(
		@Autowired final CustomerWithIdStringNoAutoGenerateRepository customerRepository)
	{
		final CustomerWithIdStringNoAutoGenerate customer1 =
			new CustomerWithIdStringNoAutoGenerate("1", TestData.FIRST_NAME, TestData.LAST_NAME);
		final CustomerWithIdStringNoAutoGenerate customer2 =
			new CustomerWithIdStringNoAutoGenerate("2", TestData.FIRST_NAME, TestData.LAST_NAME);
		Assertions.assertDoesNotThrow(() -> customerRepository.saveAll(List.of(customer1, customer2)));
	}
	
	/**
	 * In other tests {@link EclipseStoreStorage#clearData} is called. Here the datastore is restarted again to ensure
	 * no previous method is called before the test.
	 */
	@Test
	void saveSingleWithoutAnyPreviousCall(@Autowired final CustomerWithIdIntegerRepository customerRepository)
	{
		restartDatastore(this.configuration);
		final CustomerWithIdInteger customer1 = new CustomerWithIdInteger(TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<CustomerWithIdInteger> loadedCustomer = customerRepository.findById(1);
				Assertions.assertTrue(loadedCustomer.isPresent());
				Assertions.assertEquals(customer1, loadedCustomer.get());
			}
		);
	}
	
	@Test
	void createSingleWithAutoIdIntegerWorkingCopyIdSet(
		@Autowired final CustomerWithIdIntegerRepository customerRepository
	)
	{
		final CustomerWithIdInteger customer1 = new CustomerWithIdInteger(TestData.FIRST_NAME, TestData.LAST_NAME);
		Assertions.assertNull(customer1.getId());
		customerRepository.save(customer1);
		Assertions.assertNotNull(customer1.getId());
	}
	
	@Test
	void createMultipleWithAutoIdInteger(@Autowired final CustomerWithIdIntegerRepository customerRepository)
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
					TestUtil.iterableToList(customerRepository.findAllById(List.of(1, 2)));
				Assertions.assertEquals(2, loadedCustomers.size());
				Assertions.assertNotEquals(loadedCustomers.get(0), loadedCustomers.get(1));
			}
		);
	}
	
	@Test
	void createMultipleWithAutoIdIntegerSingleFinds(
		@Autowired final CustomerWithIdIntegerRepository customerRepository
	)
	{
		final CustomerWithIdInteger customer1 = new CustomerWithIdInteger(TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(customer1);
		final CustomerWithIdInteger customer2 =
			new CustomerWithIdInteger(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		customerRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<CustomerWithIdInteger> loadedCustomer1 = customerRepository.findById(1);
				Assertions.assertEquals(customer1, loadedCustomer1.get());
				final Optional<CustomerWithIdInteger> loadedCustomer2 = customerRepository.findById(2);
				Assertions.assertEquals(customer2, loadedCustomer2.get());
			}
		);
	}
	
	@Test
	void createMultipleWithAutoIdUuidSingleFinds(
		@Autowired final CustomerWithIdUuidRepository customerRepository
	)
	{
		final CustomerWithIdUuid customer1 = new CustomerWithIdUuid(TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(customer1);
		final CustomerWithIdUuid customer2 =
			new CustomerWithIdUuid(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		customerRepository.save(customer2);
		
		final UUID generatedId1 = customerRepository.findAll().get(0).getId();
		final UUID generatedId2 = customerRepository.findAll().get(1).getId();
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<CustomerWithIdUuid> loadedCustomer1 = customerRepository.findById(generatedId1);
				final Optional<CustomerWithIdUuid> loadedCustomer2 = customerRepository.findById(generatedId2);
				Assertions.assertNotEquals(loadedCustomer2, loadedCustomer1);
			}
		);
	}
	
	@Test
	void createSingleWithAutoIdInt(@Autowired final CustomerWithIdIntRepository customerRepository)
	{
		final CustomerWithIdInt customer1 = new CustomerWithIdInt(TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<CustomerWithIdInt> loadedCustomer = customerRepository.findById(1);
				Assertions.assertTrue(loadedCustomer.isPresent());
				Assertions.assertEquals(customer1, loadedCustomer.get());
			}
		);
	}
	
	@Test
	void createSingleWithAutoIdString(@Autowired final CustomerWithIdStringRepository customerRepository)
	{
		final CustomerWithIdString customer1 = new CustomerWithIdString(TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<CustomerWithIdString> loadedCustomer = customerRepository.findById("1");
				Assertions.assertTrue(loadedCustomer.isPresent());
				Assertions.assertEquals(customer1, loadedCustomer.get());
			}
		);
	}
	
	@Test
	void createSingleWithAutoIdUuid(@Autowired final CustomerWithIdUuidRepository customerRepository)
	{
		final CustomerWithIdUuid customer1 = new CustomerWithIdUuid(TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(customer1);
		
		final UUID generatedId = customerRepository.findAll().get(0).getId();
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<CustomerWithIdUuid> loadedCustomer = customerRepository.findById(generatedId);
				Assertions.assertTrue(loadedCustomer.isPresent());
				Assertions.assertEquals(customer1, loadedCustomer.get());
			}
		);
	}
	
	@Test
	void saveAfterRestartSingleWithAutoIdString(@Autowired final CustomerWithIdStringRepository customerRepository)
	{
		final CustomerWithIdString customer1 = new CustomerWithIdString(TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(customer1);
		
		TestUtil.restartDatastore(this.configuration);
		
		customerRepository.deleteAll();
		final CustomerWithIdString customer2 =
			new CustomerWithIdString(TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE);
		customerRepository.save(customer2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<CustomerWithIdString> loadedCustomer = customerRepository.findById("2");
				Assertions.assertTrue(loadedCustomer.isPresent());
				Assertions.assertEquals(customer2, loadedCustomer.get());
			}
		);
	}
	
	@Test
	void createMultipleWithAutoIdString(@Autowired final CustomerWithIdStringRepository customerRepository)
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
					TestUtil.iterableToList(customerRepository.findAllById(List.of("1", "2")));
				Assertions.assertEquals(2, loadedCustomers.size());
				Assertions.assertNotEquals(loadedCustomers.get(0), loadedCustomers.get(1));
			}
		);
	}
	
	@Test
	void createSingleWithAutoIdLong(@Autowired final CustomerWithIdLongRepository customerRepository)
	{
		final CustomerWithIdLong customer1 = new CustomerWithIdLong(TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final Optional<CustomerWithIdLong> loadedCustomer = customerRepository.findById(1L);
				Assertions.assertTrue(loadedCustomer.isPresent());
				Assertions.assertEquals(customer1, loadedCustomer.get());
				Assertions.assertEquals(1L, loadedCustomer.get().getId());
			}
		);
	}
	
	@Test
	void createMultipleWithAutoIdLong(@Autowired final CustomerWithIdLongRepository customerRepository)
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
					TestUtil.iterableToList(customerRepository.findAllById(List.of(1L, 2L)));
				Assertions.assertEquals(2, loadedCustomers.size());
				Assertions.assertNotEquals(loadedCustomers.get(0), loadedCustomers.get(1));
				final List<Long> idList = loadedCustomers.stream().map(CustomerWithIdLong::getId).toList();
				Assertions.assertTrue(idList.contains(1L));
				Assertions.assertTrue(idList.contains(2L));
			}
		);
	}
	
	@Test
	void createSingleWithNoAutoIdInteger(
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
	void createMultipleWithNoAutoIdInteger(
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
	void saveSingleWithAutoIdInteger(
		@Autowired final CustomerWithIdIntegerRepository customerRepository
	)
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
				Assertions.assertEquals(1, loadedCustomer.get(0).getId());
				Assertions.assertEquals(customer1, loadedCustomer.get(0));
			}
		);
	}
	
	@Test
	void saveSingleWithNoAutoIdInteger(
		@Autowired final CustomerWithIdIntegerNoAutoGenerateRepository customerRepository)
	{
		final CustomerWithIdIntegerNoAutoGenerate customer1 =
			new CustomerWithIdIntegerNoAutoGenerate();
		Assertions.assertThrows(
			IllegalArgumentException.class,
			() -> customerRepository.save(customer1)
		);
	}
	
	@Test
	void autoIdWithSubnodeWithId(
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
				Assertions.assertEquals(1, loadedCustomer.get(0).getPurchases().get(0).getId());
			}
		);
	}
	
	@Test
	void autoIdWithTwoSubnodeWithId(
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
				Assertions.assertEquals(1, loadedCustomer.get(0).getPurchases().get(0).getId());
				Assertions.assertEquals(2, loadedCustomer.get(0).getPurchases().get(1).getId());
			}
		);
	}
	
	@Test
	void autoIdWithTwoSameSubnodesWithSameIdDifferentNod(
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
				Assertions.assertEquals(1, loadedCustomer1.getPurchases().get(0).getId());
				
				TestUtil.iterableToList(customerRepository.findAll());
				final CustomerWithPurchase loadedCustomer2 =
					CustomerWithPurchase.getCustomerWithLastName(loadedCustomer, TestData.LAST_NAME_ALTERNATIVE);
				Assertions.assertEquals(1, loadedCustomer2.getPurchases().size());
				Assertions.assertEquals(1, loadedCustomer2.getPurchases().get(0).getId());
			}
		);
	}
	
	@Test
	void autoIdWithTwoSameSubnodesWithSameIdSameNode(
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
				Assertions.assertEquals(1, loadedCustomer.get(0).getPurchases().get(0).getId());
				Assertions.assertEquals(1, loadedCustomer.get(0).getPurchases().get(1).getId());
			}
		);
	}
	
	@Test
	void replaceWithId(@Autowired final CustomerWithIdIntegerNoAutoGenerateRepository customerRepository)
	{
		final CustomerWithIdIntegerNoAutoGenerate existingCustomer =
			new CustomerWithIdIntegerNoAutoGenerate(1, TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(existingCustomer);
		
		final CustomerWithIdIntegerNoAutoGenerate newCustomer =
			new CustomerWithIdIntegerNoAutoGenerate(1, TestData.FIRST_NAME_ALTERNATIVE,
				TestData.LAST_NAME_ALTERNATIVE);
		customerRepository.save(newCustomer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerWithIdIntegerNoAutoGenerate> loadedCustomer =
					TestUtil.iterableToList(customerRepository.findAll());
				
				Assertions.assertEquals(1, loadedCustomer.size());
				Assertions.assertEquals(TestData.FIRST_NAME_ALTERNATIVE, loadedCustomer.get(0).getFirstName());
				Assertions.assertEquals(TestData.LAST_NAME_ALTERNATIVE, loadedCustomer.get(0).getLastName());
			}
		);
	}
	
	@Test
	void replaceWithIdAfterRestart(@Autowired final CustomerWithIdIntegerNoAutoGenerateRepository customerRepository)
	{
		final CustomerWithIdIntegerNoAutoGenerate existingCustomer =
			new CustomerWithIdIntegerNoAutoGenerate(1, TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(existingCustomer);
		
		restartDatastore(this.configuration);
		
		final CustomerWithIdIntegerNoAutoGenerate newCustomer =
			new CustomerWithIdIntegerNoAutoGenerate(1, TestData.FIRST_NAME_ALTERNATIVE,
				TestData.LAST_NAME_ALTERNATIVE);
		customerRepository.save(newCustomer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerWithIdIntegerNoAutoGenerate> loadedCustomer =
					TestUtil.iterableToList(customerRepository.findAll());
				
				Assertions.assertEquals(1, loadedCustomer.size());
				Assertions.assertEquals(TestData.FIRST_NAME_ALTERNATIVE, loadedCustomer.get(0).getFirstName());
				Assertions.assertEquals(TestData.LAST_NAME_ALTERNATIVE, loadedCustomer.get(0).getLastName());
			}
		);
	}
	
	@Test
	void replaceWithAutoId(@Autowired final CustomerWithIdIntegerRepository customerRepository)
	{
		final CustomerWithIdInteger existingCustomer =
			new CustomerWithIdInteger(TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(existingCustomer);
		final Integer existingId = customerRepository.findAll().iterator().next().getId();
		
		final CustomerWithIdInteger newCustomer = new CustomerWithIdInteger(
			existingId,
			TestData.FIRST_NAME_ALTERNATIVE,
			TestData.LAST_NAME_ALTERNATIVE);
		customerRepository.save(newCustomer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerWithIdInteger> loadedCustomer =
					TestUtil.iterableToList(customerRepository.findAll());
				
				Assertions.assertEquals(1, loadedCustomer.size());
				Assertions.assertEquals(TestData.FIRST_NAME_ALTERNATIVE, loadedCustomer.get(0).getFirstName());
				Assertions.assertEquals(TestData.LAST_NAME_ALTERNATIVE, loadedCustomer.get(0).getLastName());
			}
		);
	}
	
	@Test
	void replaceWithIdSaveAll(@Autowired final CustomerWithIdIntegerNoAutoGenerateRepository customerRepository)
	{
		final CustomerWithIdIntegerNoAutoGenerate existingCustomer =
			new CustomerWithIdIntegerNoAutoGenerate(1, TestData.FIRST_NAME, TestData.LAST_NAME);
		final CustomerWithIdIntegerNoAutoGenerate newCustomer =
			new CustomerWithIdIntegerNoAutoGenerate(1, TestData.FIRST_NAME_ALTERNATIVE,
				TestData.LAST_NAME_ALTERNATIVE);
		
		Assertions.assertThrows(
			IllegalArgumentException.class,
			() -> customerRepository.saveAll(List.of(existingCustomer, newCustomer))
		);
	}
	
	@Test
	void addTwoWithId(@Autowired final CustomerWithIdIntegerNoAutoGenerateRepository customerRepository)
	{
		final CustomerWithIdIntegerNoAutoGenerate existingCustomer =
			new CustomerWithIdIntegerNoAutoGenerate(1, TestData.FIRST_NAME, TestData.LAST_NAME);
		customerRepository.save(existingCustomer);
		
		final CustomerWithIdIntegerNoAutoGenerate newCustomer =
			new CustomerWithIdIntegerNoAutoGenerate(2, TestData.FIRST_NAME_ALTERNATIVE,
				TestData.LAST_NAME_ALTERNATIVE);
		customerRepository.save(newCustomer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerWithIdIntegerNoAutoGenerate> loadedCustomer =
					TestUtil.iterableToList(customerRepository.findAll());
				
				Assertions.assertEquals(2, loadedCustomer.size());
			}
		);
	}
	
	@Test
	void idsInMultipleTransactions(
		@Autowired final CustomerWithIdIntegerNoAutoGenerateRepository customerRepository,
		@Autowired final PlatformTransactionManager transactionManager
	)
	{
		new TransactionTemplate(transactionManager).execute(
			status ->
			{
				final CustomerWithIdIntegerNoAutoGenerate existingCustomer =
					new CustomerWithIdIntegerNoAutoGenerate(1, TestData.FIRST_NAME, TestData.LAST_NAME);
				customerRepository.save(existingCustomer);
				return null;
			});
		
		new TransactionTemplate(transactionManager).execute(
			status ->
			{
				final CustomerWithIdIntegerNoAutoGenerate newCustomer =
					new CustomerWithIdIntegerNoAutoGenerate(1, TestData.FIRST_NAME_ALTERNATIVE,
						TestData.LAST_NAME_ALTERNATIVE);
				customerRepository.save(newCustomer);
				return null;
			});
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerWithIdIntegerNoAutoGenerate> loadedCustomer =
					TestUtil.iterableToList(customerRepository.findAll());
				
				Assertions.assertEquals(1, loadedCustomer.size());
				Assertions.assertEquals(TestData.FIRST_NAME_ALTERNATIVE, loadedCustomer.get(0).getFirstName());
				Assertions.assertEquals(TestData.LAST_NAME_ALTERNATIVE, loadedCustomer.get(0).getLastName());
			}
		);
	}
	
	@Test
	void idsInSingleTransactions(
		@Autowired final CustomerWithIdIntegerNoAutoGenerateRepository customerRepository,
		@Autowired final PlatformTransactionManager transactionManager
	)
	{
		new TransactionTemplate(transactionManager).execute(
			status ->
			{
				final CustomerWithIdIntegerNoAutoGenerate existingCustomer =
					new CustomerWithIdIntegerNoAutoGenerate(1, TestData.FIRST_NAME, TestData.LAST_NAME);
				customerRepository.save(existingCustomer);
				
				final CustomerWithIdIntegerNoAutoGenerate newCustomer =
					new CustomerWithIdIntegerNoAutoGenerate(1, TestData.FIRST_NAME_ALTERNATIVE,
						TestData.LAST_NAME_ALTERNATIVE);
				customerRepository.save(newCustomer);
				return null;
			});
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<CustomerWithIdIntegerNoAutoGenerate> loadedCustomer =
					TestUtil.iterableToList(customerRepository.findAll());
				
				Assertions.assertEquals(1, loadedCustomer.size());
				Assertions.assertEquals(TestData.FIRST_NAME_ALTERNATIVE, loadedCustomer.get(0).getFirstName());
				Assertions.assertEquals(TestData.LAST_NAME_ALTERNATIVE, loadedCustomer.get(0).getLastName());
			}
		);
	}
}
