package software.xdev.spring.data.eclipse.store.demo.complex;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import software.xdev.spring.data.eclipse.store.demo.TestUtil;
import software.xdev.spring.data.eclipse.store.demo.simple.SimpleDemoApplication;
import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;


@SpringBootTest(classes = ComplexDemoApplication.class)
class ComplexDemoApplicationTest
{
	private final EclipseStoreClientConfiguration configuration;
	
	@Autowired
	public ComplexDemoApplicationTest(final ComplexConfiguration configuration)
	{
		this.configuration = configuration;
	}
	
	@BeforeAll
	static void clearPreviousData()
	{
		TestUtil.deleteDirectory(new File("./" + ComplexConfiguration.STORAGE_PATH));
	}
	
	@Test
	void checkPossibilityToSimplyStartAndRestartApplication()
	{
		this.configuration.getStorageInstance().clearData();
		this.configuration.getStorageInstance().stop();
		SimpleDemoApplication.main(new String[]{});
	}
}
