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
package software.xdev.spring.data.eclipse.store.repository.support.copier.version;

import java.lang.reflect.Field;
import java.util.Optional;

import jakarta.persistence.OptimisticLockException;

import software.xdev.spring.data.eclipse.store.exceptions.FieldAccessReflectionException;
import software.xdev.spring.data.eclipse.store.exceptions.InvalidVersionException;
import software.xdev.spring.data.eclipse.store.repository.access.modifier.FieldAccessModifier;
import software.xdev.spring.data.eclipse.store.repository.support.AnnotatedFieldFinder;


/**
 * Manages the versions of one type of entity
 *
 * @param <T> type of entity
 */
public class VersionManager<T>
{
	private final Optional<Field> versionField;
	private final EntityVersionIncrementer<T> entityVersionIncrementer;
	
	public VersionManager(
		final Class<T> entityClass,
		final EntityVersionIncrementer<T> entityVersionIncrementer
	)
	{
		this.versionField = AnnotatedFieldFinder.findVersionField(entityClass);
		this.entityVersionIncrementer = entityVersionIncrementer;
	}
	
	/**
	 * Increments the version of given entity
	 *
	 * @param entity to increment the version in
	 */
	public void incrementVersion(final T entity)
	{
		this.entityVersionIncrementer.incrementVersion(entity);
	}
	
	/**
	 * Checks if the two entities are valid for merge. If they have the same version, everything is ok. But if the
	 * workingCopy-version is null or different from the original-version, an Exception is thrown.
	 */
	public void ensureSameVersion(final T workingCopy, final T original)
	{
		if(this.versionField.isPresent() && original != null)
		{
			try(final FieldAccessModifier<T> fam1 = FieldAccessModifier.prepareForField(
				this.versionField.get(),
				original))
			{
				final Object originalValue = fam1.getValueOfField(original);
				if(originalValue == null)
				{
					return;
				}
				try(final FieldAccessModifier<T> fam2 = FieldAccessModifier.prepareForField(
					this.versionField.get(),
					workingCopy))
				{
					final Object workingCopyValue = fam2.getValueOfField(workingCopy);
					if(workingCopyValue == null)
					{
						throw new InvalidVersionException(
							"Trying to an existing versioned entity with an entity without version (version is null) "
								+ "is not permitted."
						);
					}
					if(!workingCopyValue.equals(originalValue))
					{
						throw new OptimisticLockException(
							"Versions are not equal: original version=\"%s\" new version=\"%s\"".formatted(
								originalValue,
								workingCopyValue
							)
						);
					}
				}
			}
			catch(final OptimisticLockException | InvalidVersionException e)
			{
				throw e;
			}
			catch(final Exception e)
			{
				throw new FieldAccessReflectionException(this.versionField.get(), e);
			}
		}
	}
}
