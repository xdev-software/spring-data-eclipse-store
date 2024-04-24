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
package software.xdev.spring.data.eclipse.store.repository.query.criteria;

import java.util.function.Predicate;

import org.springframework.lang.Nullable;

import software.xdev.spring.data.eclipse.store.repository.query.ReflectedField;


/**
 * {@inheritDoc}
 * <p>
 * Evaluates to true if all it's own {@link #predicates} is true.
 */
public class CriteriaSingleNode<T> extends AbstractCriteriaNode<T>
{
	public CriteriaSingleNode()
	{
		super(null);
	}
	
	public CriteriaSingleNode(final ReflectedField<T, ?> field)
	{
		super(field);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Checks all it's own {@link #predicates}
	 * </p>
	 */
	@Override
	public boolean evaluate(@Nullable final T object)
	{
		for(final Predicate<T> predicate : this.predicates)
		{
			if(!predicate.test(object))
			{
				return false;
			}
		}
		return true;
	}
}
