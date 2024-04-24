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
package software.xdev.spring.data.eclipse.store.repository.support.copier;

import java.util.Objects;
import java.util.Set;

import jakarta.annotation.Nonnull;

import software.xdev.spring.data.eclipse.store.repository.lazy.SpringDataEclipseStoreLazy;


public final class DataTypeUtil
{
	private static final Set<Class<?>> WRAPPER_TYPES = Set.of(
		Integer.class,
		Byte.class,
		Character.class,
		Boolean.class,
		Double.class,
		Float.class,
		Long.class,
		Short.class,
		Void.class,
		String.class
	);
	
	private DataTypeUtil()
	{
	}
	
	public static boolean isObjectArray(final Object obj)
	{
		return obj instanceof Object[];
	}
	
	public static boolean isSpringDataEclipseStoreLazy(final Object obj)
	{
		return obj instanceof SpringDataEclipseStoreLazy<?>;
	}
	
	public static boolean isPrimitiveArray(final Object obj)
	{
		return obj instanceof boolean[]
			|| obj instanceof byte[] || obj instanceof short[]
			|| obj instanceof char[] || obj instanceof int[]
			|| obj instanceof long[] || obj instanceof float[]
			|| obj instanceof double[];
	}
	
	public static <T> boolean isPrimitiveType(@Nonnull final Class<T> source)
	{
		Objects.requireNonNull(source);
		if(WRAPPER_TYPES.contains(source))
		{
			return true;
		}
		return source.isPrimitive();
	}
}
