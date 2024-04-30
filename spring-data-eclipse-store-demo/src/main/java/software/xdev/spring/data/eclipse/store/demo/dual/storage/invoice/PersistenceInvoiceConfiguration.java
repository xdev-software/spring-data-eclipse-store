package software.xdev.spring.data.eclipse.store.demo.dual.storage.invoice;

import java.nio.file.Path;

import org.eclipse.store.integrations.spring.boot.types.configuration.EclipseStoreProperties;
import org.eclipse.store.integrations.spring.boot.types.factories.EmbeddedStorageFoundationFactory;
import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageFoundation;
import org.eclipse.store.storage.types.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;
import software.xdev.spring.data.eclipse.store.repository.config.EnableEclipseStoreRepositories;


/**
 * To set this configuration for the package we use the {@link EnableEclipseStoreRepositories#clientConfiguration()}
 * ()}. Another example:
 * {@link software.xdev.spring.data.eclipse.store.demo.dual.storage.person.PersistencePersonConfiguration}
 */
@Configuration
@EnableEclipseStoreRepositories(
	value = "software.xdev.spring.data.eclipse.store.demo.dual.storage.invoice",
	clientConfiguration = "persistenceInvoiceConfiguration"
)
public class PersistenceInvoiceConfiguration extends EclipseStoreClientConfiguration
{
	
	@Autowired
	protected PersistenceInvoiceConfiguration(
		final EclipseStoreProperties defaultEclipseStoreProperties,
		final EmbeddedStorageFoundationFactory defaultEclipseStoreProvider)
	{
		super(defaultEclipseStoreProperties, defaultEclipseStoreProvider);
	}
	
	/**
	 * This is one option how to configure the {@link EmbeddedStorageFoundation}.
	 * <p>
	 * We create a completely new foundation. That means that all configuration (e.g. properties) are not used here.
	 * With this method you have complete control over the configuration.
	 * </p>
	 * Another example: {@link PersistencePersonConfiguration#createEmbeddedStorageFoundation()}
	 */
	@Override
	public EmbeddedStorageFoundation<?> createEmbeddedStorageFoundation()
	{
		return EmbeddedStorage.Foundation(Storage.Configuration(Storage.FileProvider(Path.of("storage-invoice"))));
	}
}
