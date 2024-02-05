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
 * {@inheritDoc}
 * <p>
 * Evaluates to true if either the {@link #leftCriteria} or {@link #rightCriteria} is true.
 */
public class CriteriaOrNode<T> extends AbstractCriteriaNode<T>
{
	private final Criteria<T> leftCriteria;
	private final Criteria<T> rightCriteria;
	
	protected CriteriaOrNode(
		final Criteria<T> leftCriteria,
		final Criteria<T> rightCriteria)
	{
		super(null);
		this.leftCriteria = leftCriteria;
		this.rightCriteria = rightCriteria;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Evaluates to true if either the {@link #leftCriteria} or
	 * {@link #rightCriteria} is true.
	 * </p>
	 */
	@Override
	public boolean evaluate(@Nullable final T object)
	{
		if(this.leftCriteria.evaluate(object))
		{
			return true;
		}
		return this.rightCriteria.evaluate(object);
	}
}
