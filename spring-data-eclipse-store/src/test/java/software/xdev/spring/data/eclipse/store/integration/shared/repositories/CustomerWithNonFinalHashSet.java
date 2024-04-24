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
package software.xdev.spring.data.eclipse.store.integration.shared.repositories;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public class CustomerWithNonFinalHashSet
{
	private String firstName;
	private String lastName;
	
	private Set<String> values = new HashSet<>();
	
	public CustomerWithNonFinalHashSet(final String firstName, final String lastName)
	{
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public void setValues(final Set<String> values)
	{
		this.values = values;
	}
	
	public String getFirstName()
	{
		return this.firstName;
	}
	
	public void setFirstName(final String firstName)
	{
		this.firstName = firstName;
	}
	
	public String getLastName()
	{
		return this.lastName;
	}
	
	public void setLastName(final String lastName)
	{
		this.lastName = lastName;
	}
	
	public Set<String> getValues()
	{
		return this.values;
	}
	
	@Override
	public String toString()
	{
		return String.format(
			"Customer[firstName='%s', lastName='%s']",
			this.firstName, this.lastName);
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
		final CustomerWithNonFinalHashSet customer = (CustomerWithNonFinalHashSet)o;
		return Objects.equals(this.firstName, customer.firstName) && Objects.equals(
			this.lastName,
			customer.lastName);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.firstName, this.lastName);
	}
	
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public static CustomerWithNonFinalHashSet getCustomerWithFirstName(
		final List<CustomerWithNonFinalHashSet> customers,
		final String firstName)
	{
		return customers.stream().filter(customer -> customer.getFirstName().equals(firstName)).findFirst().get();
	}
}
