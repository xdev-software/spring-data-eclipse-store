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
import org.springframework.transaction.support.TransactionTemplate;

import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {TransactionsTestConfiguration.class})
class TransactionsTest
{
	private final TransactionsTestConfiguration configuration;
	private final AccountRepository repository;
	private Account account1;
	private Account account2;
	
	@Autowired
	public TransactionsTest(final TransactionsTestConfiguration configuration, final AccountRepository repository)
	{
		this.configuration = configuration;
		this.repository = repository;
	}
	
	@BeforeEach
	void initData()
	{
		this.account1 = new Account(1, BigDecimal.TEN);
		this.account2 = new Account(2, BigDecimal.ZERO);
		this.repository.saveAll(List.of(this.account1, this.account2));
	}
	
	@Test
	void accountTransaction_Working(@Autowired final PlatformTransactionManager transactionManager)
	{
		new TransactionTemplate(transactionManager).execute(
			status ->
			{
				this.account1.setBalance(this.account1.getBalance().subtract(BigDecimal.ONE));
				this.repository.save(this.account1);
				
				this.account2.setBalance(this.account2.getBalance().add(BigDecimal.ONE));
				this.repository.save(this.account2);
				return null;
			}
		);
		
		Assertions.assertEquals(
			BigDecimal.valueOf(9),
			this.repository.findById(this.account1.getId()).get().getBalance());
		Assertions.assertEquals(BigDecimal.ONE, this.repository.findById(this.account2.getId()).get().getBalance());
	}
	
	@Test
	void accountTransaction_UnexpectedError(@Autowired final PlatformTransactionManager transactionManager)
	{
		Assertions.assertThrows(RuntimeException.class, () ->
			new TransactionTemplate(transactionManager).execute(
				status ->
				{
					this.account1.setBalance(this.account1.getBalance().subtract(BigDecimal.ONE));
					this.repository.save(this.account1);
					
					throw new RuntimeException("Unexpected error");
				}
			));
		
		Assertions.assertEquals(BigDecimal.TEN, this.repository.findById(this.account1.getId()).get().getBalance());
		Assertions.assertEquals(BigDecimal.ZERO, this.repository.findById(this.account2.getId()).get().getBalance());
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
				this.repository.save(account3);
				
				Assertions.assertFalse(this.repository.findById(account3.getId()).isPresent());
				return null;
			}
		);
	}
}
