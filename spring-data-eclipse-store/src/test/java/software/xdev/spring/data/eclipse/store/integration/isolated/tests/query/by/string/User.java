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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.query.by.string;

import java.time.LocalDate;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


public class User
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String firstName;
	private String lastName;
	private Integer age;
	private String email;
	private String city;
	private LocalDate dateOfBirth;
	private Boolean isActive;
	
	// Constructors, Getters, and Setters
	
	public User()
	{
	}
	
	public User(
		final String firstName,
		final String lastName,
		final Integer age,
		final String email,
		final String city,
		final LocalDate dateOfBirth,
		final Boolean isActive)
	{
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.email = email;
		this.city = city;
		this.dateOfBirth = dateOfBirth;
		this.isActive = isActive;
	}
	
	public Long getId()
	{
		return this.id;
	}
	
	public void setId(final Long id)
	{
		this.id = id;
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
	
	public Integer getAge()
	{
		return this.age;
	}
	
	public void setAge(final Integer age)
	{
		this.age = age;
	}
	
	public String getEmail()
	{
		return this.email;
	}
	
	public void setEmail(final String email)
	{
		this.email = email;
	}
	
	public String getCity()
	{
		return this.city;
	}
	
	public void setCity(final String city)
	{
		this.city = city;
	}
	
	public LocalDate getDateOfBirth()
	{
		return this.dateOfBirth;
	}
	
	public void setDateOfBirth(final LocalDate dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
	}
	
	public Boolean getActive()
	{
		return this.isActive;
	}
	
	public void setActive(final Boolean active)
	{
		this.isActive = active;
	}
}
