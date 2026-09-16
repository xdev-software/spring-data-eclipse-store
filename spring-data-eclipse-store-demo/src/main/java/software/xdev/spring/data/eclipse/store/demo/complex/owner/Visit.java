/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package software.xdev.spring.data.eclipse.store.demo.complex.owner;

import java.time.LocalDate;

import org.springframework.core.style.ToStringCreator;

import software.xdev.spring.data.eclipse.store.demo.complex.model.BaseEntity;


public class Visit extends BaseEntity
{
	private LocalDate date;
	
	private String description;
	
	public Visit()
	{
		this.date = LocalDate.now();
	}
	
	public LocalDate getDate()
	{
		return this.date;
	}
	
	public void setDate(final LocalDate date)
	{
		this.date = date;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public void setDescription(final String description)
	{
		this.description = description;
	}
	
	@Override
	public String toString()
	{
		return new ToStringCreator(this)
			.append("id", this.getId())
			.append("new", this.isNew())
			.append("date", this.getDate())
			.append("description", this.getDescription())
			.toString();
	}
}
