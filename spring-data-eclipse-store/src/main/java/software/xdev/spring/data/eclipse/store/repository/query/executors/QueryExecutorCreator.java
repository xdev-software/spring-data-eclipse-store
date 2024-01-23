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

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.TypeInformation;

import software.xdev.spring.data.eclipse.store.repository.query.criteria.Criteria;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopier;


public final class QueryExecutorCreator
{
	private static final Logger LOG = LoggerFactory.getLogger(QueryExecutorCreator.class);
	
	private QueryExecutorCreator()
	{
	}
	
	/**
	 * Creates and returns Query-Executor.
	 *
	 * @param typeInformation about the query to create.
	 * @param copier          that creates working copies of the found entities
	 * @param criteria        to query for entities with specific criteria. Only if the criteria matches, entities are
	 *                        selected.
	 * @param sort            the static sort that is possibly defined through the method name
	 * @param <T>             Entity-Type to query
	 * @return the correct Query-Executor according to the given parameters
	 */
	public static <T> QueryExecutor<T> createQuery(
		final TypeInformation<?> typeInformation,
		final WorkingCopier<T> copier,
		final Criteria<T> criteria,
		final Sort sort)
	{
		if(typeInformation.isCollectionLike())
		{
			if(typeInformation.getType().equals(Page.class))
			{
				if(LOG.isDebugEnabled())
				{
					LOG.debug("Create PageableQuery");
				}
				return new PageableQueryExecutor<>(copier, criteria, sort);
			}
			if(LOG.isDebugEnabled())
			{
				LOG.debug("Create ListQuery");
			}
			return new ListQueryExecutor<>(copier, criteria, sort);
		}
		if(typeInformation.getType().equals(Optional.class))
		{
			if(LOG.isDebugEnabled())
			{
				LOG.debug("Create SingleOptionalQuery");
			}
			return new SingleOptionalQueryExecutor<>(copier, criteria, sort);
		}
		if(LOG.isDebugEnabled())
		{
			LOG.debug("Create SingleQuery");
		}
		return new SingleQueryExecutor<>(copier, criteria, sort);
	}
}
