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
package software.xdev.spring.data.eclipse.store.exceptions;

public class MergeFailedException extends RuntimeException
{
	public MergeFailedException(final Object sourceObject, final Object targetObject, final Exception e)
	{
		super(
			String.format(
				"Could not merge values of object %s with object %s",
				sourceObject.toString(),
				targetObject.toString()
			),
			e
		);
	}
}
