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
package software.xdev.spring.data.eclipse.store.repository.query.by.example;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import software.xdev.spring.data.eclipse.store.repository.StorageCommunicator;
import software.xdev.spring.data.eclipse.store.repository.query.criteria.CriteriaByExample;
import software.xdev.spring.data.eclipse.store.repository.query.executors.CountQueryExecutor;
import software.xdev.spring.data.eclipse.store.repository.query.executors.ExistsQueryExecutor;
import software.xdev.spring.data.eclipse.store.repository.query.executors.ListQueryExecutor;
import software.xdev.spring.data.eclipse.store.repository.query.executors.PageableQueryExecutor;
import software.xdev.spring.data.eclipse.store.repository.query.executors.SingleQueryExecutor;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopier;


/**
 * Needed to support {@link QueryByExampleExecutor}.
 */
public class EclipseStoreFetchableFluentQuery<T, S extends T> implements FluentQuery.FetchableFluentQuery<S>
{
	private final WorkingCopier<T> copier;
	private final Example<S> example;
	private final Class<T> domainClass;
	private final StorageCommunicator storage;
	private final Sort sort;
	
	public EclipseStoreFetchableFluentQuery(
		final WorkingCopier<T> copier,
		final Example<S> example,
		final Class<T> domainClass,
		final StorageCommunicator storage,
		final Sort sort
	)
	{
		this.copier = copier;
		this.example = example;
		this.domainClass = domainClass;
		this.storage = storage;
		this.sort = sort;
	}
	
	@Override
	public FetchableFluentQuery<S> sortBy(final Sort sort)
	{
		return new EclipseStoreFetchableFluentQuery(
			this.copier,
			this.example,
			this.domainClass,
			this.storage,
			sort
		);
	}
	
	@Override
	public <R> FetchableFluentQuery<R> as(final Class<R> resultType)
	{
		throw new UnsupportedOperationException("The method as() is not yet supported");
	}
	
	@Override
	public FetchableFluentQuery<S> project(final Collection<String> properties)
	{
		throw new UnsupportedOperationException("The method project() is not yet supported");
	}
	
	@Override
	public S oneValue()
	{
		return this.firstValue();
	}
	
	@Override
	public S firstValue()
	{
		final SingleQueryExecutor<T> query =
			new SingleQueryExecutor<>(this.copier, new CriteriaByExample<>((Example<T>)this.example), this.sort);
		return this.storage.getReadWriteLock().read(
			() ->
				(S)query.execute(
					this.domainClass,
					this.storage.getEntityProvider(this.domainClass),
					new Object[]{this.sort})
		);
	}
	
	@Override
	public List<S> all()
	{
		final ListQueryExecutor<T> query =
			new ListQueryExecutor<>(this.copier, new CriteriaByExample<>(this.example));
		return this.storage.getReadWriteLock().read(
			() -> (List<S>)query.execute(
				this.domainClass,
				this.storage.getEntityProvider(this.domainClass),
				new Object[]{
				this.sort})
		);
	}
	
	@Override
	public Page<S> page(final Pageable pageable)
	{
		final PageableQueryExecutor<T> pageableQuery =
			new PageableQueryExecutor<>(this.copier, new CriteriaByExample<>(this.example), this.sort);
		return this.storage.getReadWriteLock().read(
			() ->
				(Page<S>)pageableQuery.execute(
					this.domainClass,
					this.storage.getEntityProvider(this.domainClass),
					new Object[]{pageable, this.sort})
		);
	}
	
	@Override
	public Stream<S> stream()
	{
		return this.all().stream();
	}
	
	@Override
	public long count()
	{
		final CountQueryExecutor<T> query = new CountQueryExecutor<>(new CriteriaByExample<>(this.example));
		return this.storage.getReadWriteLock().read(
			() -> query.execute(this.domainClass, this.storage.getEntityProvider(this.domainClass), null)
		);
	}
	
	@Override
	public boolean exists()
	{
		final ExistsQueryExecutor<T> query = new ExistsQueryExecutor<>(new CriteriaByExample<>(this.example));
		return this.storage.getReadWriteLock().read(
			() -> query.execute(this.domainClass, this.storage.getEntityProvider(this.domainClass), null)
		);
	}
}
