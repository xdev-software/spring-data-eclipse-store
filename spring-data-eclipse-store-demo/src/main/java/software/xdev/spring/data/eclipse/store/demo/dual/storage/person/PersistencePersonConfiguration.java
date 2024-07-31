package software.xdev.spring.data.eclipse.store.demo.dual.storage.person;

import org.eclipse.store.integrations.spring.boot.types.configuration.ConfigurationPair;
import org.eclipse.store.integrations.spring.boot.types.configuration.EclipseStoreProperties;
import org.eclipse.store.integrations.spring.boot.types.factories.EmbeddedStorageFoundationFactory;
import org.eclipse.store.storage.embedded.configuration.types.EmbeddedStorageConfigurationPropertyNames;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageFoundation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import software.xdev.spring.data.eclipse.store.demo.dual.storage.invoice.PersistenceInvoiceConfiguration;
import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;
import software.xdev.spring.data.eclipse.store.repository.config.EnableEclipseStoreRepositories;


/**
 * To set this configuration for the package we use the
 * {@link EnableEclipseStoreRepositories#clientConfigurationClass()}. Another example:
 * {@link software.xdev.spring.data.eclipse.store.demo.dual.storage.invoice.PersistenceInvoiceConfiguration}
 */
@Configuration
@EnableEclipseStoreRepositories
public class PersistencePersonConfiguration extends EclipseStoreClientConfiguration
{
	public static final String STORAGE_PATH = "storage-person";
	
	private final EmbeddedStorageFoundationFactory foundation;
	private final EclipseStoreProperties properties;
	
	@Autowired
	public PersistencePersonConfiguration(
		final EclipseStoreProperties defaultEclipseStoreProperties,
		final EmbeddedStorageFoundationFactory defaultEclipseStoreProvider,
		final EclipseStoreProperties properties)
	{
		super(defaultEclipseStoreProperties, defaultEclipseStoreProvider);
		this.foundation = defaultEclipseStoreProvider;
		this.properties = properties;
	}
	
	/**
	 * This is one option how to configure the {@link EmbeddedStorageFoundation}.
	 * <p>
	 * We use the default {@link EclipseStoreProperties} and add our own property
	 * {@link EmbeddedStorageConfigurationPropertyNames#STORAGE_DIRECTORY}. Every other property is used like default.
	 * </p>
	 * Another example: {@link PersistenceInvoiceConfiguration#createEmbeddedStorageFoundation()}
	 */
	@Override
	public EmbeddedStorageFoundation<?> createEmbeddedStorageFoundation()
	{
		final ConfigurationPair additionalProperties = new ConfigurationPair(
			EmbeddedStorageConfigurationPropertyNames.STORAGE_DIRECTORY,
			STORAGE_PATH);
		return this.foundation.createStorageFoundation(this.properties, additionalProperties);
	}
}
