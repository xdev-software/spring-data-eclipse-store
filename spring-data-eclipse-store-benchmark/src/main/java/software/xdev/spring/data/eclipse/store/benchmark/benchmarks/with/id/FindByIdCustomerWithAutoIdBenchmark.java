package software.xdev.spring.data.eclipse.store.benchmark.benchmarks.with.id;

import java.util.stream.IntStream;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;

import software.xdev.spring.data.eclipse.store.benchmark.SpringState;
import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;


public class FindByIdCustomerWithAutoIdBenchmark
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
			this.getBean(EclipseStoreClientConfiguration.class).getStorageInstance().clearData();
			this.getBean(CustomerWithAutoIdRepository.class).saveAll(
				IntStream.range(0, this.entityCount).mapToObj(
					i -> new CustomerWithAutoId("Test" + i, "Test" + i)
				).toList()
			);
			this.getBean(EclipseStoreClientConfiguration.class).getStorageInstance().stop();
		}
	}
	
	
	public static class Simple100CustomerSpringState extends ExistingCustomerSpringState
	{
		public Simple100CustomerSpringState()
		{
			super(100);
		}
	}
	
	@Benchmark
	public void find10LastCustomersOf100(final Simple100CustomerSpringState state, final Blackhole blackhole)
	{
		final CustomerWithAutoIdRepository customerRepository = state.getBean(CustomerWithAutoIdRepository.class);
		blackhole.consume(customerRepository.findAllById(IntStream.range(89, 100).boxed().toList()));
	}
	
	public static class Simple1000CustomerSpringState extends ExistingCustomerSpringState
	{
		public Simple1000CustomerSpringState()
		{
			super(1_000);
		}
	}
	
	@Benchmark
	public void find100LastCustomersOf1000(final Simple1000CustomerSpringState state, final Blackhole blackhole)
	{
		final CustomerWithAutoIdRepository customerRepository = state.getBean(CustomerWithAutoIdRepository.class);
		blackhole.consume(customerRepository.findAllById(IntStream.range(899, 1000).boxed().toList()));
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
	public void find1000LastCustomersOf10000(final Simple10000CustomerSpringState state, final Blackhole blackhole)
	{
		final CustomerWithAutoIdRepository customerRepository = state.getBean(CustomerWithAutoIdRepository.class);
		blackhole.consume(customerRepository.findAllById(IntStream.range(8999, 10000).boxed().toList()));
	}
}
