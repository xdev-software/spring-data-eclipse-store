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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.query.by.example;

import java.math.BigDecimal;

import jakarta.persistence.Id;


public class User
{
	@Id
	private int id;
	
	private String name;
	
	private BigDecimal balance;
	
	public User(final int id, final String name, final BigDecimal balance)
	{
		this.id = id;
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
}
