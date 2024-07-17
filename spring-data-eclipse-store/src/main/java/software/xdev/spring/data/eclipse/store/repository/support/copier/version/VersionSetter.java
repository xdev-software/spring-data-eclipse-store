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
 * A IdSetter <b>must be unique</b> in one storage for one entity-class. It creates Ids and therefore must know all
 * existing entities of one class.
 */
public interface VersionSetter<T>
{
	static <T> VersionSetter<T> createVersionSetter(final Class<T> classWithId)
	{
		Objects.requireNonNull(classWithId);
		final Optional<Field> versionField = AnnotatedFieldFinder.findVersionField(classWithId);
		if(versionField.isEmpty())
		{
			return new NotSettingVersionSetter<>();
		}
		return new SimpleVersionSetter<>(
			versionField.get(),
			VersionIncrementer.createVersionIncrementer(versionField.get()));
	}
	
	/**
	 * This method makes sure, that an version is set for the given object. If it is already set (not null), then the
	 * version is incremented. If it is not set, a new one will be generated and set.
	 */
	void incrementVersion(T objectToSetVersionIn);
}
