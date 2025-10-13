package software.xdev.spring.data.eclipse.store.demo.complex;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import software.xdev.spring.data.eclipse.store.demo.TestUtil;
import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;


@SpringBootTest(
	classes = ComplexDemoApplication.class,
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class ComplexDemoApplicationTest
{
	@LocalServerPort
	private int port;
	@Autowired
	private TestRestTemplate restTemplate;
	
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
		this.configuration.getStorageInstance().stop();
		Assertions.assertDoesNotThrow(() -> ComplexDemoApplication.main(new String[]{}));
	}
	
	@Test
	void restEndpoint()
	{
		assertThat(
			this.restTemplate.getForObject(
				"http://localhost:" + this.port + "/store-data/default/root",
				String.class
			)
		).contains("ROOT");
	}
}
