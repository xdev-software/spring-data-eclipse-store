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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;

import software.xdev.spring.data.eclipse.store.demo.complex.model.Person;


public class Owner extends Person
{
	private String address;
	
	private String city;
	
	private String telephone;
	
	private final List<Pet> pets = new ArrayList<>();
	
	public String getAddress()
	{
		return this.address;
	}
	
	public String getCity()
	{
		return this.city;
	}
	
	public String getTelephone()
	{
		return this.telephone;
	}
	
	public List<Pet> getPets()
	{
		return this.pets;
	}
	
	public void addPet(final Pet pet)
	{
		if(pet.isNew())
		{
			this.getPets().add(pet);
		}
	}
	
	public Optional<Pet> getPet(final String name)
	{
		return this.getPets().stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst();
	}
	
	@Override
	public String toString()
	{
		return new ToStringCreator(this)
			.append("id", this.getId())
			.append("new", this.isNew())
			.append("lastName", this.getLastName())
			.append("firstName", this.getFirstName())
			.append("address", this.getAddress())
			.append("city", this.getCity())
			.append("telephone", this.getTelephone())
			.toString();
	}
	
	public void addVisit(final String petName, final Visit visit)
	{
		Assert.notNull(petName, "Pet name must not be null!");
		Assert.notNull(visit, "Visit must not be null!");
		
		final Optional<Pet> pet = this.getPet(petName);
		
		Assert.isTrue(pet.isPresent(), "Invalid Pet name!");
		
		pet.get().addVisit(visit);
	}
}
