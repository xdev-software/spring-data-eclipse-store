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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Provides a way to open member variables (fields) to changes.
 */
public interface FieldAccessModifier<E> extends AutoCloseable
{
	AtomicInteger javaVersion = new AtomicInteger(0);
	
	static <T> FieldAccessModifierToEditable<T> makeFieldEditable(final Field field, final T sourceObject)
		throws InvocationTargetException, IllegalAccessException
	{
		if(javaVersion.get() == 0)
		{
			javaVersion.set(getJavaVersion());
		}
		if(javaVersion.get() < 18)
		{
			return new FieldEditableMakerForJavaLowerThan18<>(
				Objects.requireNonNull(field),
				Objects.requireNonNull(sourceObject)
			);
		}
		return new FieldEditableMakerForJavaHigherOrEqualTo18<>(
			Objects.requireNonNull(field),
			Objects.requireNonNull(sourceObject)
		);
	}
	
	static <T> FieldAccessModifier<T> makeFieldReadable(final Field field, final T sourceObject)
	{
		return new FieldAccessibleMaker<>(Objects.requireNonNull(field), Objects.requireNonNull(sourceObject));
	}
	
	Object getValueOfField(E objectOfFieldToRead) throws IllegalAccessException;
	
	private static int getJavaVersion()
	{
		String version = System.getProperty("java.version");
		if(version.startsWith("1."))
		{
			version = version.substring(2, 3);
		}
		else
		{
			final int dot = version.indexOf(".");
			if(dot != -1)
			{
				version = version.substring(0, dot);
			}
		}
		return Integer.parseInt(version);
	}
}
