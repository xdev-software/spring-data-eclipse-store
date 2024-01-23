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
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import software.xdev.spring.data.eclipse.store.repository.query.criteria.Criteria;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopier;


/**
 * Executes queries that are optionally sorted and paged in collections.
 **/
public class PageableSortableCollectionQuerier<T>
{
	private static final Logger LOG = LoggerFactory.getLogger(PageableSortableCollectionQuerier.class);
	private final Criteria<T> criteria;
	private final WorkingCopier<T> copier;
	private final Optional<Sort> staticSort;
	
	public PageableSortableCollectionQuerier(final WorkingCopier<T> copier, final Criteria<T> criteria)
	{
		this(copier, criteria, null);
	}
	
	public PageableSortableCollectionQuerier(
		final WorkingCopier<T> copier, final Criteria<T> criteria,
		final Sort sort)
	{
		this.criteria = Objects.requireNonNull(criteria);
		this.copier = Objects.requireNonNull(copier);
		this.staticSort = Optional.ofNullable(sort);
	}
	
	protected List<T> getEntities(
		@Nonnull final Collection<T> entities,
		@Nullable final Pageable pageable,
		@Nullable final Class<T> clazz,
		@Nullable final Sort sort)
	{
		Objects.requireNonNull(entities);
		
		Stream<T> entityStream = entities
			.stream()
			.filter(this.criteria::evaluate);
		
		final Sort sortToUse = this.staticSort.orElse(sort);
		if(sortToUse != null)
		{
			entityStream = EntitySorter.sortEntitiesStream(clazz, sortToUse, entityStream);
		}
		
		entityStream = this.pageEntityStream(pageable, entityStream);
		
		final List<T> result = this.copyEntities(entityStream);
		
		if(LOG.isTraceEnabled())
		{
			LOG.trace("Found {} entries.", result.size());
		}
		return result;
	}
	
	private List<T> copyEntities(final Stream<T> filteredEntityStream)
	{
		return filteredEntityStream
			.map(this.copier::copy)
			.toList();
	}
	
	private Stream<T> pageEntityStream(final Pageable pageable, final Stream<T> entityStream)
	{
		if(pageable != null && pageable.isPaged())
		{
			final long skipCount = pageable.getOffset();
			return entityStream
				.skip(skipCount)
				.limit(pageable.getPageSize());
		}
		return entityStream;
	}
	
	protected List<T> getEntities(
		final Collection<T> entities,
		final Pageable pageable,
		final Class<T> clazz)
	{
		return this.getEntities(entities, pageable, clazz, pageable.getSort());
	}
	
	protected List<T> getEntities(
		final Collection<T> entities,
		final Class<T> clazz,
		final Sort sort)
	{
		return this.getEntities(entities, null, clazz, sort);
	}
	
	protected List<T> getEntities(final Collection<T> entities, final Class<T> clazz)
	{
		return this.getEntities(entities, null, clazz, null);
	}
}
