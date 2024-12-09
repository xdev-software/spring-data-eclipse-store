package software.xdev.spring.data.eclipse.store.demo.lazy.complex;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import software.xdev.spring.data.eclipse.store.demo.TestUtil;
import software.xdev.spring.data.eclipse.store.demo.lazy.LazyConfiguration;
import software.xdev.spring.data.eclipse.store.demo.lazy.LazyDemoApplication;
import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;


@SpringBootTest(classes = LazyDemoApplication.class)
class LazyDemoApplicationTest
{
	private final EclipseStoreClientConfiguration configuration;
	
	@Autowired
	public LazyDemoApplicationTest(final LazyConfiguration configuration)
	{
		this.configuration = configuration;
	}
	
	@BeforeAll
	static void clearPreviousData()
	{
		TestUtil.deleteDirectory(new File("./" + LazyConfiguration.STORAGE_PATH));
	}
	
	@Test
	void checkPossibilityToSimplyStartAndRestartApplication()
	{
		this.configuration.getStorageInstance().stop();
		LazyDemoApplication.main(new String[]{});
	}
}
