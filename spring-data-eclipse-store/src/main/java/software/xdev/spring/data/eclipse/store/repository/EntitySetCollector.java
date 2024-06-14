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
package software.xdev.spring.data.eclipse.store.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.xdev.spring.data.eclipse.store.core.IdentitySet;


public class EntitySetCollector
{
	private static final Logger LOG = LoggerFactory.getLogger(EntitySetCollector.class);
	private final Map<Class<?>, List<IdentitySet<? super Object>>> childClassToParentSets = new HashMap<>();
	
	public EntitySetCollector(
		final Function<Class<?>, IdentitySet<?>> entityLists,
		final Set<Class<?>> entityClasses)
	{
		this.buildParentClassList(entityLists, entityClasses);
	}
	
	private void buildParentClassList(
		final Function<Class<?>, IdentitySet<?>> entityLists,
		final Set<Class<?>> entityClasses
	)
	{
		if(LOG.isDebugEnabled())
		{
			LOG.debug("Initializing parent class list...");
		}
		for(final Class<?> possibleParentEntry : entityClasses)
		{
			for(final Class<?> possibleChildEntry : entityClasses)
			{
				this.childClassToParentSets.putIfAbsent(
					possibleChildEntry, new ArrayList<>()
				);
				if(possibleParentEntry.isAssignableFrom(possibleChildEntry))
				{
					this.childClassToParentSets.get(possibleChildEntry)
						.add((IdentitySet<? super Object>)entityLists.apply(possibleParentEntry));
				}
			}
		}
		if(LOG.isDebugEnabled())
		{
			LOG.debug("Done initializing parent class list.");
		}
	}
	
	/**
	 * @return a list with all related IdentitySets (including its own).
	 */
	public <T> List<IdentitySet<Object>> getRelatedIdentitySets(final Class<T> clazz)
	{
		return this.childClassToParentSets.get(clazz);
	}
}
