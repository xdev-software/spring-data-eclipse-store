package software.xdev.spring.data.eclipse.store.demo.dual.storage.invoice;

import static org.eclipse.store.storage.embedded.types.EmbeddedStorage.Foundation;

import java.nio.file.Path;

import org.eclipse.store.storage.embedded.types.EmbeddedStorageFoundation;
import org.eclipse.store.storage.types.Storage;
import org.springframework.context.annotation.Configuration;

import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;
import software.xdev.spring.data.eclipse.store.repository.config.EnableEclipseStoreRepositories;


@Configuration
@EnableEclipseStoreRepositories(
	value = "software.xdev.spring.data.eclipse.store.demo.dual.storage.invoice",
	clientConfiguration = "persistenceInvoiceConfiguration"
)
public class PersistenceInvoiceConfiguration extends EclipseStoreClientConfiguration
{
	@Override
	public EmbeddedStorageFoundation<?> getEmbeddedStorageFoundation()
	{
		return Foundation(Storage.Configuration(Storage.FileProvider(Path.of("storage-invoice"))));
	}
}
