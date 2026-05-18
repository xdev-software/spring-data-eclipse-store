package software.xdev.spring.data.eclipse.store.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;


@Entity
public class CustomerInJpa
{
	@Id
	private String id;
	
	private String firstName;
	private String lastName;
	
	public CustomerInJpa(final String id, final String firstName, final String lastName)
	{
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public CustomerInJpa()
	{
	
	}
	
	@Override
	public String toString()
	{
		return String.format(
			"Customer[id=%s, firstName='%s', lastName='%s']",
			this.id, this.firstName, this.lastName);
	}
}
