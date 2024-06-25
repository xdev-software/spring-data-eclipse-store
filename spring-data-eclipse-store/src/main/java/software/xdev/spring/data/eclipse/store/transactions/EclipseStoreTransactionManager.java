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
package software.xdev.spring.data.eclipse.store.transactions;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;


public class EclipseStoreTransactionManager extends AbstractPlatformTransactionManager
{
	private static final String TRANSACTION_MANAGER = "ATransactionManagerForThisThread";
	
	@Override
	protected Object doGetTransaction() throws TransactionException
	{
		final EclipseStoreExistingTransactionObject transactionObject =
			(EclipseStoreExistingTransactionObject)TransactionSynchronizationManager.getResource(TRANSACTION_MANAGER);
		return transactionObject == null ? new EclipseStoreExistingTransactionObject() : transactionObject;
	}
	
	@Override
	protected void doBegin(final Object transaction, final TransactionDefinition definition) throws TransactionException
	{
		final EclipseStoreExistingTransactionObject transactionObject =
			this.extractEclipseStoreTransaction(transaction);
		transactionObject.startTransaction();
		TransactionSynchronizationManager.bindResource(TRANSACTION_MANAGER, transactionObject);
	}
	
	@Override
	protected void doCommit(final DefaultTransactionStatus status) throws TransactionException
	{
		this.extractEclipseStoreTransaction(status.getTransaction()).commitTransaction();
	}
	
	@Override
	protected void doRollback(final DefaultTransactionStatus status) throws TransactionException
	{
		this.extractEclipseStoreTransaction(status.getTransaction()).rollbackTransaction();
	}
	
	@Override
	protected void doCleanupAfterCompletion(final Object transaction)
	{
		TransactionSynchronizationManager.unbindResource(TRANSACTION_MANAGER);
	}
	
	private EclipseStoreExistingTransactionObject extractEclipseStoreTransaction(final Object transaction)
	{
		Assert.isInstanceOf(
			EclipseStoreExistingTransactionObject.class, transaction,
			() -> String.format(
				"Expected to find a %s but it turned out to be %s.",
				EclipseStoreExistingTransactionObject.class,
				transaction.getClass()));
		
		return (EclipseStoreExistingTransactionObject)transaction;
	}
	
	public EclipseStoreTransaction getTransaction()
	{
		final EclipseStoreTransaction transactionObject =
			(EclipseStoreTransaction)TransactionSynchronizationManager.getResource(TRANSACTION_MANAGER);
		return transactionObject == null ? new EclipseStoreNoTransactionObject() : transactionObject;
	}
}
