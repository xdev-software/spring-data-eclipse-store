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
	// single TransactionTemplate shared amongst all methods in this instance
	private final TransactionTemplate transactionTemplate;
	
	@Autowired
	public TransactionsTest(
		final TransactionsTestConfiguration configuration,
		final PlatformTransactionManager transactionManager)
	{
		this.configuration = configuration;
		this.transactionTemplate = new TransactionTemplate(transactionManager);
	}
	
	@Test
	void accountTransaction_Working(final AccountRepository repository)
	{
		final Account account1 = new Account(BigDecimal.TEN);
		final Account account2 = new Account(BigDecimal.ZERO);
		repository.saveAll(List.of(account1, account2));
		
		this.transactionTemplate.execute(
			status ->
			{
				account1.setBalance(account1.getBalance().subtract(BigDecimal.ONE));
				repository.save(account1);
				
				account2.setBalance(account2.getBalance().subtract(BigDecimal.ONE));
				repository.save(account2);
				return null;
			}
		);
		
		Assertions.assertEquals(BigDecimal.valueOf(9.0), repository.findById(account1.getId()).get().getBalance());
		Assertions.assertEquals(BigDecimal.ONE, repository.findById(account2.getId()).get().getBalance());
	}
	
	@Test
	void accountTransaction_UnexpectedError(final AccountRepository repository)
	{
		final Account account1 = new Account(BigDecimal.TEN);
		final Account account2 = new Account(BigDecimal.ZERO);
		repository.saveAll(List.of(account1, account2));
		
		this.transactionTemplate.execute(
			status ->
			{
				account1.setBalance(account1.getBalance().subtract(BigDecimal.ONE));
				repository.save(account1);
				
				throw new RuntimeException("Unexpected error");
			}
		);
		
		Assertions.assertEquals(BigDecimal.TEN, repository.findById(account1.getId()).get().getBalance());
		Assertions.assertEquals(BigDecimal.ZERO, repository.findById(account2.getId()).get().getBalance());
	}
}
