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

import java.util.HashMap;
import java.util.Map;

import software.xdev.spring.data.eclipse.store.core.IdentitySet;


/**
 * This is the root object for all versions <2.0.0 and is used for upgrading to the new root.
 * @deprecated should not be initialised any more. Version for <2.0.0
 */
@Deprecated(forRemoval = false, since = "2.0.0")
public class Root
{
	private final Map<String, IdentitySet<Object>> entityLists;
	private final Map<String, Object> lastIds;
	
	public Root()
	{
		this.entityLists = new HashMap<>();
		this.lastIds = new HashMap<>();
	}
	
	public Map<String, IdentitySet<Object>> getEntityLists()
	{
		return this.entityLists;
	}
	
	public <T> IdentitySet<T> getEntityList(final Class<T> entityClass)
	{
		return (IdentitySet<T>)this.entityLists.get(this.getEntityName(entityClass));
	}
	
	public <T> void createNewEntityList(final Class<T> entityClass)
	{
		this.entityLists.put(this.getEntityName(entityClass), new IdentitySet<>());
	}
	
	private <T> String getEntityName(final Class<T> classToRegister)
	{
		return classToRegister.getName();
	}
	
	public Object getLastId(final Class<?> entityClass)
	{
		return this.getLastIds().get(this.getEntityName(entityClass));
	}
	
	public void setLastId(final Class<?> entityClass, final Object lastId)
	{
		this.getLastIds().put(this.getEntityName(entityClass), lastId);
	}
	
	public Map<String, Object> getLastIds()
	{
		return this.lastIds;
	}
}
