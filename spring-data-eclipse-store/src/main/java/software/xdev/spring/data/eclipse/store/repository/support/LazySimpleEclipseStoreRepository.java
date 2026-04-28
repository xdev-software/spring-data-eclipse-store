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
package software.xdev.spring.data.eclipse.store.repository.support;

import software.xdev.spring.data.eclipse.store.repository.StorageCommunicator;
import software.xdev.spring.data.eclipse.store.repository.interfaces.lazy.LazyEclipseStoreCrudRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.lazy.LazyEclipseStoreListCrudRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.lazy.LazyEclipseStoreListPagingAndSortingRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.lazy.LazyEclipseStorePagingAndSortingRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.lazy.LazyEclipseStoreQueryByExampleExecutor;
import software.xdev.spring.data.eclipse.store.repository.interfaces.lazy.LazyEclipseStoreRepository;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopier;
import software.xdev.spring.data.eclipse.store.repository.support.id.IdManager;
import software.xdev.spring.data.eclipse.store.transactions.EclipseStoreTransactionManager;


@SuppressWarnings("java:S119")
public class LazySimpleEclipseStoreRepository<T, ID>
	extends SimpleEclipseStoreRepository<T, ID>
	implements
	LazyEclipseStoreRepository<T, ID>,
	LazyEclipseStorePagingAndSortingRepository<T, ID>,
	LazyEclipseStoreListPagingAndSortingRepository<T, ID>,
	LazyEclipseStoreCrudRepository<T, ID>,
	LazyEclipseStoreListCrudRepository<T, ID>,
	LazyEclipseStoreQueryByExampleExecutor<T>
{
	public LazySimpleEclipseStoreRepository(
		final StorageCommunicator storage,
		final WorkingCopier<T> copier,
		final Class<T> domainClass,
		final EclipseStoreTransactionManager transactionManager,
		final IdManager<T, ID> idManager)
	{
		super(storage, copier, domainClass, transactionManager, idManager);
	}
	
	@Override
	public boolean isLazy()
	{
		return true;
	}
}
