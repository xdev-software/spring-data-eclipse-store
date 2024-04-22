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
package software.xdev.spring.data.eclipse.store.util;

import software.xdev.spring.data.eclipse.store.exceptions.NotComparableException;


public final class GenericObjectComparer
{
	private GenericObjectComparer()
	{
	}
	
	public static <E> int compare(final E o1, final E o2)
	{
		if(o1 == null && o2 == null)
		{
			return 0;
		}
		if(o1 == null)
		{
			return -1;
		}
		if(o2 == null)
		{
			return 1;
		}
		if(o1 instanceof final Comparable<?> comparableGeneric)
		{
			final Integer comparingResult = compareWithComparable(comparableGeneric, o2);
			if(comparingResult != null)
			{
				return comparingResult;
			}
		}
		throw new NotComparableException(String.format(
			"Type %s is not comparable. Sorting is not available for that type.",
			o1.getClass().getSimpleName()));
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	private static <T extends Comparable, E> Integer compareWithComparable(
		final T o1,
		final E o2)
	{
		if(Comparable.class.isAssignableFrom(o2.getClass()))
		{
			return o1.compareTo(o2);
		}
		return null;
	}
	
	/**
	 * @return {@code true} if o1 is less than o2, {@code false} if not.
	 */
	public static <E> boolean isLessThan(final E o1, final E o2)
	{
		return compare(o1, o2) < 0;
	}
	
	/**
	 * @return {@code true} if o1 is less or equal to o2, {@code false} if not.
	 */
	public static <E> boolean isLessOrEqualTo(final E o1, final E o2)
	{
		return compare(o1, o2) <= 0;
	}
	
	/**
	 * @return {@code true} if o1 is greater than o2, {@code false} if not.
	 */
	public static <E> boolean isGreaterThan(final E o1, final E o2)
	{
		return compare(o1, o2) > 0;
	}
	
	/**
	 * @return {@code true} if o1 is greater or equal to o2, {@code false} if not.
	 */
	public static <E> boolean isGreaterOrEqualTo(final E o1, final E o2)
	{
		return compare(o1, o2) >= 0;
	}
}
