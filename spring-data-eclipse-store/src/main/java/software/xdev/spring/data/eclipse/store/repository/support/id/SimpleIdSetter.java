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
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

import software.xdev.spring.data.eclipse.store.exceptions.FieldAccessReflectionException;
import software.xdev.spring.data.eclipse.store.exceptions.IdFieldException;
import software.xdev.spring.data.eclipse.store.repository.access.modifier.FieldAccessModifier;
import software.xdev.spring.data.eclipse.store.repository.support.id.strategy.IdFinder;


@SuppressWarnings("java:S119")
public class SimpleIdSetter<T, ID> implements IdSetter<T>
{
	private final IdFinder<ID> idFinder;
	private final Field idField;
	private final Consumer<ID> lastIdPersister;
	
	public SimpleIdSetter(final Field idField, final IdFinder<ID> idFinder, final Consumer<Object> lastIdPersister)
	{
		this.idField = idField;
		this.idFinder = idFinder;
		this.lastIdPersister = lastIdPersister::accept;
		this.checkIfIdFieldIsFinal();
	}
	
	private void checkIfIdFieldIsFinal()
	{
		final int fieldModifiers = this.idField.getModifiers();
		if(Modifier.isFinal(fieldModifiers))
		{
			throw new IdFieldException(String.format(
				"Field %s is final and cannot be modified. ID fields must not be final.",
				this.idField.getName()));
		}
	}
	
	@Override
	public void ensureId(final T objectToSetIdIn)
	{
		try(final FieldAccessModifier<T> fam = FieldAccessModifier.prepareForField(
			this.idField,
			objectToSetIdIn))
		{
			final Object existingId = fam.getValueOfField(objectToSetIdIn);
			if(existingId == null || existingId.equals(this.idFinder.getDefaultValue()))
			{
				final ID newId = this.idFinder.findId();
				fam.writeValueOfField(objectToSetIdIn, newId, true);
				this.lastIdPersister.accept(newId);
			}
		}
		catch(final Exception e)
		{
			throw new FieldAccessReflectionException(this.idField, e);
		}
	}
	
	@Override
	public boolean isAutomaticSetter()
	{
		return true;
	}
	
	@Override
	public Object getDefaultValue()
	{
		return this.idFinder.getDefaultValue();
	}
}
