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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.keywords;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


public class MinimalDaoObject
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String value;
	
	public MinimalDaoObject(final String value)
	{
		this.value = value;
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public void setId(final int id)
	{
		this.id = id;
	}
	
	public String getValue()
	{
		return this.value;
	}
	
	public void setValue(final String value)
	{
		this.value = value;
	}
}
