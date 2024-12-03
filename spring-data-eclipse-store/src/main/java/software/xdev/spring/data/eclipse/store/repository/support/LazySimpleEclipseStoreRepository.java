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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import jakarta.annotation.Nonnull;

import org.eclipse.serializer.reference.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

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
	implements
	LazyEclipseStoreRepository<T, ID>,
	LazyEclipseStorePagingAndSortingRepositoryRepository<T, ID>,
	LazyEclipseStoreListPagingAndSortingRepositoryRepository<T, ID>,
	LazyEclipseStoreCrudRepository<T, ID>,
	LazyEclipseStoreListCrudRepository<T, ID>,
	LazyEclipseStoreQueryByExampleExecutor<T>
{
	private final SimpleEclipseStoreRepository<Lazy<T>, ID> repository;
	
	public LazySimpleEclipseStoreRepository(
		final EclipseStoreStorage storage,
		final WorkingCopier<Lazy<T>> copier,
		final Class<Lazy<T>> domainClass,
		final EclipseStoreTransactionManager transactionManager,
		final IdManager<Lazy<T>, ID> idManager
	)
	{
		this.repository =
			new SimpleEclipseStoreRepository<>(storage, copier, domainClass, transactionManager, idManager);
	}
	
	@Override
	public void deleteById(@Nonnull final ID id)
	{
		repository.deleteById(id);
	}
	
	public <S extends T> List<S> saveBulk(final Collection<S> entities)
	{
		return repository.saveBulk(entities);
	}
	
	@Nonnull
	public <S extends T> S save(@Nonnull final S entity)
	{
		return repository.save(entity);
	}
	
	@Nonnull
	public <S extends T> List<S> saveAll(@Nonnull final Iterable<S> entities)
	{
		return repository.saveAll(entities);
	}
	
	@Override
	@Nonnull
	public Optional<T> findById(@Nonnull final ID id)
	{
		return repository.findById(id);
	}
	
	@Override
	public boolean existsById(@Nonnull final ID id)
	{
		return repository.existsById(id);
	}
	
	@Override
	@Nonnull
	public List<T> findAll()
	{
		return repository.findAll();
	}
	
	@Override
	@Nonnull
	public List<T> findAllById(@Nonnull final Iterable<ID> idsToFind)
	{
		return repository.findAllById(idsToFind);
	}
	
	@Override
	public long count()
	{
		return repository.count();
	}
	
	public void delete(@Nonnull final T entity)
	{
		repository.delete(entity);
	}
	
	@Override
	public void deleteAllById(final Iterable<? extends ID> ids)
	{
		repository.deleteAllById(ids);
	}
	
	public void deleteAll(final Iterable<? extends T> entities)
	{
		repository.deleteAll(entities);
	}
	
	@Override
	public void deleteAll()
	{
		repository.deleteAll();
	}
	
	@Override
	@Nonnull
	public List<T> findAll(@Nonnull final Sort sort)
	{
		return repository.findAll(sort);
	}
	
	@Override
	@Nonnull
	public Page<T> findAll(@Nonnull final Pageable pageable)
	{
		return repository.findAll(pageable).;
	}
	
	public <S extends T> Optional<S> findOne(final Example<S> example)
	{
		return repository.findOne(example);
	}
	
	public <S extends T> Iterable<S> findAll(final Example<S> example)
	{
		return repository.findAll(example);
	}
	
	public <S extends T> Iterable<S> findAll(final Example<S> example, final Sort sort)
	{
		return repository.findAll(example, sort);
	}
	
	public <S extends T> Page<S> findAll(final Example<S> example, final Pageable pageable)
	{
		return repository.findAll(example, pageable);
	}
	
	public <S extends T> long count(final Example<S> example)
	{
		return repository.count(example);
	}
	
	public <S extends T> boolean exists(final Example<S> example)
	{
		return repository.exists(example);
	}
	
	public <S extends T, R> R findBy(
		final Example<S> example,
		final Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction)
	{
		return repository.findBy(example, queryFunction);
	}
}
