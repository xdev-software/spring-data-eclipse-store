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

import java.util.ArrayList;

import org.springframework.transaction.TransactionSystemException;


public class EclipseStoreExistingTransactionObject implements EclipseStoreTransaction
{
	private ArrayList<EclipseStoreTransactionAction> actions;
	
	public void startTransaction()
	{
		if(this.actions != null)
		{
			throw new TransactionSystemException(
				"Transaction is already started but it should start again. This is not allowed!");
		}
		this.actions = new ArrayList<>();
	}
	
	public void rollbackTransaction()
	{
		this.actions = null;
	}
	
	public void commitTransaction()
	{
		if(this.actions == null)
		{
			throw new TransactionSystemException(
				"Transaction is not started but actions should be executed. This is not allowed!");
		}
		this.actions.forEach(EclipseStoreTransactionAction::execute);
		this.actions = null;
	}
	
	@Override
	public void addAction(final EclipseStoreTransactionAction action)
	{
		if(action == null)
		{
			return;
		}
		if(this.actions == null)
		{
			throw new TransactionSystemException(
				"Transaction is not started but action should be added. This is not allowed!");
		}
		this.actions.add(action);
	}
}
