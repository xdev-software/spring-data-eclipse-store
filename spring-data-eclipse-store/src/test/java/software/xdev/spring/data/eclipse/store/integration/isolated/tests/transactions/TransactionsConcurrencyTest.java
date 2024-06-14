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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.transactions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {TransactionsTestConfiguration.class})
class TransactionsConcurrencyTest
{
	private final AccountRepository accountRepository;
	
	@Autowired
	public TransactionsConcurrencyTest(final AccountRepository accountRepository)
	{
		this.accountRepository = accountRepository;
	}
	
	@Test
	void testSaveConcurrentlyPreviouslyNonExistingAccounts(
		@Autowired final PlatformTransactionManager transactionManager
	)
		throws InterruptedException
	{
		final List<Account> testAccounts =
			IntStream.range(1, 1000).mapToObj((i) -> new Account(i, BigDecimal.TEN)).toList();
		
		final ExecutorService service = Executors.newFixedThreadPool(10);
		final CountDownLatch latch = new CountDownLatch(testAccounts.size());
		testAccounts.forEach(
			account ->
				service.execute(() ->
					{
						new TransactionTemplate(transactionManager).execute(
							status ->
							{
								account.setBalance(account.getBalance().subtract(BigDecimal.ONE));
								this.accountRepository.save(account);
								return null;
							});
						Assertions.assertEquals(
							BigDecimal.valueOf(9),
							this.accountRepository.findById(account.getId()).get().getBalance());
						latch.countDown();
					}
				)
		);
		
		assertTrue(latch.await(5, TimeUnit.SECONDS));
		
		final List<Account> accounts = TestUtil.iterableToList(this.accountRepository.findAll());
		assertEquals(testAccounts.size(), accounts.size());
		accounts.forEach(account -> Assertions.assertEquals(BigDecimal.valueOf(9), account.getBalance()));
	}
	
	@Test
	void testSaveConcurrentlyPreviouslyExistingAccounts(
		@Autowired final PlatformTransactionManager transactionManager)
		throws InterruptedException
	{
		final List<Account> testAccounts =
			IntStream.range(1, 100).mapToObj((i) -> new Account(i, BigDecimal.TEN)).toList();
		this.accountRepository.saveAll(testAccounts);
		
		final ExecutorService service = Executors.newFixedThreadPool(10);
		final CountDownLatch latch = new CountDownLatch(testAccounts.size());
		testAccounts.forEach(
			account ->
				service.execute(() ->
					{
						Assertions.assertEquals(
							BigDecimal.TEN,
							this.accountRepository.findById(account.getId()).get().getBalance());
						new TransactionTemplate(transactionManager).execute(
							status ->
							{
								account.setBalance(account.getBalance().subtract(BigDecimal.ONE));
								this.accountRepository.save(account);
								return null;
							});
						Assertions.assertEquals(
							BigDecimal.valueOf(9),
							this.accountRepository.findById(account.getId()).get().getBalance());
						latch.countDown();
					}
				)
		);
		
		assertTrue(latch.await(5, TimeUnit.SECONDS));
		
		final List<Account> accounts = TestUtil.iterableToList(this.accountRepository.findAll());
		assertEquals(testAccounts.size(), accounts.size());
		accounts.forEach(account -> Assertions.assertEquals(BigDecimal.valueOf(9), account.getBalance()));
	}
	
	/**
	 * Here it is enough if all the executions are running through. The final balance of the account varies with
	 * different CPUs.
	 */
	@Test
	void testSaveConcurrentlyChangesOnSameAccount(
		@Autowired final PlatformTransactionManager transactionManager)
		throws InterruptedException
	{
		final Account account = new Account(1, BigDecimal.ZERO);
		this.accountRepository.save(account);
		
		final ExecutorService service = Executors.newFixedThreadPool(10);
		final CountDownLatch latch = new CountDownLatch(100);
		IntStream.range(0, 100).forEach(
			i ->
				service.execute(() ->
					{
						new TransactionTemplate(transactionManager).execute(
							status ->
							{
								final Account loadedAccount = this.accountRepository.findById(1).get();
								loadedAccount.setBalance(loadedAccount.getBalance().add(BigDecimal.ONE));
								this.accountRepository.save(loadedAccount);
								return null;
							});
						latch.countDown();
					}
				)
		);
		
		assertTrue(latch.await(5, TimeUnit.SECONDS));
	}
	
	@Test
	void testSaveConcurrentlyChangesOnSameAccountMassRollback(
		@Autowired final PlatformTransactionManager transactionManager)
		throws InterruptedException
	{
		final Account account = new Account(1, BigDecimal.ZERO);
		this.accountRepository.save(account);
		
		final ExecutorService service = Executors.newFixedThreadPool(10);
		final CountDownLatch latch = new CountDownLatch(100);
		IntStream.range(0, 100).forEach(
			i ->
				service.execute(() ->
					{
						Assertions.assertThrows(
							RuntimeException.class, () ->
								new TransactionTemplate(transactionManager).execute(
									status ->
									{
										final Account loadedAccount = this.accountRepository.findById(1).get();
										loadedAccount.setBalance(loadedAccount.getBalance().add(BigDecimal.ONE));
										this.accountRepository.save(loadedAccount);
										throw new RuntimeException("Random exception");
									})
						);
						latch.countDown();
					}
				)
		);
		
		assertTrue(latch.await(5, TimeUnit.SECONDS));
		Assertions.assertEquals(BigDecimal.ZERO, this.accountRepository.findById(1).get().getBalance());
	}
}
