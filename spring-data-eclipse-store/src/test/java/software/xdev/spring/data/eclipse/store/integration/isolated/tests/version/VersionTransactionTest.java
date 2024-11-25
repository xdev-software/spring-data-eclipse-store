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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.version;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {VersionTestConfiguration.class})
class VersionTransactionTest
{
	private final VersionTestConfiguration configuration;
	
	@Autowired
	public VersionTransactionTest(final VersionTestConfiguration configuration)
	{
		this.configuration = configuration;
	}
	
	/**
	 * Save-Calls are not squashed to one save, but executed consecutive. This is the current behavior and should
	 * not be
	 * changed.
	 */
	@ParameterizedTest
	@MethodSource("software.xdev.spring.data.eclipse.store.integration.isolated.tests.version"
		+ ".VersionTest#generateData")
	<T extends VersionedEntity<?>> void doubleSave(
		final SingleTestDataset<T> data,
		@Autowired final ApplicationContext context,
		@Autowired final PlatformTransactionManager transactionManager
	)
	{
		final EclipseStoreRepository<T, ?> repository = data.repositoryGenerator().apply(context);
		final T entity = data.enitityGenerator().apply(TestData.FIRST_NAME);
		repository.save(entity);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() ->
				Assertions.assertThrows(
					jakarta.persistence.OptimisticLockException.class, () ->
						new TransactionTemplate(transactionManager).execute(
							status ->
							{
								final T firstLoadedEntry = repository.findAll().get(0);
								final T secondLoadedEntry = repository.findAll().get(0);
								
								secondLoadedEntry.setName(TestData.FIRST_NAME_ALTERNATIVE);
								repository.save(firstLoadedEntry);
								repository.save(secondLoadedEntry);
								return null;
							}
						)
				)
		);
	}
	
	@ParameterizedTest
	@MethodSource("software.xdev.spring.data.eclipse.store.integration.isolated.tests.version"
		+ ".VersionTest#generateData")
	<T extends VersionedEntity<?>> void transactionSave(
		final SingleTestDataset<T> data,
		@Autowired final ApplicationContext context,
		@Autowired final PlatformTransactionManager transactionManager
	)
	{
		final EclipseStoreRepository<T, ?> repository = data.repositoryGenerator().apply(context);
		final T entity = data.enitityGenerator().apply(TestData.FIRST_NAME);
		repository.save(entity);
		
		if(data.firstVersion() != null)
		{
			Assertions.assertEquals(data.firstVersion(), repository.findAll().get(0).getVersion());
		}
		
		new TransactionTemplate(transactionManager).execute(
			status ->
			{
				final T loadedEntry = repository.findAll().get(0);
				loadedEntry.setName(TestData.FIRST_NAME_ALTERNATIVE);
				repository.save(loadedEntry);
				return null;
			}
		);
		
		if(data.secondVersion() != null)
		{
			Assertions.assertEquals(data.secondVersion(), repository.findAll().get(0).getVersion());
		}
		Assertions.assertEquals(TestData.FIRST_NAME_ALTERNATIVE, repository.findAll().get(0).getName());
	}
}
