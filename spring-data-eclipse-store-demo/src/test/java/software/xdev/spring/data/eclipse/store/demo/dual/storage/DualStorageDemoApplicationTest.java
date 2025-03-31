package software.xdev.spring.data.eclipse.store.demo.dual.storage;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import software.xdev.spring.data.eclipse.store.demo.TestUtil;
import software.xdev.spring.data.eclipse.store.demo.dual.storage.invoice.PersistenceInvoiceConfiguration;
import software.xdev.spring.data.eclipse.store.demo.dual.storage.person.PersistencePersonConfiguration;


@SpringBootTest(classes = DualStorageDemoApplication.class)
class DualStorageDemoApplicationTest
{
	private final PersistenceInvoiceConfiguration invoiceConfiguration;
	private final PersistencePersonConfiguration personConfiguration;
	
	@Autowired
	public DualStorageDemoApplicationTest(
		final PersistenceInvoiceConfiguration invoiceConfiguration,
		final PersistencePersonConfiguration personConfiguration)
	{
		this.invoiceConfiguration = invoiceConfiguration;
		this.personConfiguration = personConfiguration;
	}
	
	@BeforeAll
	static void clearPreviousData()
	{
		TestUtil.deleteDirectory(new File("./" + PersistenceInvoiceConfiguration.STORAGE_PATH));
		TestUtil.deleteDirectory(new File("./"	+ PersistencePersonConfiguration.STORAGE_PATH));
	}
	
	@Test
	void checkPossibilityToSimplyStartAndRestartApplication()
	{
		this.invoiceConfiguration.getStorageInstance().stop();
		this.personConfiguration.getStorageInstance().stop();
		Assertions.assertDoesNotThrow(() -> DualStorageDemoApplication.main(new String[]{}));
	}
}
