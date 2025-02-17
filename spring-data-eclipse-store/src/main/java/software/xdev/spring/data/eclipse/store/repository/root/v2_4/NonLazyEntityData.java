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
package software.xdev.spring.data.eclipse.store.repository.root.v2_4;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import software.xdev.spring.data.eclipse.store.core.IdentitySet;
import software.xdev.spring.data.eclipse.store.repository.support.id.IdGetter;


/**
 * @param <T>  type of entity to store
 * @param <ID> type of id of the entity to store. Can be {@link Void} if no ID is used.
 */
public class NonLazyEntityData<T, ID> implements EntityData<T, ID>
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
	
	private transient IdGetter<T, ID> idGetter;
	
	public NonLazyEntityData()
	{
		this.entities = new IdentitySet<>();
		this.entitiesById = new HashMap<>();
	}
	
	/**
	 * Accepts {@code null} if no id field is defined
	 */
	@Override
	public void setIdGetter(final IdGetter<T, ID> idGetter)
	{
		this.idGetter = idGetter;
		
		this.ensureEntitiesAndEntitiesByIdAreTheSameSize();
	}
	
	@Override
	public Stream<T> getEntitiesAsStream()
	{
		return this.entities.stream();
	}
	
	@Override
	public boolean containsEntity(final T entity)
	{
		if(this.idGetter == null)
		{
			return this.entities.contains(entity);
		}
		else
		{
			final ID id = this.idGetter.getId(entity);
			final T existingEntity = this.entitiesById.get(id);
			return existingEntity != null && existingEntity == entity;
		}
	}
	
	private void ensureEntitiesAndEntitiesByIdAreTheSameSize()
	{
		if(this.idGetter != null && this.entities.size() != this.entitiesById.size())
		{
			this.entitiesById.clear();
			this.entities.forEach(entity -> this.entitiesById.put(this.idGetter.getId(entity), entity));
		}
		if(this.idGetter == null)
		{
			this.entitiesById.clear();
		}
	}
	
	@Override
	public ID getLastId()
	{
		return this.lastId;
	}
	
	@Override
	public long getEntityCount()
	{
		return this.entities.size();
	}
	
	@Override
	public void setLastId(final Object lastId)
	{
		this.lastId = (ID)lastId;
	}
	
	@Override
	public Collection<Object> ensureEntityAndReturnObjectsToStore(final T entityToStore)
	{
		Collection<Object> listToSave = List.of();
		if(this.entities.add(entityToStore))
		{
			listToSave = this.getObjectsToStore();
		}
		if(this.idGetter != null && this.entitiesById.get(this.idGetter.getId(entityToStore)) != entityToStore)
		{
			this.entitiesById.put(this.idGetter.getId(entityToStore), entityToStore);
			listToSave = this.getObjectsToStore();
		}
		return listToSave;
	}
	
	@Override
	public Collection<Object> getObjectsToStore()
	{
		return List.of(this.entities.getInternalMap(), this.entitiesById);
	}
	
	@Override
	public T getEntityById(final ID id)
	{
		return this.entitiesById.get(id);
	}
	
	@Override
	public Collection<Object> removeEntityAndReturnObjectsToStore(final T entityToRemove)
	{
		this.entities.remove(entityToRemove);
		if(this.idGetter != null)
		{
			this.entitiesById.remove(this.idGetter.getId(entityToRemove));
		}
		return this.getObjectsToStore();
	}
	
	@Override
	public Collection<Object> removeAllEntitiesAndReturnObjectsToStore()
	{
		this.entities.clear();
		this.entitiesById.clear();
		return this.getObjectsToStore();
	}
}
