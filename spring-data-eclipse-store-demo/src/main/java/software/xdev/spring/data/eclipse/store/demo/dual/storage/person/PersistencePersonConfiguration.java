package software.xdev.spring.data.eclipse.store.demo.dual.storage.person;

import static org.eclipse.store.storage.embedded.types.EmbeddedStorage.Foundation;

import java.nio.file.Path;

import org.eclipse.store.storage.embedded.types.EmbeddedStorageFoundation;
import org.eclipse.store.storage.types.Storage;
import org.springframework.context.annotation.Configuration;

import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;
import software.xdev.spring.data.eclipse.store.repository.config.EnableEclipseStoreRepositories;


@Configuration
@EnableEclipseStoreRepositories(
	value = "software.xdev.spring.data.eclipse.store.demo.dual.storage.person",
	clientConfigurationClass = PersistencePersonConfiguration.class
)
public class PersistencePersonConfiguration extends EclipseStoreClientConfiguration
{
	@Override
	public EmbeddedStorageFoundation<?> createEmbeddedStorageFoundation()
	{
		return Foundation(Storage.Configuration(Storage.FileProvider(Path.of("storage-person"))));
	}
}
