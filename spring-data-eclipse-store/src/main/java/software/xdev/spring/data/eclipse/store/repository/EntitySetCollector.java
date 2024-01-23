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
package software.xdev.spring.data.eclipse.store.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.xdev.spring.data.eclipse.store.core.IdentitySet;


public class EntitySetCollector
{
	private static final Logger LOG = LoggerFactory.getLogger(EntitySetCollector.class);
	private final Map<Class<?>, List<IdentitySet<? super Object>>> childClassToParentSets = new HashMap<>();
	
	public EntitySetCollector(
		final Map<String, IdentitySet<Object>> entityLists,
		final Map<Class<?>, String> entityClassToRepositoryName)
	{
		this.buildParentClassList(entityLists, entityClassToRepositoryName);
	}
	
	private void buildParentClassList(
		final Map<String, IdentitySet<Object>> entityLists,
		final Map<Class<?>, String> entityClassToRepositoryName
	)
	{
		if(LOG.isDebugEnabled())
		{
			LOG.debug("Initializing parent class list...");
		}
		for(final Class<?> possibleParentClass : entityClassToRepositoryName.keySet())
		{
			for(final Class<?> possibleChildClass : entityClassToRepositoryName.keySet())
			{
				this.childClassToParentSets.putIfAbsent(
					possibleChildClass, new ArrayList<>()
				);
				if(possibleParentClass.isAssignableFrom(possibleChildClass))
				{
					this.childClassToParentSets.get(possibleChildClass)
						.add(entityLists.get(entityClassToRepositoryName.get(possibleParentClass)));
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
	public <T> List<IdentitySet<? super Object>> getRelatedIdentitySets(final Class<T> clazz)
	{
		return this.childClassToParentSets.get(clazz);
	}
}
