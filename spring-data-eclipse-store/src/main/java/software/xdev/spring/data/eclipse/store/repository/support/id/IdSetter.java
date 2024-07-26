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
package software.xdev.spring.data.eclipse.store.repository.support.id;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import jakarta.persistence.GeneratedValue;

import software.xdev.spring.data.eclipse.store.repository.support.AnnotatedFieldFinder;
import software.xdev.spring.data.eclipse.store.repository.support.id.strategy.IdFinder;


/**
 * A IdSetter <b>must be unique</b> in one storage for one entity-class. It creates Ids and therefore must know all
 * existing entities of one class.
 */
public interface IdSetter<T>
{
	static <T> IdSetter<T> createIdSetter(
		final Class<T> classWithId,
		final Consumer<Object> lastIdPersister,
		final Supplier<Object> lastIdGetter)
	{
		Objects.requireNonNull(classWithId);
		Objects.requireNonNull(lastIdPersister);
		Objects.requireNonNull(lastIdGetter);
		final Optional<Field> idField = AnnotatedFieldFinder.findIdField(classWithId);
		if(idField.isEmpty())
		{
			return new NotSettingIdSetter<>();
		}
		final GeneratedValue generatedValueAnnotation = idField.get().getAnnotation(GeneratedValue.class);
		if(generatedValueAnnotation == null)
		{
			return new NotSettingIdSetter<>();
		}
		return new SimpleIdSetter<>(
			idField.get(),
			IdFinder.createIdFinder(idField.get(), generatedValueAnnotation, lastIdGetter),
			lastIdPersister);
	}
	
	/**
	 * This method makes sure, that an id is set for the given object. If it is already set (not null), then nothing is
	 * done. If it is not set, a new one will be generated and set.
	 */
	void ensureId(T objectToSetIdIn);
	
	boolean isAutomaticSetter();
	
	Object getDefaultValue();
}
