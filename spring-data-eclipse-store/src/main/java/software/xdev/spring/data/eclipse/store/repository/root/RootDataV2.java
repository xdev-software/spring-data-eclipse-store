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
package software.xdev.spring.data.eclipse.store.repository.root;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


/**
 * This is a object for all versions {@literal <}2.4.0 and is used for upgrading to the new root.
 * @deprecated should not be initialised anymore. Version for {@literal <}2.4.0
 */
@Deprecated(forRemoval = false, since = "2.4.0")
@SuppressWarnings({"java:S119", "unchecked"})
public class RootDataV2
{
	private final Map<String, EntityData<?, ?>> entityLists;
	
	public RootDataV2()
	{
		this.entityLists = new HashMap<>();
	}
	
	public Map<String, EntityData<?, ?>> getEntityListsToStore()
	{
		return this.entityLists;
	}
	
	public long getEntityTypesCount()
	{
		return this.entityLists.size();
	}
	
	public long getEntityCount()
	{
		return this.entityLists.values().stream().map(EntityData::getEntityCount).reduce(0L, Long::sum);
	}
	
	public <T, ID> EntityData<T, ID> getEntityData(final Class<T> entityClass)
	{
		return this.getEntityData(this.getEntityName(entityClass));
	}
	
	public <T, ID> EntityData<T, ID> getEntityData(final String entityClassName)
	{
		return (EntityData<T, ID>)this.entityLists.get(entityClassName);
	}
	
	public <T, ID> void createNewEntityData(final Class<T> entityClass, final Function<T, ID> idGetter)
	{
		final EntityData<T, ID> entityData = new EntityData<>();
		entityData.setIdGetter(idGetter);
		this.entityLists.put(this.getEntityName(entityClass), entityData);
	}
	
	private <T> String getEntityName(final Class<T> classToRegister)
	{
		return classToRegister.getName();
	}
	
	public Object getLastId(final Class<?> entityClass)
	{
		final EntityData<?, ?> entityData = this.entityLists.get(this.getEntityName(entityClass));
		return entityData == null ? null : entityData.getLastId();
	}
	
	public void setLastId(final Class<?> entityClass, final Object lastId)
	{
		this.entityLists.get(this.getEntityName(entityClass)).setLastId(lastId);
	}
	
	public Object getObjectsToStoreAfterNewLastId(final Class<?> entityClass)
	{
		return this.entityLists.get(this.getEntityName(entityClass));
	}
}
