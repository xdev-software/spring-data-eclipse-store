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
import java.util.Optional;

import org.springframework.data.domain.Sort;

import jakarta.annotation.Nullable;
import software.xdev.spring.data.eclipse.store.repository.query.criteria.Criteria;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopier;


/**
 * Queries entities and returns the found entity.
 *
 * @param <T> Entity-Type to query
 */
public class SingleQueryExecutor<T> implements QueryExecutor<T>
{
	private final SingleOptionalQueryExecutor<T> optionalQueryExecutor;
	
	public SingleQueryExecutor(final WorkingCopier<T> copier, final Criteria<T> criteria, final Sort sort)
	{
		this.optionalQueryExecutor = new SingleOptionalQueryExecutor<>(copier, criteria, sort);
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @return found object. If no object with criteria is found, {@code null} is returned.
	 */
	@Override
	public T execute(
		@Nullable final Class<T> clazz,
		@Nullable final Collection<T> entities,
		@Nullable final Object[] values)
	{
		final Optional<T> optionalResult = this.optionalQueryExecutor.execute(clazz, entities, values);
		return optionalResult.orElse(null);
	}
}
