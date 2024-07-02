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
import java.util.stream.Stream;

import jakarta.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.xdev.spring.data.eclipse.store.core.EntityProvider;
import software.xdev.spring.data.eclipse.store.repository.query.criteria.Criteria;


/**
 * Executes queries that are optionally sorted and paged in collections.
 **/
public class CountQueryExecutor<T> implements QueryExecutor<T>
{
	private static final Logger LOG = LoggerFactory.getLogger(CountQueryExecutor.class);
	private final Criteria<T> criteria;
	
	public CountQueryExecutor(final Criteria<T> criteria)
	{
		this.criteria = criteria;
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @return a list of the found/sorted/paged entities
	 */
	@Override
	public Long execute(final Class<T> clazz, @Nullable final EntityProvider<T> entities, final Object[] values)
	{
		Objects.requireNonNull(entities);
		
		final Stream<? extends T> entityStream = entities
			.stream()
			.filter(this.criteria::evaluate);
		
		final long result = entityStream.count();
		
		if(LOG.isTraceEnabled())
		{
			LOG.trace("Found {} entries.", result);
		}
		return result;
	}
}
