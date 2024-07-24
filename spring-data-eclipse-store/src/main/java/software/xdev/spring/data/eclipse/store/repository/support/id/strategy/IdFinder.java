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
package software.xdev.spring.data.eclipse.store.repository.support.id.strategy;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Supplier;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import software.xdev.spring.data.eclipse.store.exceptions.IdGeneratorNotSupportedException;
import software.xdev.spring.data.eclipse.store.repository.support.id.strategy.auto.AutoIntegerIdFinder;
import software.xdev.spring.data.eclipse.store.repository.support.id.strategy.auto.AutoLongIdFinder;
import software.xdev.spring.data.eclipse.store.repository.support.id.strategy.auto.AutoStringIdFinder;


/**
 * A IdFinder <b>must be unique</b> in one storage for one entity-class. It creates Ids and therefore must know all
 * existing entities of one class.
 */
public interface IdFinder<ID>
{
	@SuppressWarnings({"java:S1452", "TypeParameterExplicitlyExtendsObject"})
	static <ID> IdFinder<ID> createIdFinder(
		final Field idField,
		final GeneratedValue generatedValueAnnotation,
		final Supplier<Object> lastIdGetter)
	{
		Objects.requireNonNull(lastIdGetter);
		if(generatedValueAnnotation.strategy() == GenerationType.AUTO)
		{
			if(Integer.class.isAssignableFrom(idField.getType()) || int.class.isAssignableFrom(idField.getType()))
			{
				return (IdFinder<ID>)new AutoIntegerIdFinder(lastIdGetter);
			}
			else if(idField.getType().equals(String.class))
			{
				return (IdFinder<ID>)new AutoStringIdFinder(lastIdGetter);
			}
			else if(Long.class.isAssignableFrom(idField.getType()) || long.class.isAssignableFrom(idField.getType()))
			{
				return (IdFinder<ID>)new AutoLongIdFinder(lastIdGetter);
			}
		}
		throw new IdGeneratorNotSupportedException(String.format(
			"Id generator with strategy %s for type %s is not supported.",
			generatedValueAnnotation.strategy(),
			idField.getType().getSimpleName()));
	}
	
	ID findId();
}
