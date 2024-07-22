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
package software.xdev.spring.data.eclipse.store.repository.query.executors;

import java.util.Objects;

import jakarta.annotation.Nullable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import software.xdev.spring.data.eclipse.store.core.EntityProvider;
import software.xdev.spring.data.eclipse.store.exceptions.NoPageableObjectFoundException;
import software.xdev.spring.data.eclipse.store.repository.query.criteria.Criteria;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopier;


/**
 * Queries entities and returns a the result as page.
 *
 * @param <T> Entity-Type to query
 */
public class PageableQueryExecutor<T> implements QueryExecutor<T>
{
	private final PageableSortableCollectionQuerier<T> querier;
	private final CountQueryExecutor<T> countQueryExecutor;
	
	public PageableQueryExecutor(final WorkingCopier<T> copier, final Criteria<T> criteria, final Sort sort)
	{
		this.querier = new PageableSortableCollectionQuerier<>(copier, criteria, sort);
		this.countQueryExecutor = new CountQueryExecutor<>(criteria);
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @return a page of the found/sorted/paged entities
	 */
	@Override
	public Page<T> execute(final Class<T> clazz, @Nullable final EntityProvider<T> entities, final Object[] values)
	{
		Objects.requireNonNull(clazz);
		
		if(entities == null || entities.isEmpty())
		{
			return Page.empty();
		}
		if(values != null && values.length > 0)
		{
			if(values[values.length - 1] instanceof final Pageable pageable)
			{
				final Long total = this.countQueryExecutor.execute(clazz, entities, null);
				return new PageImpl<>(this.querier.getEntities(entities, pageable, clazz), pageable, total);
			}
			if(values[values.length - 1] instanceof final Sort sort)
			{
				return new PageImpl<>(this.querier.getEntities(entities, clazz, sort));
			}
		}
		throw new NoPageableObjectFoundException(
			"Could not execute query, because there is no Pageable or Sort object given.");
	}
}
