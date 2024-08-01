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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

import software.xdev.spring.data.eclipse.store.exceptions.IdFieldException;
import software.xdev.spring.data.eclipse.store.exceptions.InvalidVersionException;
import software.xdev.spring.data.eclipse.store.repository.access.AccessHelper;


public final class AnnotatedFieldFinder
{
	private AnnotatedFieldFinder()
	{
	}
	
	/**
	 * Finds any field in a class with an ID-Annotation ({@link jakarta.persistence.Id},
	 * {@link org.springframework.data.annotation.Id} or {@link jakarta.persistence.EmbeddedId}). Finds this field
	 * recursively in the Hierarchy-tree.
	 *
	 * @return field with ID-Annotation. Is {@link Optional#empty()} if no field was found.
	 */
	public static Optional<Field> findIdField(final Class<?> domainClass)
	{
		final List<Field> idFields = findAnnotatedFields(
			domainClass,
			List.of(
				Id.class,
				org.springframework.data.annotation.Id.class,
				EmbeddedId.class)
		);
		
		if(idFields.isEmpty())
		{
			return Optional.empty();
		}
		else
		{
			if(idFields.size() > 1)
			{
				throw new IdFieldException("Only one id field is allowed");
			}
			return Optional.of(idFields.get(0));
		}
	}
	
	/**
	 * Finds any field in a class with an Version-Annotation ({@link jakarta.persistence.Version} or
	 * {@link org.springframework.data.annotation.Version}). Finds this field recursively in the Hierarchy-tree.
	 *
	 * @return field with Version-Annotation. Is {@link Optional#empty()} if no field was found.
	 */
	public static Optional<Field> findVersionField(final Class<?> domainClass)
	{
		final List<Field> versionFields = findAnnotatedFields(
			domainClass,
			List.of(Version.class, org.springframework.data.annotation.Version.class)
		);
		
		if(versionFields.isEmpty())
		{
			return Optional.empty();
		}
		else
		{
			if(versionFields.size() > 1)
			{
				throw new InvalidVersionException("Only one version field is allowed");
			}
			return Optional.of(versionFields.get(0));
		}
	}
	
	/**
	 * Finds any field in a class with specified annotations. Finds this field recursively in the Hierarchy-tree.
	 *
	 * @return fields with annotation.
	 */
	public static List<Field> findAnnotatedFields(
		final Class<?> domainClass,
		final Collection<Class<? extends Annotation>> annotations)
	{
		final ArrayList<Field> foundFields = new ArrayList<>();
		final Collection<Field> classFields = AccessHelper.getInheritedPrivateFieldsByName(domainClass).values();
		for(final Field currentField : classFields)
		{
			for(final Class<? extends Annotation> annotation : annotations)
			{
				if(currentField.getAnnotationsByType(annotation).length > 0)
				{
					foundFields.add(currentField);
				}
			}
		}
		return foundFields;
	}
}
