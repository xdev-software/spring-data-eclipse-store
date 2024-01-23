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
package software.xdev.spring.data.eclipse.store.repository.support.copier;

import java.util.HashSet;
import java.util.Objects;

import jakarta.annotation.Nonnull;

public class DataTypeUtil
{
	private static final HashSet<Class<?>> WRAPPER_TYPES;
	
	static
	{
		WRAPPER_TYPES = new HashSet<>(10);
		WRAPPER_TYPES.add(Integer.class);
		WRAPPER_TYPES.add(Byte.class);
		WRAPPER_TYPES.add(Character.class);
		WRAPPER_TYPES.add(Boolean.class);
		WRAPPER_TYPES.add(Double.class);
		WRAPPER_TYPES.add(Float.class);
		WRAPPER_TYPES.add(Long.class);
		WRAPPER_TYPES.add(Short.class);
		WRAPPER_TYPES.add(Void.class);
		WRAPPER_TYPES.add(String.class);
	}
	
	private DataTypeUtil()
	{
		// Only static methods available
	}
	
	public static boolean isObjectArray(final Object obj)
	{
		return obj instanceof Object[];
	}
	
	public static boolean isPrimitiveArray(final Object obj)
	{
		return obj instanceof boolean[] ||
			obj instanceof byte[] || obj instanceof short[] ||
			obj instanceof char[] || obj instanceof int[] ||
			obj instanceof long[] || obj instanceof float[] ||
			obj instanceof double[];
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
