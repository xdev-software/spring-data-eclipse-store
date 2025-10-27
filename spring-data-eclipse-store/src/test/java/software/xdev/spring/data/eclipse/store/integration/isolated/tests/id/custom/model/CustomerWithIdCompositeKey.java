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

import java.util.Objects;

import jakarta.persistence.Id;


public class CustomerWithIdCompositeKey
{
	@Id
	private CompositeKey id;
	
	private final String firstName;
	
	public CustomerWithIdCompositeKey(final CompositeKey id, final String firstName)
	{
		this.id = id;
		this.firstName = firstName;
	}
	
	public String getFirstName()
	{
		return this.firstName;
	}
	
	@Override
	public String toString()
	{
		return String.format(
			"Customer[firstName='%s']",
			this.firstName);
	}
	
	public CompositeKey getId()
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
		final CustomerWithIdCompositeKey customer = (CustomerWithIdCompositeKey)o;
		return Objects.equals(this.firstName, customer.firstName);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.firstName);
	}
}
