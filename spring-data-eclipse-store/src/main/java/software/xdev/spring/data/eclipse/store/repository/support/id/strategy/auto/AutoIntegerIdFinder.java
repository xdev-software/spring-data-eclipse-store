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
package software.xdev.spring.data.eclipse.store.repository.support.id.strategy.auto;

import java.util.function.Supplier;


public class AutoIntegerIdFinder extends AbstractAutoIdFinder<Integer>
{
	public AutoIntegerIdFinder(final Supplier<Object> idGetter)
	{
		super(() -> (Integer)idGetter.get());
	}
	
	@Override
	protected Integer getNext(final Integer oldId)
	{
		if(oldId == null || oldId == Integer.MAX_VALUE)
		{
			return 0;
		}
		return oldId + 1;
	}
}
