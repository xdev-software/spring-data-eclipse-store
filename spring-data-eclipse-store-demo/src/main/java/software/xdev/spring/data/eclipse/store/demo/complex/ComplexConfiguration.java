package software.xdev.spring.data.eclipse.store.demo.complex;

import org.eclipse.store.integrations.spring.boot.types.configuration.EclipseStoreProperties;
import org.eclipse.store.integrations.spring.boot.types.factories.EmbeddedStorageFoundationFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;
import software.xdev.spring.data.eclipse.store.repository.config.EnableEclipseStoreRepositories;


@Configuration
@EnableEclipseStoreRepositories
public class ComplexConfiguration extends EclipseStoreClientConfiguration
{
	@Autowired
	public ComplexConfiguration(
		final EclipseStoreProperties defaultEclipseStoreProperties,
		final EmbeddedStorageFoundationFactory defaultEclipseStoreProvider
	)
	{
		super(defaultEclipseStoreProperties, defaultEclipseStoreProvider);
	}
	
	/**
	 * Overriding {@link #transactionManager(ObjectProvider)} only to add the {@link Bean}-Annotation.
	 */
	@Bean
	@Override
	public PlatformTransactionManager transactionManager(final ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers)
	{
		return super.transactionManager(transactionManagerCustomizers);
	}
}
