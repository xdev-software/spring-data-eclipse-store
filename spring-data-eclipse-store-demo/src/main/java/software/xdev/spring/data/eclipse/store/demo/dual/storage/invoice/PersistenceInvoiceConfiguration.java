package software.xdev.spring.data.eclipse.store.demo.dual.storage.invoice;

import java.nio.file.Path;

import org.eclipse.serializer.reflect.ClassLoaderProvider;
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
@EnableEclipseStoreRepositories
public class PersistenceInvoiceConfiguration extends EclipseStoreClientConfiguration
{
	public static final String STORAGE_PATH = "storage-invoice";
	
	@Autowired
	protected PersistenceInvoiceConfiguration(
		final EclipseStoreProperties defaultEclipseStoreProperties,
		final EmbeddedStorageFoundationFactory defaultEclipseStoreProvider,
		final ClassLoaderProvider classLoaderProvider)
	{
		super(defaultEclipseStoreProperties, defaultEclipseStoreProvider, classLoaderProvider);
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
		final EmbeddedStorageFoundation<?> storageFoundation =
			EmbeddedStorage.Foundation(Storage.Configuration(Storage.FileProvider(Path.of(STORAGE_PATH))));
		// This is only needed, if a different ClassLoader is used (e.g. when using spring-dev-tools)
		storageFoundation.getConnectionFoundation().setClassLoaderProvider(getClassLoaderProvider());
		return storageFoundation;
	}
}
