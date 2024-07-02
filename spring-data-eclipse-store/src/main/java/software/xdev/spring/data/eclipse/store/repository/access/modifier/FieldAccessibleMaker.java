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
package software.xdev.spring.data.eclipse.store.repository.access.modifier;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * {@link Field}s are made <b>readable</b> with this class. Should be created through {@link FieldAccessModifier}.
 * <p>
 * Can be used like this because it is {@link AutoCloseable}:
 * <code>
 * try(final FieldAccessModifier fem = FieldAccessModifier.makeFieldReadable(field,sourceObject))<br/> {<br/> // Read
 * the data from the field.<br/> }<br/>
 * </code>
 */
@SuppressWarnings({"java:S3011", "PMD:PreserveStackTrace"})
public class FieldAccessibleMaker<E> implements FieldAccessModifier<E>
{
	private static final Logger LOG = LoggerFactory.getLogger(FieldAccessibleMaker.class);
	private final Field field;
	private final boolean isFinal;
	
	FieldAccessibleMaker(final Field field, final E sourceObject)
	{
		this.field = field;
		final boolean wasAccessible = field.canAccess(Objects.requireNonNull(sourceObject));
		final int fieldModifiers = field.getModifiers();
		this.isFinal = Modifier.isFinal(fieldModifiers);
		if(!wasAccessible)
		{
			if(LOG.isTraceEnabled())
			{
				LOG.trace(
					"Make field {}#{} accessible.",
					this.field.getDeclaringClass().getSimpleName(),
					this.field.getName());
			}
			this.field.trySetAccessible();
		}
	}
	
	@Override
	public Object getValueOfField(final E objectOfFieldToRead) throws IllegalAccessException
	{
		try
		{
			return this.field.get(objectOfFieldToRead);
		}
		catch(final IllegalAccessException e)
		{
			throw this.createIllegalAccessToField(this.field);
		}
	}
	
	@Override
	public void writeValueOfField(
		final E objectOfFieldToWriteTo,
		final Object valueToWrite,
		final boolean throwExceptionIfFinal)
		throws IllegalAccessException
	{
		if(throwExceptionIfFinal && this.isFinal)
		{
			throw new IllegalAccessException(
				"Field %s#%s is final and cannot be modified. Make the field not final."
					.formatted(
						this.field.getType().getName(),
						this.field.getName()
					)
			);
		}
		try
		{
			this.field.set(objectOfFieldToWriteTo, valueToWrite);
		}
		catch(final IllegalAccessException e)
		{
			throw this.createIllegalAccessToField(this.field);
		}
	}
	
	private IllegalAccessException createIllegalAccessToField(final Field field) throws IllegalAccessException
	{
		return new IllegalAccessException(
			("Could not access field %s#%s. Make sure that the module is open e.g. with following VM option: "
				+ "\"--add-opens %s/%s=ALL-UNNAMED\"")
				.formatted(
					field.getDeclaringClass().getName(),
					field.getName(),
					field.getDeclaringClass().getModule().getName(),
					field.getDeclaringClass().getPackageName()
				)
		);
	}
	
	public boolean isFinal()
	{
		return this.isFinal;
	}
	
	@Override
	public void close()
	{
		// This used to make the field optionally inaccessible again,
		// but was removed due to concurrency issues.
	}
}
