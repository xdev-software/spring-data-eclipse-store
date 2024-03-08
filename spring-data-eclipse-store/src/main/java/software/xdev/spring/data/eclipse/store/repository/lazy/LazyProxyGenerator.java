package software.xdev.spring.data.eclipse.store.repository.lazy;

import org.eclipse.serializer.reference.Lazy;
import org.springframework.aop.framework.ProxyFactory;


public final class LazyProxyGenerator
{
	private LazyProxyGenerator()
	{
	}
	
	public static <L extends Lazy<T>, T> L generateLazyProxy(final L lazy, final Class<L> clazz)
	{
		final ProxyFactory f = new ProxyFactory(clazz, new LazyInterceptor<>(lazy));
		return (L)f.getProxy();
	}
	
	public static <T> Lazy<T> generateLazyProxy(final Lazy<T> lazy)
	{
		return generateLazyProxy(lazy, Lazy.class);
	}
	
	public static <T> Lazy.Default<T> generateLazyDefaultProxy(final Lazy.Default<T> lazy)
	{
		return generateLazyProxy(lazy, Lazy.Default.class);
	}
}
