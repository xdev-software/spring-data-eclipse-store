package software.xdev.spring.data.eclipse.store.jpa.repository;

import java.util.Objects;

import org.springframework.data.annotation.Id;


public class Customer
{
	@Id
	private String id;
	
	private final String firstName;
	private final String lastName;
	
	public Customer(final String firstName, final String lastName)
	{
		this.firstName = firstName;
		this.lastName = lastName;
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
		final Customer customer = (Customer)o;
		return Objects.equals(this.id, customer.id) && Objects.equals(this.firstName, customer.firstName)
			&& Objects.equals(this.lastName, customer.lastName);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.id, this.firstName, this.lastName);
	}
}
