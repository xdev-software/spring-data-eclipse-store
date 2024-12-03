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

import java.util.List;

import org.eclipse.serializer.reference.Lazy;

import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;
import software.xdev.spring.data.eclipse.store.repository.interfaces.lazy.LazyEclipseStoreCrudRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.lazy.LazyEclipseStoreListCrudRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.lazy.LazyEclipseStoreListPagingAndSortingRepositoryRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.lazy.LazyEclipseStorePagingAndSortingRepositoryRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.lazy.LazyEclipseStoreQueryByExampleExecutor;
import software.xdev.spring.data.eclipse.store.repository.interfaces.lazy.LazyEclipseStoreRepository;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopier;
import software.xdev.spring.data.eclipse.store.repository.support.id.IdManager;
import software.xdev.spring.data.eclipse.store.transactions.EclipseStoreTransactionManager;


@SuppressWarnings("java:S119")
public class LazySimpleEclipseStoreRepository<T, ID>
	extends SimpleEclipseStoreRepository<Lazy<T>, ID>
	implements
	LazyEclipseStoreRepository<T, ID>,
	LazyEclipseStorePagingAndSortingRepositoryRepository<T, ID>,
	LazyEclipseStoreListPagingAndSortingRepositoryRepository<T, ID>,
	LazyEclipseStoreCrudRepository<T, ID>,
	LazyEclipseStoreListCrudRepository<T, ID>,
	LazyEclipseStoreQueryByExampleExecutor<T>
{
	
	public LazySimpleEclipseStoreRepository(
		final EclipseStoreStorage storage,
		final WorkingCopier<Lazy<T>> copier,
		final Class<Lazy<T>> domainClass,
		final EclipseStoreTransactionManager transactionManager,
		final IdManager<Lazy<T>, ID> idManager
	)
	{
		super(storage, copier, domainClass, transactionManager, idManager);
	}
	
	@Override
	public void deleteEntity(final T entity)
	{
	
	}
	
	@Override
	public void deleteAllEntities(final Iterable<? extends T> entities)
	{
	
	}
	
	@Override
	public <S extends T> List<S> saveAllEntities(final Iterable<S> entities)
	{
		return List.of();
	}
	
	@Override
	public <S extends T> S saveEntity(final S entity)
	{
		return null;
	}
}
