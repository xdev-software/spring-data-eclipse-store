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
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {TransactionsTestConfiguration.class})
class TransactionsConcurrencyTest
{
	private static Stream<Arguments> generateData()
	{
		return Stream.of(
			new SingleTestDataset<>(
				i -> new AccountNoVersion(i, BigDecimal.TEN),
				context -> context.getBean(AccountNoVersionRepository.class),
				true
			).toArguments(),
			new SingleTestDataset<>(
				i -> new AccountNoVersion(i, BigDecimal.TEN),
				context -> context.getBean(AccountNoVersionRepository.class),
				false
			).toArguments(),
			new SingleTestDataset<>(
				i -> new AccountWithVersion(i, BigDecimal.TEN),
				context -> context.getBean(AccountWithVersionRepository.class),
				true
			).toArguments(),
			new SingleTestDataset<>(
				i -> new AccountWithVersion(i, BigDecimal.TEN),
				context -> context.getBean(AccountWithVersionRepository.class),
				false
			).toArguments()
		);
	}
	
	private record SingleTestDataset<T extends Account>(
		Function<Integer, T> entityGenerator,
		Function<ApplicationContext, ? extends EclipseStoreRepository<T, Integer>> repositoryGenerator,
		boolean previouslyExisting
	)
	{
		public Arguments toArguments()
		{
			return Arguments.of(this);
		}
	}
	
	@ParameterizedTest
	@MethodSource("generateData")
	<T extends Account> void saveConcurrently(
		final SingleTestDataset<T> dataset,
		@Autowired final PlatformTransactionManager transactionManager,
		@Autowired final ApplicationContext context
	) throws InterruptedException
	{
		final EclipseStoreRepository<T, Integer> repository = dataset.repositoryGenerator().apply(context);
		final List<T> testAccounts =
			IntStream.range(1, 1000).mapToObj(dataset.entityGenerator::apply).toList();
		if(dataset.previouslyExisting())
		{
			repository.saveAll(testAccounts);
		}
		
		final ExecutorService service = Executors.newFixedThreadPool(10);
		final CountDownLatch latch = new CountDownLatch(testAccounts.size());
		testAccounts.forEach(
			account ->
				service.execute(() ->
					{
						if(dataset.previouslyExisting())
						{
							Assertions.assertEquals(
								BigDecimal.TEN,
								repository.findById(account.getId()).get().getBalance());
						}
						new TransactionTemplate(transactionManager).execute(
							status ->
							{
								account.setBalance(account.getBalance().subtract(BigDecimal.ONE));
								repository.save(account);
								return null;
							});
						Assertions.assertEquals(
							BigDecimal.valueOf(9),
							repository.findById(account.getId()).get().getBalance());
						latch.countDown();
					}
				)
		);
		
		assertTrue(latch.await(5, TimeUnit.SECONDS));
		
		final List<T> accounts = TestUtil.iterableToList(repository.findAll());
		assertEquals(testAccounts.size(), accounts.size());
		accounts.forEach(account -> Assertions.assertEquals(BigDecimal.valueOf(9), account.getBalance()));
	}
	
	/**
	 * Here it is enough if all the executions are running through. The final balance of the account varies with
	 * different CPUs.
	 */
	@Test
	void testSaveConcurrentlyChangesOnSameAccount(
		@Autowired final AccountNoVersionRepository repository,
		@Autowired final PlatformTransactionManager transactionManager,
		@Autowired final ApplicationContext context)
		throws InterruptedException
	{
		final AccountNoVersion account = new AccountNoVersion(1, BigDecimal.ZERO);
		repository.save(account);
		
		final ExecutorService service = Executors.newFixedThreadPool(10);
		final CountDownLatch latch = new CountDownLatch(100);
		IntStream.range(0, 100).forEach(
			i ->
				service.execute(() ->
					{
						new TransactionTemplate(transactionManager).execute(
							status ->
							{
								final AccountNoVersion loadedAccount = repository.findById(1).get();
								loadedAccount.setBalance(loadedAccount.getBalance().add(BigDecimal.ONE));
								repository.save(loadedAccount);
								return null;
							});
						latch.countDown();
					}
				)
		);
		
		assertTrue(latch.await(5, TimeUnit.SECONDS));
	}
	
	@ParameterizedTest
	@MethodSource("generateData")
	<T extends Account> void testSaveConcurrentlyChangesOnSameAccountMassRollback(
		final SingleTestDataset<T> dataset,
		@Autowired final PlatformTransactionManager transactionManager,
		@Autowired final ApplicationContext context)
		throws InterruptedException
	{
		final EclipseStoreRepository<T, Integer> repository = dataset.repositoryGenerator().apply(context);
		final T account = dataset.entityGenerator.apply(1);
		repository.save(account);
		
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
										final T loadedAccount =
											repository.findById(1).get();
										loadedAccount.setBalance(loadedAccount.getBalance().add(BigDecimal.ONE));
										repository.save(loadedAccount);
										throw new RuntimeException("Random exception");
									})
						);
						latch.countDown();
					}
				)
		);
		
		assertTrue(latch.await(5, TimeUnit.SECONDS));
		Assertions.assertEquals(BigDecimal.TEN, repository.findById(1).get().getBalance());
	}
}
