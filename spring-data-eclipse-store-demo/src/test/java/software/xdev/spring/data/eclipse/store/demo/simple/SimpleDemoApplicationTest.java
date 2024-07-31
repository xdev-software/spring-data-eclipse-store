package software.xdev.spring.data.eclipse.store.demo.simple;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import software.xdev.spring.data.eclipse.store.demo.TestUtil;
import software.xdev.spring.data.eclipse.store.repository.config.DefaultEclipseStoreClientConfiguration;
import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;


@SpringBootTest(classes = SimpleDemoApplication.class)
class SimpleDemoApplicationTest
{
	public static final String STORAGE_PATH = "storage";
	private final EclipseStoreClientConfiguration configuration;
	
	@Autowired
	public SimpleDemoApplicationTest(final DefaultEclipseStoreClientConfiguration configuration)
	{
		this.configuration = configuration;
	}
	
	@BeforeAll
	static void clearPreviousData()
	{
		TestUtil.deleteDirectory(new File("./" + STORAGE_PATH));
	}
	
	@Test
	void checkPossibilityToSimplyStartAndRestartApplication()
	{
		this.configuration.getStorageInstance().clearData();
		this.configuration.getStorageInstance().stop();
		SimpleDemoApplication.main(new String[]{});
	}
}
