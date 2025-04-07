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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.migration;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


public class User
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	private String name;
	
	private BigDecimal balance;
	
	public User(final String name, final BigDecimal balance)
	{
		this.name = name;
		this.balance = balance;
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public void setId(final int id)
	{
		this.id = id;
	}
	
	public BigDecimal getBalance()
	{
		return this.balance;
	}
	
	public void setBalance(final BigDecimal balance)
	{
		this.balance = balance;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setName(final String name)
	{
		this.name = name;
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
		final User user = (User)o;
		return this.id == user.id && Objects.equals(this.name, user.name) && Objects.equals(
			this.balance,
			user.balance);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.id, this.name, this.balance);
	}
}
