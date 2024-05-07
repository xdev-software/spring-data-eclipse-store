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
import org.springframework.transaction.annotation.Transactional;

import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {TransactionsTestConfiguration.class})
@Transactional
class TransactionsAnnotationTest
{
	private final AccountRepository repository;
	
	@Autowired
	public TransactionsAnnotationTest(final AccountRepository repository)
	{
		this.repository = repository;
	}
	
	@Test
	void accountTransaction_UnexpectedError_Annotation()
	{
		try
		{
			final Account account1 = new Account(1, BigDecimal.TEN);
			final Account account2 = new Account(2, BigDecimal.ZERO);
			this.repository.saveAll(List.of(account1, account2));
			
			throw new RuntimeException("Unexpected error");
		}
		catch(final RuntimeException e)
		{
		}
		Assertions.assertEquals(0, TestUtil.iterableToList(this.repository.findAll()).size());
	}
}
