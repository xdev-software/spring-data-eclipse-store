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
package software.xdev.spring.data.eclipse.store.integration.isolatet.tests.special.types;

import java.util.Objects;

import jakarta.persistence.Id;


public class ComplexObject<T>
{
	@Id
	private final Integer id;
	
	private T value;
	
	public ComplexObject(final Integer id, final T value)
	{
		this.id = id;
		this.value = value;
	}
	
	public T getValue()
	{
		return this.value;
	}
	
	public void setValue(final T value)
	{
		this.value = value;
	}
	
	public Integer getId()
	{
		return this.id;
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
		final ComplexObject<?> that = (ComplexObject<?>)o;
		return Objects.equals(this.id, that.id) && Objects.equals(this.value, that.value);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.id, this.value);
	}
}
