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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import software.xdev.spring.data.eclipse.store.repository.access.AccessHelper;


public final class AnnotatedFieldFinder
{
	private AnnotatedFieldFinder()
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
		return findAnnotatedField(
			domainClass,
			List.of(jakarta.persistence.Id.class, org.springframework.data.annotation.Id.class)
		);
	}
	
	/**
	 * Finds any field in a class with an Version-Annotation ({@link jakarta.persistence.Version} or
	 * {@link org.springframework.data.annotation.Version}). Finds this field recursively in the Hierarchy-tree.
	 *
	 * @return field with Version-Annotation. Is {@link Optional#empty()} if no field was found.
	 */
	public static Optional<Field> findVersionField(final Class<?> domainClass)
	{
		return findAnnotatedField(
			domainClass,
			List.of(jakarta.persistence.Version.class, org.springframework.data.annotation.Version.class)
		);
	}
	
	/**
	 * Finds any field in a class with specified annotations. Finds this field recursively in the Hierarchy-tree.
	 *
	 * @return field with annotation. Is {@link Optional#empty()} if no field was found.
	 */
	public static Optional<Field> findAnnotatedField(
		final Class<?> domainClass,
		final Collection<Class<? extends Annotation>> annotations)
	{
		final Collection<Field> classFields = AccessHelper.getInheritedPrivateFieldsByName(domainClass).values();
		for(final Field currentField : classFields)
		{
			for(final Class<? extends Annotation> annotation : annotations)
			{
				if(currentField.getAnnotationsByType(annotation).length > 0)
				{
					return Optional.of(currentField);
				}
			}
		}
		return Optional.empty();
	}
}
