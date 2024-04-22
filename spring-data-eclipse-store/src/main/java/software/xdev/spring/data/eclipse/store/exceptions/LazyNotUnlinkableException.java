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
package software.xdev.spring.data.eclipse.store.exceptions;

/**
 * Is used when a {@link software.xdev.spring.data.eclipse.store.repository.lazy.SpringDataEclipseStoreLazy} is not able
 * to get unlinked from the object tree.
 * <p>
 * This exception should not be created by the user, but only within the Spring-Data-Eclipse-Store-Library.
 * </p>
 */
public class LazyNotUnlinkableException extends RuntimeException
{
	public LazyNotUnlinkableException(final String message, final Throwable e)
	{
		super(message, e);
	}
}
