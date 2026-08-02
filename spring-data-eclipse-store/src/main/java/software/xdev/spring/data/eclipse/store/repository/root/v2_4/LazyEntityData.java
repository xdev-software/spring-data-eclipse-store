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
import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.serializer.reference.Lazy;
import org.eclipse.serializer.reference.Referencing;

import software.xdev.spring.data.eclipse.store.core.IdentitySet;
import software.xdev.spring.data.eclipse.store.repository.support.id.IdGetter;


/**
 * @param <T>  type of entity to store
 * @param <ID> type of id of the entity to store. Can be {@link Void} if no ID is used.
 */
public class LazyEntityData<T, ID> implements EntityData<T, ID>
{
	private final IdentitySet<Lazy<T>> entities;
	private ID lastId;
	/**
	 * "Why do you keep the entites at two places? This seems like a waste of space." - Yes, it seems like it is
	 * duplicated information, but we need to be able to find an entity based on its identity as fast as possible
	 * ({@link #entities} and also find an entity based on its id {@link #entitiesById}. We could use the BiMaps
	 * provided by google and apache, but they implemented this also with two seperate lists so there is no benefit in
	 * using these.
	 */
	private final HashMap<ID, Lazy<T>> entitiesById;
	
	private transient IdGetter<T, ID> idGetter;
	
	public LazyEntityData()
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
		return this.entities.stream().map(Referencing::get);
	}
	
	@Override
	public boolean containsEntity(final T entity)
	{
		if(this.idGetter == null)
		{
			return this.entities
				.stream()
				.anyMatch(lazyEntity -> lazyEntity.get() == entity);
		}
		else
		{
			final ID id = this.idGetter.getId(entity);
			final Lazy<T> existingEntity = this.entitiesById.get(id);
			return existingEntity != null && existingEntity.get() == entity;
		}
	}
	
	private void ensureEntitiesAndEntitiesByIdAreTheSameSize()
	{
		if(this.idGetter != null && this.entities.size() != this.entitiesById.size())
		{
			this.entitiesById.clear();
			this.entities.forEach(entity -> this.entitiesById.put(this.idGetter.getId(entity.get()), entity));
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
	public T getEntityById(final ID id)
	{
		final Lazy<T> lazyEntity = this.entitiesById.get(id);
		return lazyEntity == null ? null : lazyEntity.get();
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
		if(this.idGetter == null && !this.containsEntity(entityToStore))
		{
			// Does not create a SpringDataEclipseStore.Lazy Instance, but a EclipseStore.Lazy because this is
			// outside the scope of the SpringDataEclipseStore.Lazy. No conversion is taking place.
			this.entities.add(Lazy.Reference(entityToStore));
			listToSave = this.getObjectsToStore();
		}
		if(this.idGetter != null)
		{
			final Lazy<T> existingEntity = this.entitiesById.get(this.idGetter.getId(entityToStore));
			if(existingEntity == null || existingEntity.get() != entityToStore)
			{
				this.entities.remove(existingEntity);
				// Does not create a SpringDataEclipseStore.Lazy Instance, but a EclipseStore.Lazy because this is
				// outside the scope of the SpringDataEclipseStore.Lazy. No conversion is taking place.
				final Lazy<T> newLazyInstance = Lazy.Reference(entityToStore);
				this.entities.add(newLazyInstance);
				this.entitiesById.put(this.idGetter.getId(entityToStore), newLazyInstance);
				listToSave = this.getObjectsToStore();
			}
		}
		return listToSave;
	}
	
	@Override
	public Collection<Object> getObjectsToStore()
	{
		return List.of(this.entities.getInternalMap(), this.entitiesById);
	}
	
	@Override
	public Collection<Object> removeEntityAndReturnObjectsToStore(final T entityToRemove)
	{
		if(this.idGetter == null)
		{
			this.entities
				.stream()
				.filter(entity -> entity.get() == entityToRemove)
				.findAny()
				.ifPresent(this.entities::remove);
		}
		else
		{
			final ID id = this.idGetter.getId(entityToRemove);
			final Lazy<T> lazyReference = this.entitiesById.get(id);
			this.entities.remove(lazyReference);
			this.entitiesById.remove(id);
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
	
	public Map<ID, Lazy<T>> getNativeLazyEntitiesById()
	{
		return this.entitiesById;
	}
}
