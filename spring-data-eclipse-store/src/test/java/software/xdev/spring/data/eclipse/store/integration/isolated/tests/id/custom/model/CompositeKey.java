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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.custom.model;

import java.io.Serializable;
import java.util.Objects;


public class CompositeKey implements Serializable
{
	private final int idPart1;
	private final int idPart2;
	
	public CompositeKey(final int idPart1, final int idPart2)
	{
		this.idPart1 = idPart1;
		this.idPart2 = idPart2;
	}
	
	@Override
	public boolean equals(final Object o)
	{
		if(this == o)
		{
			return true;
		}
		if(o == null || this.getClass() != o.getClass())
		{
			return false;
		}
		final CompositeKey that = (CompositeKey)o;
		return this.idPart1 == that.idPart1 && this.idPart2 == that.idPart2;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.idPart1, this.idPart2);
	}
}
