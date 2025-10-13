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
package software.xdev.spring.data.eclipse.store.repository.access;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.xdev.spring.data.eclipse.store.exceptions.FieldAccessReflectionException;
import software.xdev.spring.data.eclipse.store.repository.access.modifier.FieldAccessModifier;
import software.xdev.spring.data.eclipse.store.util.StringUtil;


/**
 * Helps finding and handling {@link Field}s.
 */
public final class AccessHelper
{
	private static final Logger LOG = LoggerFactory.getLogger(AccessHelper.class);
	
	private AccessHelper()
	{
	}
	
	/**
	 * @param clazz where to look for fields.
	 * @return all the fields defined in the given class and all its parent classes.
	 */
	public static Map<String, Field> getInheritedPrivateFieldsByName(final Class<?> clazz)
	{
		Objects.requireNonNull(clazz);
		
		final Map<String, Field> result = new HashMap<>();
		
		Class<?> i = clazz;
		while(i != null && i != Object.class)
		{
			for(final Field field : i.getDeclaredFields())
			{
				if(!field.isSynthetic())
				{
					result.put(field.getName(), field);
				}
			}
			i = i.getSuperclass();
		}
		return result;
	}
	
	/**
	 * @param clazz     where the field is searched in.
	 * @param fieldName of the field that is searched in the class.
	 * @return Not only the field with a specific name in the given class but also finds the field if it is inherited
	 * from some parent class.
	 * @throws NoSuchFieldException if the field is not existing. It does <b>not</b> return {@code null} because this
	 *                              should not happen during production.
	 */
	public static Field getInheritedPrivateField(final Class<?> clazz, final String fieldName)
		throws NoSuchFieldException
	{
		Objects.requireNonNull(clazz);
		StringUtil.requireNonNullAndNonBlank(fieldName);
		
		final Map<String, Field> inheritedPrivateFieldsByName = getInheritedPrivateFieldsByName(clazz);
		if(LOG.isDebugEnabled())
		{
			LOG.debug("Found {} fields in class {}.", inheritedPrivateFieldsByName.size(), clazz.getSimpleName());
		}
		if(!inheritedPrivateFieldsByName.containsKey(fieldName))
		{
			throw new NoSuchFieldException(String.format(
				"Could not find field %s in class %s",
				fieldName,
				clazz.getSimpleName()));
		}
		return inheritedPrivateFieldsByName.get(fieldName);
	}
	
	/**
	 * Makes the given field of the given object readable/accessible and returns its value. After reading the value the
	 * field is made unreadable/inaccessible again (if it was unreadable before).
	 */
	public static <T> Object readFieldVariable(final Field field, final T sourceObject)
	{
		try(final FieldAccessModifier<T> fieldAccessModifier = FieldAccessModifier.prepareForField(
			Objects.requireNonNull(field),
			Objects.requireNonNull(sourceObject)))
		{
			return fieldAccessModifier.getValueOfField(sourceObject);
		}
		catch(final Exception e)
		{
			throw new FieldAccessReflectionException(field, e);
		}
	}
	
	public static void checkAllFieldsForReadRestrictions(final Class<?> clazz)
	{
		getInheritedPrivateFieldsByName(clazz).forEach(
			(fieldName, field) ->
			{
				if(!field.trySetAccessible())
				{
					throw new FieldAccessReflectionException(field);
				}
			}
		);
	}
}
