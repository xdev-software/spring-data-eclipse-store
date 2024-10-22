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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import software.xdev.spring.data.eclipse.store.core.IdentitySet;


/**
 * @param <T>  type of entity to store
 * @param <ID> type of id of the entity to store. Can be {@link Void} if no ID is used.
 */
public class EntityData<T, ID>
{
	private final IdentitySet<T> entities;
	private ID lastId;
	/**
	 * "Why do you keep the entites at two places? This seems like a waste of space." - Yes, it seems like it is
	 * duplicated information, but we need to be able to find an entity based on its identity as fast as possible
	 * ({@link #entities} and also find an entity based on its id {@link #entitiesById}. We could use the BiMaps
	 * provided by google and apache, but they implemented this also with two seperate lists so there is no benefit in
	 * using these.
	 */
	private final HashMap<ID, T> entitiesById;
	
	private transient Function<T, ID> idGetter;
	
	public EntityData()
	{
		this.entities = new IdentitySet<>();
		this.entitiesById = new HashMap<>();
	}
	
	/**
	 * Accepts {@code null} if no id field is defined
	 */
	public void setIdGetter(final Function<T, ID> idGetter)
	{
		this.idGetter = idGetter;
		
		this.ensureEntitiesAndEntitiesByIdAreTheSameSize();
	}
	
	private void ensureEntitiesAndEntitiesByIdAreTheSameSize()
	{
		if(this.idGetter != null && this.entities.size() != this.entitiesById.size())
		{
			this.entitiesById.clear();
			this.entities.forEach(entity -> this.entitiesById.put(this.idGetter.apply(entity), entity));
		}
		if(this.idGetter == null)
		{
			this.entitiesById.clear();
		}
	}
	
	public IdentitySet<T> getEntities()
	{
		return this.entities;
	}
	
	public ID getLastId()
	{
		return this.lastId;
	}
	
	public HashMap<ID, T> getEntitiesById()
	{
		return this.entitiesById;
	}
	
	public long getEntityCount()
	{
		return this.entities.size();
	}
	
	public void setLastId(final Object lastId)
	{
		this.lastId = (ID)lastId;
	}
	
	public Collection<Object> ensureEntityAndReturnObjectsToStore(final T entityToStore)
	{
		if(!this.getEntities().contains(entityToStore))
		{
			this.entities.add(entityToStore);
			if(this.idGetter != null)
			{
				this.entitiesById.put(this.idGetter.apply(entityToStore), entityToStore);
			}
			return this.getObjectsToStore();
		}
		return List.of();
	}
	
	public Collection<Object> getObjectsToStore()
	{
		return List.of(this.entities.getInternalMap(), this.entitiesById);
	}
	
	public Collection<Object> removeEntityAndReturnObjectsToStore(final T entityToRemove)
	{
		this.entities.remove(entityToRemove);
		if(this.idGetter != null)
		{
			this.entitiesById.remove(this.idGetter.apply(entityToRemove));
		}
		return this.getObjectsToStore();
	}
	
	public Collection<Object> removeAllEntitiesAndReturnObjectsToStore()
	{
		this.entities.clear();
		this.entitiesById.clear();
		return this.getObjectsToStore();
	}
}
