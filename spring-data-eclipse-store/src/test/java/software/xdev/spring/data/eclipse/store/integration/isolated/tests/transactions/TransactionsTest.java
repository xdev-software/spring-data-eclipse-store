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

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.TransactionTemplate;

import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {TransactionsTestConfiguration.class})
class TransactionsTest
{
	private final AccountRepository accountRepository;
	private final CounterRepository counterRepository;
	private Account account1;
	private Account account2;
	private Counter counter1;
	private Counter counter2;
	
	@Autowired
	public TransactionsTest(
		final AccountRepository accountRepository,
		final CounterRepository counterRepository)
	{
		this.accountRepository = accountRepository;
		this.counterRepository = counterRepository;
	}
	
	@BeforeEach
	void initData()
	{
		this.account1 = new Account(1, BigDecimal.TEN);
		this.account2 = new Account(2, BigDecimal.ZERO);
		this.accountRepository.saveAll(List.of(this.account1, this.account2));
		
		this.counter1 = new Counter(1, 10);
		this.counter2 = new Counter(2, 0);
		this.counterRepository.saveAll(List.of(this.counter1, this.counter2));
	}
	
	@Test
	void accountTransactionWorking(@Autowired final PlatformTransactionManager transactionManager)
	{
		new TransactionTemplate(transactionManager).execute(
			status ->
			{
				this.account1.setBalance(this.account1.getBalance().subtract(BigDecimal.ONE));
				this.accountRepository.save(this.account1);
				
				this.account2.setBalance(this.account2.getBalance().add(BigDecimal.ONE));
				this.accountRepository.save(this.account2);
				return null;
			}
		);
		
		Assertions.assertEquals(
			BigDecimal.valueOf(9),
			this.accountRepository.findById(this.account1.getId()).get().getBalance());
		Assertions.assertEquals(
			BigDecimal.ONE,
			this.accountRepository.findById(this.account2.getId()).get().getBalance());
	}
	
	/**
	 * This is actually correct (if you look at Spring Data JPA for comparison). The second change is also persisted
	 * and
	 * not only the first one. This seems counterintuitive, but is just like with the rest of Spring Data
	 * Implementations.
	 */
	@Test
	void accountTransactionChangeAfterSave(@Autowired final PlatformTransactionManager transactionManager)
	{
		new TransactionTemplate(transactionManager).execute(
			status ->
			{
				this.account1.setBalance(this.account1.getBalance().subtract(BigDecimal.ONE));
				this.accountRepository.save(this.account1);
				this.account1.setBalance(this.account1.getBalance().subtract(BigDecimal.ONE));
				return null;
			}
		);
		
		Assertions.assertEquals(
			BigDecimal.valueOf(8),
			this.accountRepository.findById(this.account1.getId()).get().getBalance());
	}
	
	@Test
	void accountAndCounterTransactionSequential(@Autowired final PlatformTransactionManager transactionManager)
	{
		this.accountTransactionWorking(transactionManager);
		
		new TransactionTemplate(transactionManager).execute(
			status ->
			{
				this.counter1.setCount(this.counter1.getCount() - 1);
				this.counterRepository.save(this.counter1);
				
				this.counter2.setCount(this.counter2.getCount() + 1);
				this.counterRepository.save(this.counter2);
				return null;
			}
		);
		
		Assertions.assertEquals(
			9,
			this.counterRepository.findById(this.counter1.getId()).get().getCount());
		Assertions.assertEquals(
			1,
			this.counterRepository.findById(this.counter2.getId()).get().getCount());
	}
	
	/**
	 * Other implementations of Spring Data can do this, but we can't for now.
	 */
	@Test
	void doubleTransaction(@Autowired final PlatformTransactionManager transactionManager)
	{
		new TransactionTemplate(transactionManager).execute(
			status1 ->
			{
				final TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
				Assertions.assertThrows(
					TransactionSystemException.class,
					() -> transactionTemplate.execute(status2 -> null)
				);
				return null;
			}
		);
	}
	
	@Test
	void accountAndCounterTransactionSameTransaction(@Autowired final PlatformTransactionManager transactionManager)
	{
		new TransactionTemplate(transactionManager).execute(
			status ->
			{
				this.account1.setBalance(this.account1.getBalance().subtract(BigDecimal.ONE));
				this.accountRepository.save(this.account1);
				
				this.account2.setBalance(this.account2.getBalance().add(BigDecimal.ONE));
				this.accountRepository.save(this.account2);
				
				this.counter1.setCount(this.counter1.getCount() - 1);
				this.counterRepository.save(this.counter1);
				
				this.counter2.setCount(this.counter2.getCount() + 1);
				this.counterRepository.save(this.counter2);
				
				return null;
			}
		);
		
		Assertions.assertEquals(
			BigDecimal.valueOf(9),
			this.accountRepository.findById(this.account1.getId()).get().getBalance());
		Assertions.assertEquals(
			BigDecimal.ONE,
			this.accountRepository.findById(this.account2.getId()).get().getBalance());
		Assertions.assertEquals(
			9,
			this.counterRepository.findById(this.counter1.getId()).get().getCount());
		Assertions.assertEquals(
			1,
			this.counterRepository.findById(this.counter2.getId()).get().getCount());
	}
	
	@Test
	void accountTransactionUnexpectedError(@Autowired final PlatformTransactionManager transactionManager)
	{
		Assertions.assertThrows(RuntimeException.class, () ->
			new TransactionTemplate(transactionManager).execute(
				status ->
				{
					this.account1.setBalance(this.account1.getBalance().subtract(BigDecimal.ONE));
					this.accountRepository.save(this.account1);
					
					throw new RuntimeException("Unexpected error");
				}
			));
		
		Assertions.assertEquals(
			BigDecimal.TEN,
			this.accountRepository.findById(this.account1.getId()).get().getBalance());
		Assertions.assertEquals(
			BigDecimal.ZERO,
			this.accountRepository.findById(this.account2.getId()).get().getBalance());
	}
	
	/**
	 * This test should demonstrate the behavior of transactions.
	 */
	@Test
	void findStoredEntityWithinTransaction(@Autowired final PlatformTransactionManager transactionManager)
	{
		new TransactionTemplate(transactionManager).execute(
			status ->
			{
				final Account account3 = new Account(3, BigDecimal.valueOf(100.0));
				this.accountRepository.save(account3);
				
				Assertions.assertFalse(this.accountRepository.findById(account3.getId()).isPresent());
				return null;
			}
		);
	}
	
	/**
	 * Opposite test to {@link TransactionsAnnotationTest#accountTransactionUnexpectedErrorAnnotation()}.
	 */
	@Test
	void accountNoTransactionUnexpectedError()
	{
		Assertions.assertThrows(RuntimeException.class, () -> {
			final Account account1 = new Account(1, BigDecimal.TEN);
			final Account account2 = new Account(2, BigDecimal.ZERO);
			this.accountRepository.saveAll(List.of(account1, account2));
			
			throw new RuntimeException("Unexpected error");
		});
		Assertions.assertEquals(4, TestUtil.iterableToList(this.accountRepository.findAll()).size());
	}
}
