/*
 * Copyright © 2023 XDEV Software (https://xdev.software)
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

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.xdev.spring.data.eclipse.store.exceptions.FieldAccessReflectionException;


/**
 * {@link Field}s are made <b>modifiable/editable</b> with this class. Should be created through
 * {@link FieldAccessModifier}.
 * <p>
 * Can be used like this because it is {@link AutoCloseable}:
 * <p>
 * <code>
 * try(final FieldAccessModifier fem = FieldAccessModifier.makeFieldEditable(field,sourceObject))<br/> {<br/> // Do
 * something with the field.<br/> }<br/>
 * </code>
 * <p>
 * This is a quite shaky way to make final variables "unfinal". Should work for java versions lower than 18.
 * <p>
 * <a href="https://stackoverflow.com/a/56043252/2351407">https://stackoverflow.com/a/56043252/2351407</a>
 */
@SuppressWarnings("java:S3011")
public class FieldEditableMakerForJavaLowerThan18<E> implements FieldAccessModifierToEditable<E>
{
	private static final Logger LOG = LoggerFactory.getLogger(FieldEditableMakerForJavaLowerThan18.class);
	private static final VarHandle MODIFIERS;
	
	static
	{
		try
		{
			final var lookup = MethodHandles.privateLookupIn(Field.class, MethodHandles.lookup());
			MODIFIERS = lookup.findVarHandle(Field.class, "modifiers", int.class);
		}
		catch(final IllegalAccessException | NoSuchFieldException ex)
		{
			throw new FieldAccessReflectionException(
				"Can't find modifiers in Fields. Please check if you're using a java version lower than 18.",
				ex);
		}
	}
	
	private final Field field;
	private final boolean wasFinal;
	private final boolean wasAccessible;
	
	FieldEditableMakerForJavaLowerThan18(final Field field, final E sourceObject)
	{
		this.field = Objects.requireNonNull(field);
		final int fieldModifiers = field.getModifiers();
		this.wasAccessible = field.canAccess(Objects.requireNonNull(sourceObject));
		this.wasFinal = Modifier.isFinal(fieldModifiers);
		this.startMakingEditable();
	}
	
	private void startMakingEditable()
	{
		this.field.setAccessible(true);
		if(!this.wasFinal)
		{
			if(LOG.isTraceEnabled())
			{
				LOG.trace(
					"Make field {}#{} editable.",
					this.field.getDeclaringClass().getSimpleName(),
					this.field.getName());
			}
			MODIFIERS.set(this.field, this.field.getModifiers() & ~Modifier.FINAL);
		}
	}
	
	@Override
	public void close()
	{
		if(!this.wasFinal)
		{
			if(LOG.isTraceEnabled())
			{
				LOG.trace(
					"Make field {}#{} immutable.",
					this.field.getDeclaringClass().getSimpleName(),
					this.field.getName());
			}
			MODIFIERS.set(this.field, this.field.getModifiers() | Modifier.FINAL);
		}
		this.field.setAccessible(this.wasAccessible);
	}
	
	@Override
	public Object getValueOfField(final E objectOfFieldToRead) throws IllegalAccessException
	{
		return this.field.get(objectOfFieldToRead);
	}
	
	@Override
	public void writeValueOfField(final E objectOfFieldToWriteTo, final Object valueToWrite)
		throws IllegalAccessException
	{
		this.field.set(objectOfFieldToWriteTo, valueToWrite);
	}
}
