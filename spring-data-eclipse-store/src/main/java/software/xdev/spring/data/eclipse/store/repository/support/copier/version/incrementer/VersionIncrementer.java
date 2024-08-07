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
package software.xdev.spring.data.eclipse.store.repository.support.copier.version.incrementer;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.UUID;

import software.xdev.spring.data.eclipse.store.exceptions.IdGeneratorNotSupportedException;


/**
 * Increments given version to a new version
 */
public interface VersionIncrementer<VERSION>
{
	/**
	 * Increments the original value and returns it.
	 */
	VERSION increment(final VERSION original);
	
	@SuppressWarnings({"java:S1452", "TypeParameterExplicitlyExtendsObject"})
	static VersionIncrementer<?> createVersionIncrementer(final Field versionField)
	{
		Objects.requireNonNull(versionField);
		if(Integer.class.isAssignableFrom(versionField.getType()) || int.class.isAssignableFrom(versionField.getType()))
		{
			return new IntegerVersionIncrementer();
		}
		else if(versionField.getType().equals(String.class))
		{
			return new StringVersionIncrementer();
		}
		else if(versionField.getType().equals(UUID.class))
		{
			return new UUIDVersionIncrementer();
		}
		else if(Long.class.isAssignableFrom(versionField.getType())
			|| long.class.isAssignableFrom(versionField.getType()))
		{
			return new LongVersionIncrementer();
		}
		throw new IdGeneratorNotSupportedException(
			"@Version with type %s is not supported.".formatted(versionField.getType().getSimpleName())
		);
	}
}
