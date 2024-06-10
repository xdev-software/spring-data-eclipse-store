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


/**
 * Queries the actual entities from the original entities.
 *
 * @param <T> Entity-Type to query
 */
public interface QueryExecutor<T>
{
	/**
	 * Executes the created query over entities.
	 * <p>
	 * The result is always <b>a working copy</b> of the entities.
	 * </p>
	 *
	 * @param clazz    of the entities
	 * @param entities where the query is executed on
	 * @param values   for the query. These are values that might be compared to entities.
	 * @return entities/entity on which the conditions match.
	 */
	Object execute(Class<T> clazz, final Collection<T> entities, Object[] values);
}
