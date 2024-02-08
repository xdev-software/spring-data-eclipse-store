package software.xdev.spring.data.eclipse.store.jpa.integration.repository;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;


@Entity
public class PersonToTestInJpa
{
	@Id
	private String id;
	
	private String firstName;
	private String lastName;
	
	public PersonToTestInJpa(final String firstName, final String lastName)
	{
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public PersonToTestInJpa()
	{
	
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
		final PersonToTestInJpa customer = (PersonToTestInJpa)o;
		return Objects.equals(this.id, customer.id) && Objects.equals(this.firstName, customer.firstName)
			&& Objects.equals(this.lastName, customer.lastName);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.id, this.firstName, this.lastName);
	}
}
