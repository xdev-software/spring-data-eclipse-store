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
package software.xdev.spring.data.eclipse.store.repository.query.criteria;

import org.springframework.lang.Nullable;

/**
 * Criteria to apply to an entity and check if the criteria is fulfilled or not.
 *
 * @param <T> entity-type to apply the criteria to.
 */
@FunctionalInterface
public interface Criteria<T>
{
	/**
	 * Empty criteria which is <b>always {@code true}</b>.
	 */
	static <T> Criteria<T> createNoCriteria()
	{
		return object -> true;
	}
	
	/**
	 * Checks the criteria against the given object.
	 *
	 * @param object to check the criteria against.
	 * @return {@code true} if the object is within the criteria, {@code false} if not.
	 */
	boolean evaluate(@Nullable final T object);
}
