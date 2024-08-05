package software.xdev.spring.data.eclipse.store.integration.isolated.tests.query.hsql;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


public class OtherEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String description;
	
	public OtherEntity(final Long id, final String description)
	{
		this.id = id;
		this.description = description;
	}
	
	public OtherEntity()
	{
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public void setDescription(final String description)
	{
		this.description = description;
	}
	
	public Long getId()
	{
		return this.id;
	}
}

