package software.xdev.spring.data.eclipse.store.repository.lazy;

import org.eclipse.serializer.reference.Lazy;
import org.springframework.aop.framework.ProxyFactory;


public final class LazyProxyGenerator
{
	private LazyProxyGenerator()
	{
	}
	
	public static <T> Lazy<T> generateLazyProxy(final Lazy<T> lazy)
	{
		final ProxyFactory f = new ProxyFactory(Lazy.class, new LazyInterceptor<>(lazy));
		return (Lazy<T>)f.getProxy();
	}
}
