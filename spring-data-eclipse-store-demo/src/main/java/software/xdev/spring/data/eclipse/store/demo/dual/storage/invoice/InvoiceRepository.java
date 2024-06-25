package software.xdev.spring.data.eclipse.store.demo.dual.storage.invoice;

import org.springframework.data.repository.CrudRepository;


public interface InvoiceRepository extends CrudRepository<Invoice, Integer>
{
}
