package software.xdev.spring.data.eclipse.store.benchmark.benchmarks.simple.customer;

import java.util.stream.IntStream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;

import software.xdev.spring.data.eclipse.store.benchmark.SpringState;
import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;


public class LoadingSimpleCustomerBenchmark
{
	public abstract static class ExistingCustomerSpringState extends SpringState
	{
		private final int entityCount;
		
		protected ExistingCustomerSpringState(final int entityCount)
		{
			this.entityCount = entityCount;
		}
		
		@Override
		@Setup(Level.Invocation)
		public void doSetupData()
		{
			this.getBean(EclipseStoreStorage.class).clearData();
			this.getBean(CustomerRepository.class).saveAll(
				IntStream.range(0, this.entityCount).mapToObj(
					i -> new Customer("Test" + i, "Test" + i)
				).toList()
			);
			this.getBean(EclipseStoreStorage.class).stop();
		}
	}
	
	
	public static class SimpleSingleCustomerSpringState extends SpringState
	{
		@Override
		@Setup(Level.Invocation)
		public void doSetupData()
		{
			this.getBean(EclipseStoreStorage.class).clearData();
			this.getBean(CustomerRepository.class).save(new Customer("Test", "Test"));
			this.getBean(EclipseStoreStorage.class).stop();
		}
	}
	
	@Benchmark
	public void loadSingleCustomer(final SimpleSingleCustomerSpringState state, final Blackhole blackhole)
	{
		final CustomerRepository customerRepository = state.getBean(CustomerRepository.class);
		blackhole.consume(customerRepository.findAll());
	}
	
	public static class Simple100CustomerSpringState extends ExistingCustomerSpringState
	{
		public Simple100CustomerSpringState()
		{
			super(100);
		}
	}
	
	@Benchmark
	public void load100Customers(final Simple100CustomerSpringState state, final Blackhole blackhole)
	{
		final CustomerRepository customerRepository = state.getBean(CustomerRepository.class);
		blackhole.consume(customerRepository.findAll());
	}
	
	public static class Simple1000CustomerSpringState extends ExistingCustomerSpringState
	{
		public Simple1000CustomerSpringState()
		{
			super(1_000);
		}
	}
	
	@Benchmark
	public void load1000Customers(final Simple1000CustomerSpringState state, final Blackhole blackhole)
	{
		final CustomerRepository customerRepository = state.getBean(CustomerRepository.class);
		blackhole.consume(customerRepository.findAll());
	}
	
	public static class Simple10000CustomerSpringState extends ExistingCustomerSpringState
	{
		@SuppressWarnings("checkstyle:MagicNumber")
		public Simple10000CustomerSpringState()
		{
			super(10_000);
		}
	}
	
	@Benchmark
	// Disabled because this is not working right now.
	public void load10000Customers(final Simple10000CustomerSpringState state, final Blackhole blackhole)
	{
		final CustomerRepository customerRepository = state.getBean(CustomerRepository.class);
		blackhole.consume(customerRepository.findAll());
	}
}
