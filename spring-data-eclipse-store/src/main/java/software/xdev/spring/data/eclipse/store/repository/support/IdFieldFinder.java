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
package software.xdev.spring.data.eclipse.store.repository.support;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Optional;

import software.xdev.spring.data.eclipse.store.repository.access.AccessHelper;


public final class IdFieldFinder
{
	private IdFieldFinder()
	{
	}
	
	/**
	 * Finds any field in a class with an ID-Annotation ({@link jakarta.persistence.Id} or
	 * {@link org.springframework.data.annotation.Id}). Finds this field recursively in the Hierarchy-tree.
	 *
	 * @return field with ID-Annotation. Is {@link Optional#empty()} if no field was found.
	 */
	public static Optional<Field> findIdField(final Class<?> domainClass)
	{
		final Collection<Field> classFields = AccessHelper.getInheritedPrivateFieldsByName(domainClass).values();
		for(final Field currentField : classFields)
		{
			if(
				currentField.getAnnotationsByType(jakarta.persistence.Id.class).length > 0
					|| currentField.getAnnotationsByType(org.springframework.data.annotation.Id.class).length > 0
			)
			{
				return Optional.of(currentField);
			}
		}
		return Optional.empty();
	}
}
