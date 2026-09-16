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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.lazy;

import java.util.Objects;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


public class SimpleEntityWithId
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String name;
	
	public SimpleEntityWithId(final String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setName(final String name)
	{
		this.name = name;
	}
	
	public long getId()
	{
		return this.id;
	}
	
	public void setId(final long id)
	{
		this.id = id;
	}
	
	@Override
	public boolean equals(final Object o)
	{
		if(o == null || this.getClass() != o.getClass())
		{
			return false;
		}
		final SimpleEntityWithId that = (SimpleEntityWithId)o;
		return this.id == that.id && Objects.equals(this.name, that.name);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.id, this.name);
	}
}
