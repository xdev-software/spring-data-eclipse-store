package software.xdev.spring.data.eclipse.store.integration.isolated.tests.simple.lazy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.simple.SimpleSingleTest;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {SimpleLazyTestConfiguration.class})
public class LazySimpleSingleTest extends SimpleSingleTest
{
	@Autowired
	public LazySimpleSingleTest(
		final CustomerLazyRepository repository,
		final CustomerAsRecordLazyRepository recordRepository,
		final CustomerNotCrudLazyRepository notCrudRepository,
		final SimpleLazyTestConfiguration configuration)
	{
		super(repository, recordRepository, notCrudRepository, configuration);
	}
}
