package software.xdev.spring.data.eclipse.store.integration.isolated.tests.query.hsql;

import java.time.LocalDate;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


public class MyEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String name;
	
	private int age;
	
	private LocalDate creationDate;
	
	private boolean active;
	
	private OtherEntity otherEntity;
	
	public MyEntity()
	{
	}
	
	public MyEntity(
		final Long id,
		final String name,
		final int age,
		final LocalDate creationDate,
		final boolean active,
		final OtherEntity otherEntity)
	{
		this.id = id;
		this.name = name;
		this.age = age;
		this.creationDate = creationDate;
		this.active = active;
		this.otherEntity = otherEntity;
	}
	
	public Long getId()
	{
		return this.id;
	}
	
	public void setId(final Long id)
	{
		this.id = id;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setName(final String name)
	{
		this.name = name;
	}
	
	public int getAge()
	{
		return this.age;
	}
	
	public void setAge(final int age)
	{
		this.age = age;
	}
	
	public LocalDate getCreationDate()
	{
		return this.creationDate;
	}
	
	public void setCreationDate(final LocalDate creationDate)
	{
		this.creationDate = creationDate;
	}
	
	public boolean isActive()
	{
		return this.active;
	}
	
	public void setActive(final boolean active)
	{
		this.active = active;
	}
	
	public OtherEntity getOtherEntity()
	{
		return this.otherEntity;
	}
	
	public void setOtherEntity(final OtherEntity otherEntity)
	{
		this.otherEntity = otherEntity;
	}
}
