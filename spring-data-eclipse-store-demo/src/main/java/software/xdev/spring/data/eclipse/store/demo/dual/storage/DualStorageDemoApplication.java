package software.xdev.spring.data.eclipse.store.demo.dual.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import software.xdev.spring.data.eclipse.store.demo.dual.storage.invoice.Invoice;
import software.xdev.spring.data.eclipse.store.demo.dual.storage.invoice.InvoiceRepository;
import software.xdev.spring.data.eclipse.store.demo.dual.storage.person.Person;
import software.xdev.spring.data.eclipse.store.demo.dual.storage.person.PersonRepository;


@SpringBootApplication
public class DualStorageDemoApplication implements CommandLineRunner
{
	private static final Logger LOG = LoggerFactory.getLogger(DualStorageDemoApplication.class);
	private final InvoiceRepository invoiceRepository;
	private final PersonRepository personRepository;
	
	public DualStorageDemoApplication(
		final InvoiceRepository invoiceRepository,
		final PersonRepository personRepository)
	{
		this.invoiceRepository = invoiceRepository;
		this.personRepository = personRepository;
	}
	
	public static void main(final String[] args)
	{
		SpringApplication.run(DualStorageDemoApplication.class, args);
	}
	
	@Override
	public void run(final String... args)
	{
		LOG.info("----Invoices-BeforeDeleteAll----");
		this.invoiceRepository.findAll().forEach(i -> LOG.info(i.toString()));
		this.invoiceRepository.deleteAll();
		
		LOG.info("----Invoices-AfterDeleteAll----");
		this.invoiceRepository.findAll().forEach(i -> LOG.info(i.toString()));
		
		this.invoiceRepository.save(new Invoice("N1", 100.0));
		
		LOG.info("----Persons-BeforeDeleteAll----");
		this.personRepository.findAll().forEach(i -> LOG.info(i.toString()));
		this.personRepository.deleteAll();
		
		LOG.info("----Persons-AfterDeleteAll----");
		this.personRepository.findAll().forEach(i -> LOG.info(i.toString()));
		
		this.personRepository.save(new Person("Stevie", "Nicks"));
	}
}
