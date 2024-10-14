package software.xdev.spring.data.eclipse.store.demo.complex;

import java.nio.file.Path;

import org.eclipse.serializer.reflect.ClassLoaderProvider;
import org.eclipse.store.integrations.spring.boot.types.configuration.EclipseStoreProperties;
import org.eclipse.store.integrations.spring.boot.types.factories.EmbeddedStorageFoundationFactory;
import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageFoundation;
import org.eclipse.store.storage.types.Storage;
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
	
	public static final String STORAGE_PATH = "storage-complex";
	
	private final ClassLoaderProvider classLoaderProvider;
	
	@Autowired
	public ComplexConfiguration(
		final EclipseStoreProperties defaultEclipseStoreProperties,
		final EmbeddedStorageFoundationFactory defaultEclipseStoreProvider,
		final ClassLoaderProvider classLoaderProvider
	)
	{
		super(defaultEclipseStoreProperties, defaultEclipseStoreProvider);
		this.classLoaderProvider = classLoaderProvider;
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
		storageFoundation.getConnectionFoundation().setClassLoaderProvider(this.classLoaderProvider);
		return storageFoundation;
	}
	
	/**
	 * Overriding {@link #transactionManager(ObjectProvider)} only to add the {@link Bean}-Annotation.
	 */
	@Bean
	@Override
	public PlatformTransactionManager transactionManager(
		final ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers
	)
	{
		return super.transactionManager(transactionManagerCustomizers);
	}
}
