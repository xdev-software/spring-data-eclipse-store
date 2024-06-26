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
package software.xdev.spring.data.eclipse.store.repository.support.copier.id;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.Nonnull;

import software.xdev.spring.data.eclipse.store.exceptions.FieldAccessReflectionException;
import software.xdev.spring.data.eclipse.store.exceptions.NoIdFieldFoundException;
import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;
import software.xdev.spring.data.eclipse.store.repository.access.modifier.FieldAccessModifier;
import software.xdev.spring.data.eclipse.store.repository.support.IdFieldFinder;


public class IdManager<T, ID> implements EntityGetterById<T, ID>
{
	private final Class<T> domainClass;
	private final IdSetter<T, ID> idSetter;
	private final Optional<Field> idField;
	private final EclipseStoreStorage storage;
	
	public IdManager(
		final Class<T> domainClass,
		final IdSetter<T, ID> idSetter,
		final EclipseStoreStorage storage
	)
	{
		this.domainClass = domainClass;
		this.idSetter = idSetter;
		this.storage = storage;
		this.idField = IdFieldFinder.findIdField(this.domainClass);
	}
	
	public Field ensureIdField()
	{
		if(this.idField.isEmpty())
		{
			throw new NoIdFieldFoundException(String.format(
				"Could not find id field in class %s",
				this.domainClass.getSimpleName()));
		}
		return this.idField.get();
	}
	
	@Override
	public Optional<T> findById(@Nonnull final ID id)
	{
		this.ensureIdField();
		return this.storage.getReadWriteLock().read(
			() -> this.storage
				.getEntityList(this.domainClass)
				.parallelStream()
				.filter(
					entity ->
					{
						try(final FieldAccessModifier<T> fam = FieldAccessModifier.prepareForField(
							this.ensureIdField(),
							entity))
						{
							if(id.equals(fam.getValueOfField(entity)))
							{
								return true;
							}
						}
						catch(final Exception e)
						{
							throw new FieldAccessReflectionException(String.format(
								FieldAccessReflectionException.COULD_NOT_READ_FIELD,
								this.ensureIdField().getName()), e);
						}
						return false;
					}
				)
				.findAny()
		);
	}
	
	public List<T> findAllById(@Nonnull final Iterable<ID> idsToFind)
	{
		this.ensureIdField();
		return this.storage.getReadWriteLock().read(
			() -> this.storage
				.getEntityList(this.domainClass)
				.parallelStream()
				.filter(
					entity ->
					{
						try(final FieldAccessModifier<T> fam = FieldAccessModifier.prepareForField(
							this.ensureIdField(),
							entity))
						{
							final Object idOfEntity = fam.getValueOfField(entity);
							for(final ID idToFind : idsToFind)
							{
								if(idToFind.equals(idOfEntity))
								{
									return true;
								}
							}
						}
						catch(final Exception e)
						{
							throw new FieldAccessReflectionException(String.format(
								FieldAccessReflectionException.COULD_NOT_READ_FIELD,
								this.ensureIdField().getName()), e);
						}
						return false;
					}
				)
				.toList()
		);
	}
	
	public IdSetter<T, ID> getIdSetter()
	{
		return this.idSetter;
	}
}
