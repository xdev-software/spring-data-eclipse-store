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
package software.xdev.spring.data.eclipse.store.integration.shared.repositories.id;

import java.util.Objects;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


public class Purchase
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	private final String productName;
	
	public Purchase(final String productName)
	{
		this.productName = productName;
	}
	
	public String getProductName()
	{
		return this.productName;
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
		final Purchase purchase = (Purchase)o;
		return Objects.equals(this.id, purchase.id) && Objects.equals(this.productName, purchase.productName);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.id, this.productName);
	}
}
