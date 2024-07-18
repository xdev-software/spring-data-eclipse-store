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
import java.lang.reflect.Modifier;

import software.xdev.spring.data.eclipse.store.exceptions.FieldAccessReflectionException;
import software.xdev.spring.data.eclipse.store.exceptions.IdFieldFinalException;
import software.xdev.spring.data.eclipse.store.repository.access.modifier.FieldAccessModifier;
import software.xdev.spring.data.eclipse.store.repository.support.copier.version.incrementer.VersionIncrementer;


public class SimpleEntityVersionIncrementer<T, VERSION> implements EntityVersionIncrementer<T>
{
	private final VersionIncrementer<VERSION> versionIncrementer;
	private final Field versionField;
	
	public SimpleEntityVersionIncrementer(
		final Field versionField,
		final VersionIncrementer<VERSION> versionIncrementer)
	{
		this.versionField = versionField;
		this.versionIncrementer = versionIncrementer;
		this.checkIfVersionFieldIsFinal();
	}
	
	private void checkIfVersionFieldIsFinal()
	{
		final int fieldModifiers = this.versionField.getModifiers();
		if(Modifier.isFinal(fieldModifiers))
		{
			throw new IdFieldFinalException(String.format(
				"Field %s is final and cannot be modified. Version fields must not be final.",
				this.versionField.getName()));
		}
	}
	
	@Override
	public void incrementVersion(final T objectToSetVersionIn)
	{
		try(final FieldAccessModifier<T> fam = FieldAccessModifier.prepareForField(
			this.versionField,
			objectToSetVersionIn))
		{
			final VERSION existingVersion = (VERSION)fam.getValueOfField(objectToSetVersionIn);
			final VERSION newVersion = this.versionIncrementer.increment(existingVersion);
			fam.writeValueOfField(objectToSetVersionIn, newVersion, true);
		}
		catch(final Exception e)
		{
			throw new FieldAccessReflectionException(this.versionField, e);
		}
	}
}
