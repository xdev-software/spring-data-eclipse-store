package software.xdev.spring.data.eclipse.store.integration.isolated.tests.simple.nonlazy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.simple.SimpleMultipleTest;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {SimpleNonLazyTestConfiguration.class})
public class NonLazySimpleMultipleTest extends SimpleMultipleTest
{
	@Autowired
	public NonLazySimpleMultipleTest(
		final CustomerNonLazyRepository customerRepository,
		final OwnerNonLazyRepository ownerRepository,
		final SimpleNonLazyTestConfiguration configuration)
	{
		super(customerRepository, ownerRepository, configuration);
	}
}
