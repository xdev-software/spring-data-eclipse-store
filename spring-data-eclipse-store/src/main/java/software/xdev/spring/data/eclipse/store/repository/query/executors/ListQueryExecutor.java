/*
 * Copyright © 2023 XDEV Software (https://xdev.software)
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

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import jakarta.annotation.Nullable;
import software.xdev.spring.data.eclipse.store.repository.query.criteria.Criteria;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopier;


/**
 * Queries entities and returns a the result as list.
 *
 * @param <T> Entity-Type to query
 */
public class ListQueryExecutor<T> implements QueryExecutor<T>
{
	private final PageableSortableCollectionQuerier<T> querier;
	
	public ListQueryExecutor(final WorkingCopier<T> copier, final Criteria<T> criteria)
	{
		this.querier = new PageableSortableCollectionQuerier<>(copier, criteria);
	}
	
	public ListQueryExecutor(final WorkingCopier<T> copier, final Criteria<T> criteria, final Sort sort)
	{
		this.querier = new PageableSortableCollectionQuerier<>(copier, criteria, sort);
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @return a list of the found/sorted/paged entities
	 */
	@Override
	public List<T> execute(final Class<T> clazz, @Nullable final Collection<T> entities, final Object[] values)
	{
		Objects.requireNonNull(clazz);
		if(entities == null || entities.isEmpty())
		{
			return List.of();
		}
		if(values != null && values.length > 0)
		{
			if(values[values.length - 1] instanceof final Pageable pageable)
			{
				return this.querier.getEntities(entities, pageable, clazz);
			}
			if(values[values.length - 1] instanceof final Sort sort)
			{
				return this.querier.getEntities(entities, clazz, sort);
			}
		}
		return this.querier.getEntities(entities, clazz);
	}
}
