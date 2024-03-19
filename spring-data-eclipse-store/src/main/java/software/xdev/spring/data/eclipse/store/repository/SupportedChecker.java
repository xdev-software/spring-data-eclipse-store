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
package software.xdev.spring.data.eclipse.store.repository;

import java.util.Calendar;
import java.util.EnumMap;
import java.util.List;
import java.util.WeakHashMap;

import org.eclipse.serializer.collections.lazy.LazyArrayList;
import org.eclipse.serializer.collections.lazy.LazyHashMap;
import org.eclipse.serializer.collections.lazy.LazyHashSet;
import org.eclipse.serializer.reference.Lazy;


/**
 * Checks if a class is supported by the Spring-Data-Eclipse-Store library.
 * <p>
 * Some classes are not supported, because Eclipse Store doesn't support them.
 * </p>
 * <p>
 * {@link Lazy} is not supported, because a lot of hidden stuff must be done to keep Lazy-References really Lazy when
 * creating a working copy.
 * </p>
 */
@FunctionalInterface
public interface SupportedChecker
{
	/**
	 * Checks if a class is supported by the Spring-Data-Eclipse-Store library.
	 * <p>
	 * Some classes are not supported, because Eclipse Store doesn't support them.
	 * </p>
	 */
	boolean isSupported(Class<?> clazz);
	
	class Implementation implements SupportedChecker
	{
		private static final List<Class<?>> UNSUPPORTED_DATA_TYPES = List.of(
			// Here EclipseStore has problems: https://github.com/microstream-one/microstream/issues/173
			Calendar.class,
			WeakHashMap.class,
			// Here EclipseStore has problems too: https://github.com/microstream-one/microstream/issues/204
			EnumMap.class,
			LazyHashMap.class,
			LazyArrayList.class,
			LazyHashSet.class
		);
		
		@Override
		public boolean isSupported(final Class<?> clazz)
		{
			return !UNSUPPORTED_DATA_TYPES.stream().anyMatch(
				unsupportedClazz -> unsupportedClazz.isAssignableFrom(clazz)
			);
		}
	}
}
