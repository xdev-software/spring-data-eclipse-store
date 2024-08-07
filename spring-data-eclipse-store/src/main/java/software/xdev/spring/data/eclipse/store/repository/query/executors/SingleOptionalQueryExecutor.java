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
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;

import software.xdev.spring.data.eclipse.store.core.EntityProvider;
import software.xdev.spring.data.eclipse.store.repository.query.criteria.Criteria;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopier;


/**
 * Queries entities and returns the result wrapped in an optional.
 *
 * @param <T> Entity-Type to query
 */
public class SingleOptionalQueryExecutor<T> implements QueryExecutor<T>
{
	private static final Logger LOG = LoggerFactory.getLogger(SingleOptionalQueryExecutor.class);
	private final Criteria<T> criteria;
	private final WorkingCopier<T> copier;
	private final Optional<Sort> staticSort;
	
	public SingleOptionalQueryExecutor(final WorkingCopier<T> copier, final Criteria<T> criteria, final Sort sort)
	{
		this.criteria = Objects.requireNonNull(criteria);
		this.copier = Objects.requireNonNull(copier);
		this.staticSort = Optional.ofNullable(sort);
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @return an optional with the found entity. {@link Optional#empty()} if nothing was found.
	 */
	@Override
	public Optional<T> execute(
		final Class<T> clazz,
		@Nullable final EntityProvider<T, ?> entities,
		@Nullable final Object[] values)
	{
		Objects.requireNonNull(clazz);
		if(entities == null || entities.isEmpty())
		{
			return Optional.empty();
		}
		Stream<? extends T> entityStream = entities
			.stream()
			.filter(this.criteria::evaluate)
			.map(this.copier::copy);
		
		if(this.staticSort.isPresent())
		{
			entityStream = EntitySorter.sortEntitiesStream(clazz, this.staticSort.get(), entityStream);
		}
		
		final Optional<? extends T> result = entityStream.findFirst();
		if(LOG.isDebugEnabled())
		{
			LOG.debug(
				"Query for class {} found an entity: {}",
				clazz.getSimpleName(),
				result.isPresent()
			);
		}
		return (Optional<T>)result;
	}
}
