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
package software.xdev.spring.data.eclipse.store.repository.support.id;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import jakarta.annotation.Nonnull;

import software.xdev.spring.data.eclipse.store.exceptions.FieldAccessReflectionException;
import software.xdev.spring.data.eclipse.store.exceptions.NoIdFieldFoundException;
import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;
import software.xdev.spring.data.eclipse.store.repository.access.modifier.FieldAccessModifier;
import software.xdev.spring.data.eclipse.store.repository.support.AnnotatedFieldFinder;


@SuppressWarnings("java:S119")
public class IdManager<T, ID> implements EntityGetterById<T, ID>, IdGetter<T, ID>
{
	private final Class<T> classWithId;
	private final IdSetter<T> idSetter;
	private final Optional<Field> idField;
	private final EclipseStoreStorage storage;
	
	public IdManager(
		final Class<T> classWithId,
		final IdSetter<T> idSetter,
		final EclipseStoreStorage storage
	)
	{
		this.classWithId = classWithId;
		this.idSetter = idSetter;
		this.storage = storage;
		this.idField = AnnotatedFieldFinder.findIdField(this.classWithId);
	}
	
	public Field ensureIdField()
	{
		if(this.idField.isEmpty())
		{
			throw new NoIdFieldFoundException(String.format(
				"Could not find id field in class %s",
				this.classWithId.getSimpleName()));
		}
		return this.idField.get();
	}
	
	@Override
	public Optional<T> findById(@Nonnull final ID id)
	{
		this.ensureIdField();
		return this.storage.getReadWriteLock().read(
			() -> this.storage
				.getEntityProvider(this.classWithId)
				.findAnyEntityWithId(id)
		);
	}
	
	public List<T> findAllById(@Nonnull final Iterable<ID> idsToFind)
	{
		this.ensureIdField();
		
		return StreamSupport
			.stream(idsToFind.spliterator(), false)
			.map(this::findById)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.toList();
	}
	
	public IdSetter<T> getIdSetter()
	{
		return this.idSetter;
	}
	
	@Override
	public ID getId(final T entity)
	{
		if(this.hasIdField())
		{
			try(final FieldAccessModifier<T> fam = FieldAccessModifier.prepareForField(
				this.ensureIdField(),
				entity))
			{
				return (ID)fam.getValueOfField(entity);
			}
			catch(final Exception e)
			{
				throw new FieldAccessReflectionException(this.ensureIdField(), e);
			}
		}
		return null;
	}
	
	public boolean hasIdField()
	{
		return this.idField.isPresent();
	}
	
	/**
	 * This method makes sure, that an id is set for the given object. If it is already set (not null), then nothing is
	 * done. If it is not set, a new one will be generated and set.
	 */
	public void ensureId(final T objectToSetIdIn)
	{
		this.getIdSetter().ensureId(objectToSetIdIn);
	}
	
	public <S extends T> void checkIds(final Collection<S> entities)
	{
		if(!this.hasIdField())
		{
			return;
		}
		final List<ID> ids = entities
			.stream()
			.map(this::getId)
			.toList();
		
		if(!this.getIdSetter().isAutomaticSetter() && ids.contains(null))
		{
			final Optional<S> entityWithNullId =
				entities.stream().filter(entity -> this.getId(entity) == null).findAny();
			if(entityWithNullId.isPresent())
			{
				throw new IllegalArgumentException(
					"Invalid ID (null) for entity " + entityWithNullId
				);
			}
		}
		
		final List<ID> multipleEqualIds = ids.stream()
			.filter(
				id -> id != null
					&& !id.equals(this.idSetter.getDefaultValue())
					&& Collections.frequency(ids, id) > 1)
			.toList();
		if(!multipleEqualIds.isEmpty())
		{
			throw new IllegalArgumentException(
				"Same ID %s is set multiple times in one save call ".formatted(multipleEqualIds.get(0))
			);
		}
	}
}
