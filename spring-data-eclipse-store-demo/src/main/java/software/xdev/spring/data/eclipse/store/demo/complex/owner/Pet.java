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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.core.style.ToStringCreator;

import software.xdev.spring.data.eclipse.store.demo.complex.model.NamedEntity;


public class Pet extends NamedEntity
{
	private LocalDate birthDate;
	
	private PetType type;
	
	private final List<Visit> visits = new ArrayList<>();
	
	public void setBirthDate(final LocalDate birthDate)
	{
		this.birthDate = birthDate;
	}
	
	public LocalDate getBirthDate()
	{
		return this.birthDate;
	}
	
	public PetType getType()
	{
		return this.type;
	}
	
	public void setType(final PetType type)
	{
		this.type = type;
	}
	
	public Collection<Visit> getVisits()
	{
		return this.visits;
	}
	
	public void addVisit(final Visit visit)
	{
		this.getVisits().add(visit);
	}
	
	@Override
	public String toString()
	{
		return new ToStringCreator(this)
			.append("id", this.getId())
			.append("new", this.isNew())
			.append("name", this.getName())
			.append("birthday", this.getBirthDate())
			.append("type", this.getType())
			.toString();
	}
}
