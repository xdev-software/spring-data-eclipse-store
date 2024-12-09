package software.xdev.spring.data.eclipse.store.integration.isolated.tests.simple.lazy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.simple.SimpleMultipleTest;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {SimpleLazyTestConfiguration.class})
public class LazySimpleMultipleTest extends SimpleMultipleTest
{
	@Autowired
	public LazySimpleMultipleTest(
		final CustomerLazyRepository customerRepository,
		final OwnerLazyRepository ownerRepository,
		final SimpleLazyTestConfiguration configuration)
	{
		super(customerRepository, ownerRepository, configuration);
	}
}
