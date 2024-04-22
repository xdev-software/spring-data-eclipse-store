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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.special.types;

import java.util.Objects;

import org.eclipse.serializer.reference.Lazy;


public class LazyDaoObject extends ComplexObject<Lazy<String>>
{
	public LazyDaoObject(final Integer id, final Lazy<String> value)
	{
		super(id, value);
	}
	
	@Override
	public int hashCode()
	{
		return super.hashCode();
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
		final ComplexObject<Lazy<String>> that = (ComplexObject<Lazy<String>>)o;
		return Objects.equals(this.getId(), that.getId())
			&& (this.getValue() == null && that.getValue() == null)
			|| Objects.equals(
			this.getValue().get(),
			that.getValue().get());
	}
}
