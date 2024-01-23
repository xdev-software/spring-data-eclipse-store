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
package software.xdev.spring.data.eclipse.store.repository.query.executors;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.stream.Stream;

import org.springframework.data.domain.Sort;

import software.xdev.spring.data.eclipse.store.exceptions.NotComparableException;
import software.xdev.spring.data.eclipse.store.repository.access.AccessHelper;
import software.xdev.spring.data.eclipse.store.util.GenericObjectComparer;


public final class EntitySorter
{
	private EntitySorter()
	{
	}
	
	public static <T> Stream<T> sortEntitiesStream(final Class<T> clazz, final Sort sort, final Stream<T> entityStream)
	{
		if(sort != null)
		{
			for(final Sort.Order order : sort)
			{
				try
				{
					final Field fieldForOrder =
						AccessHelper.getInheritedPrivateField(clazz, order.getProperty());
					final Comparator<? super T> comparator =
						EntitySorter.getComparator(fieldForOrder, order.getDirection());
					return entityStream.sorted(comparator);
				}
				catch(final NoSuchFieldException e)
				{
					throw new NotComparableException(String.format(
						"Could not sort entities by property %s",
						order.getProperty()));
				}
			}
		}
		return entityStream;
	}
	
	private static <T> Comparator<? super T> getComparator(final Field fieldForOrder, final Sort.Direction direction)
	{
		return (e1, e2) -> {
			final int result = GenericObjectComparer.compare(
				AccessHelper.readFieldVariable(fieldForOrder, e1),
				AccessHelper.readFieldVariable(fieldForOrder, e2)
			);
			if(direction.isDescending())
			{
				return result * -1;
			}
			return result;
		};
	}
}
