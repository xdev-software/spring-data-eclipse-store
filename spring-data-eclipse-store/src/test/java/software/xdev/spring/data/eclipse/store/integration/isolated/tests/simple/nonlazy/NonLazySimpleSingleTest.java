package software.xdev.spring.data.eclipse.store.integration.isolated.tests.simple.nonlazy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.simple.SimpleSingleTest;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {SimpleNonLazyTestConfiguration.class})
public class NonLazySimpleSingleTest extends SimpleSingleTest
{
	@Autowired
	public NonLazySimpleSingleTest(
		final CustomerNonLazyRepository repository,
		final CustomerAsRecordNonLazyRepository recordRepository,
		final CustomerNotCrudNonLazyRepository notCrudRepository,
		final SimpleNonLazyTestConfiguration configuration)
	{
		super(repository, recordRepository, notCrudRepository, configuration);
	}
}
