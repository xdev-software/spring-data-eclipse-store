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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.query.hsql;

import java.util.Date;
import java.util.Objects;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


public class MyEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String name;
	
	private Integer age;
	
	private Date creationDate;
	
	private Boolean active;
	
	private OtherEntity otherEntity;
	
	public MyEntity()
	{
	}
	
	public MyEntity(
		final Long id,
		final String name,
		final int age,
		final Date creationDate,
		final boolean active,
		final OtherEntity otherEntity)
	{
		this.id = id;
		this.name = name;
		this.age = age;
		this.creationDate = creationDate;
		this.active = active;
		this.otherEntity = otherEntity;
	}
	
	public Long getId()
	{
		return this.id;
	}
	
	public void setId(final Long id)
	{
		this.id = id;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setName(final String name)
	{
		this.name = name;
	}
	
	public int getAge()
	{
		return this.age;
	}
	
	public void setAge(final int age)
	{
		this.age = age;
	}
	
	public Date getCreationDate()
	{
		return this.creationDate;
	}
	
	public void setCreationDate(final Date creationDate)
	{
		this.creationDate = creationDate;
	}
	
	public boolean isActive()
	{
		return this.active;
	}
	
	public void setActive(final boolean active)
	{
		this.active = active;
	}
	
	public OtherEntity getOtherEntity()
	{
		return this.otherEntity;
	}
	
	public void setOtherEntity(final OtherEntity otherEntity)
	{
		this.otherEntity = otherEntity;
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
		final MyEntity myEntity = (MyEntity)o;
		return Objects.equals(this.id, myEntity.id) && Objects.equals(this.name, myEntity.name)
			&& Objects.equals(this.age, myEntity.age) && Objects.equals(this.creationDate, myEntity.creationDate)
			&& Objects.equals(this.active, myEntity.active) && Objects.equals(
			this.otherEntity,
			myEntity.otherEntity);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.id, this.name, this.age, this.creationDate, this.active, this.otherEntity);
	}
}
