package software.xdev.spring.data.eclipse.store.repository.lazy;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.ConstructorInvocation;
import org.eclipse.serializer.reference.Lazy;


public class LazyInterceptor<T> implements ConstructorInterceptor
{
	private final Lazy<T> originalLazy;
	
	public LazyInterceptor(final Lazy<T> originalLazy)
	{
		this.originalLazy = originalLazy;
	}
	
	@Override
	public Lazy<T> construct(final ConstructorInvocation invocation)
	{
		return Lazy.Reference(this.originalLazy.get());
	}
}
