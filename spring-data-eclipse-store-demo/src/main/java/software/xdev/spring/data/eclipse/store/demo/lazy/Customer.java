package software.xdev.spring.data.eclipse.store.demo.lazy;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import org.springframework.data.annotation.Id;


public class Customer
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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
