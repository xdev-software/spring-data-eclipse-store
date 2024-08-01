package software.xdev.spring.data.eclipse.store.integration.isolated.tests.id.custom.model;

import java.util.Objects;


public class CompositeKey
{
	private final int idPart1;
	private final int idPart2;
	
	public CompositeKey(final int idPart1, final int idPart2)
	{
		this.idPart1 = idPart1;
		this.idPart2 = idPart2;
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
		final CompositeKey that = (CompositeKey)o;
		return this.idPart1 == that.idPart1 && this.idPart2 == that.idPart2;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(this.idPart1, this.idPart2);
	}
}
