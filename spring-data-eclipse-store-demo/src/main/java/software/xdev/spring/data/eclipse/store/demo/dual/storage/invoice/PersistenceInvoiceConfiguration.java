package software.xdev.spring.data.eclipse.store.demo.dual.storage.invoice;

import org.springframework.context.annotation.Configuration;

import software.xdev.spring.data.eclipse.store.repository.config.EnableEclipseStoreRepositories;


@Configuration
@EnableEclipseStoreRepositories("software.xdev.spring.data.eclipse.store.demo.dual.storage.invoice")
public class PersistenceInvoiceConfiguration
{
}
