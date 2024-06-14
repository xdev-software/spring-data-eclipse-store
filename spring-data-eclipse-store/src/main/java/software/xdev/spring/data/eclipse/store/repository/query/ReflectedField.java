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
package software.xdev.spring.data.eclipse.store.repository.query;

import java.lang.reflect.Field;
import java.util.Objects;

import jakarta.annotation.Nonnull;

import software.xdev.spring.data.eclipse.store.exceptions.FieldAccessReflectionException;
import software.xdev.spring.data.eclipse.store.repository.access.AccessHelper;


/**
 * Simple wrapper for a field to make it easily readable. If the fields is not accessible, it is made accessible with
 * the {@link AccessHelper#readFieldVariable(Field, Object)}.
 *
 * @param <T> Input
 * @param <E> Value
 */
public class ReflectedField<T, E>
{
	private final Field field;
	
	public ReflectedField(final Field field)
	{
		this.field = Objects.requireNonNull(field);
	}
	
	public static <T, E> ReflectedField<T, E> createReflectedField(final Class<T> domainClass, final String fieldName)
	{
		try
		{
			return new ReflectedField<>(AccessHelper.getInheritedPrivateField(domainClass, fieldName));
		}
		catch(final NoSuchFieldException e)
		{
			throw new FieldAccessReflectionException(String.format(
				"Field %s in class %s was not found!",
				fieldName,
				domainClass.getSimpleName()), e);
		}
	}
	
	/**
	 * Reads the field of the given object. If the fields is not accessible, it is made accessible with the
	 * {@link AccessHelper#readFieldVariable(Field, Object)}.
	 *
	 * @param object to read the field of
	 * @return value of the field in the given object.
	 */
	@SuppressWarnings("unchecked")
	public E readValue(@Nonnull final T object)
	{
		return (E)AccessHelper.readFieldVariable(Objects.requireNonNull(this.field), Objects.requireNonNull(object));
	}
}
