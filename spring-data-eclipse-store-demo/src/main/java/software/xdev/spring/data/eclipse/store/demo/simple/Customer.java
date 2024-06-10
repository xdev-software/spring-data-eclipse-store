package software.xdev.spring.data.eclipse.store.demo.simple;

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
	public String toString()
	{
		return String.format(
			"Customer[id=%s, firstName='%s', lastName='%s']",
			this.id, this.firstName, this.lastName);
	}
}
