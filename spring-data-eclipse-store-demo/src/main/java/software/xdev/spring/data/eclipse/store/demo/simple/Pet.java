package software.xdev.spring.data.eclipse.store.demo.simple;

import org.springframework.data.annotation.Id;


public class Pet
{
	@Id
	private String id;
	
	private String name;
	private Integer age;
	
	public Pet()
	{
	}
	
	public Pet(final String id, final String name, final Integer age)
	{
		this.id = id;
		this.name = name;
		this.age = age;
	}
	
	@Override
	public String toString()
	{
		return String.format(
			"Pet[id=%s, name='%s', age='%s']",
			this.id, this.name, this.age);
	}
}
