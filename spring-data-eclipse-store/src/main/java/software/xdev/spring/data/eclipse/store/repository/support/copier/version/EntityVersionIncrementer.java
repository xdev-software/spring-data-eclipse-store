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
package software.xdev.spring.data.eclipse.store.repository.support.copier.version;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;

import software.xdev.spring.data.eclipse.store.repository.support.AnnotatedFieldFinder;
import software.xdev.spring.data.eclipse.store.repository.support.copier.version.incrementer.VersionIncrementer;


/**
 * Increments the version of a given entity. One VersionSetter is created for every entity type.
 */
public interface EntityVersionIncrementer<T>
{
	/**
	 * Creates a new version setter for one specific entity type. If the entity type has no version, a
	 * {@link NotIncrementingEntityVersionIncrementer} is created, which does nothing.
	 */
	static <T> EntityVersionIncrementer<T> createVersionSetter(final Class<T> classWithId)
	{
		Objects.requireNonNull(classWithId);
		final Optional<Field> versionField = AnnotatedFieldFinder.findVersionField(classWithId);
		if(versionField.isEmpty())
		{
			return new NotIncrementingEntityVersionIncrementer<>();
		}
		return new SimpleEntityVersionIncrementer<>(
			versionField.get(),
			VersionIncrementer.createVersionIncrementer(versionField.get()));
	}
	
	/**
	 * This method makes sure, that a version is set for the given object. If it is already set (not null), then the
	 * version is incremented. If it is not set, a new one will be generated and set.
	 */
	void incrementVersion(T objectToSetVersionIn);
}
