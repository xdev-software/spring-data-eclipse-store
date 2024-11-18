package software.xdev.spring.data.eclipse.store.benchmark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import software.xdev.spring.data.eclipse.store.repository.config.EnableEclipseStoreRepositories;


@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@SpringBootApplication
@EnableEclipseStoreRepositories
public class BenchmarkApplication
{
	private static final Logger LOG = LoggerFactory.getLogger(BenchmarkApplication.class);
	
	public static void main(final String[] args)
	{
		try
		{
			final ConfigurableApplicationContext run = SpringApplication.run(BenchmarkRunner.class, args);
			run.close();
		}
		catch(final Exception e)
		{
			LOG.error("Error starting the benchmark", e);
		}
	}
}
