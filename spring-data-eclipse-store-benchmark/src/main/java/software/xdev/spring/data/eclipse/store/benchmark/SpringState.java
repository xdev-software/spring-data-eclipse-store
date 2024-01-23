package software.xdev.spring.data.eclipse.store.benchmark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.FileSystemUtils;

import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;


@State(Scope.Thread)
public class SpringState
{
	private ConfigurableApplicationContext context;
	private Path tempStorageDirectory;
	
	@Setup(Level.Trial)
	public void doSetup() throws IOException
	{
		this.tempStorageDirectory = Files.createTempDirectory("tempstorage");
		this.context = SpringApplication.run(
			BenchmarkApplication.class,
			"--org.eclipse.store.storage-directory=" + this.tempStorageDirectory.toAbsolutePath());
	}
	
	@Setup(Level.Invocation)
	public void doSetupData()
	{
		this.context.getBean(EclipseStoreStorage.class).clearData();
	}
	
	@TearDown(Level.Invocation)
	public void doTearDownData()
	{
		this.context.getBean(EclipseStoreStorage.class).clearData();
	}
	
	@TearDown(Level.Trial)
	public void doTearDown() throws IOException
	{
		SpringApplication.exit(this.context);
		this.context.close();
		this.context = null;
		FileSystemUtils.deleteRecursively(this.tempStorageDirectory);
	}
	
	public <T> T getBean(final Class<T> classOfBeanToGet)
	{
		return this.context.getBean(classOfBeanToGet);
	}
}
