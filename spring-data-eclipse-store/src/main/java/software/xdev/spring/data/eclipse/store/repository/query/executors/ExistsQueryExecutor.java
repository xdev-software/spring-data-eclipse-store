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

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.xdev.spring.data.eclipse.store.repository.query.criteria.Criteria;


/**
 * Queries entities and returns the result wrapped in an optional.
 *
 * @param <T> Entity-Type to query
 */
public class ExistsQueryExecutor<T> implements QueryExecutor<T>
{
	private static final Logger LOG = LoggerFactory.getLogger(ExistsQueryExecutor.class);
	private final Criteria<T> criteria;
	
	public ExistsQueryExecutor(final Criteria<T> criteria)
	{
		this.criteria = Objects.requireNonNull(criteria);
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @return whether the entity exists
	 */
	@Override
	public Boolean execute(
		final Class<T> clazz,
		@Nullable final Collection<T> entities,
		@Nullable final Object[] values)
	{
		Objects.requireNonNull(clazz);
		if(entities == null || entities.isEmpty())
		{
			return false;
		}
		final Stream<T> entityStream = entities
			.stream()
			.filter(this.criteria::evaluate);
		
		final Optional<T> result = entityStream.findAny();
		if(LOG.isDebugEnabled())
		{
			LOG.debug(
				"Query for class {} found an entity: {}",
				clazz.getSimpleName(),
				result.isPresent()
			);
		}
		return result.isPresent();
	}
}
