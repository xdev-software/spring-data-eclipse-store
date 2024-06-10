package software.xdev.spring.data.eclipse.store.benchmark.benchmarks.simple.customer;

import java.util.stream.IntStream;

import org.openjdk.jmh.annotations.Benchmark;

import software.xdev.spring.data.eclipse.store.benchmark.SpringState;


@SuppressWarnings("checkstyle:MagicNumber")
public class StoringSimpleCustomerBenchmark
{
	@Benchmark
	public void saveSingleCustomer(final SpringState state)
	{
		final CustomerRepository customerRepository = state.getBean(CustomerRepository.class);
		customerRepository.save(new Customer("Test", "Test"));
	}
	
	@Benchmark
	public void save100CustomerInForEach(final SpringState state)
	{
		this.saveCustomerInForEach(state, 100);
	}
	
	@Benchmark
	public void save100CustomerInSaveAll(final SpringState state)
	{
		this.saveCustomerInSaveAll(state, 100);
	}
	
	@Benchmark
	public void save1000CustomerInSaveAll(final SpringState state)
	{
		this.saveCustomerInSaveAll(state, 1_000);
	}
	
	@Benchmark
	public void save10000CustomerInSaveAll(final SpringState state)
	{
		this.saveCustomerInSaveAll(state, 10_000);
	}
	
	@Benchmark
	public void save1000CustomerInForEach(final SpringState state)
	{
		this.saveCustomerInForEach(state, 1_000);
	}
	
	@Benchmark
	public void save10000CustomerInForEach(final SpringState state)
	{
		this.saveCustomerInForEach(state, 10_000);
	}
	
	@Benchmark
	public void save100000CustomerInSaveAll(final SpringState state)
	{
		this.saveCustomerInSaveAll(state, 100_000);
	}
	
	private void saveCustomerInForEach(final SpringState state, final int entityCount)
	{
		final CustomerRepository customerRepository = state.getBean(CustomerRepository.class);
		IntStream.range(0, entityCount).forEach(
			i -> customerRepository.save(new Customer("Test" + i, "Test" + i))
		);
	}
	
	private void saveCustomerInSaveAll(final SpringState state, final int entityCount)
	{
		final CustomerRepository customerRepository = state.getBean(CustomerRepository.class);
		customerRepository.saveAll(
			IntStream.range(0, entityCount).mapToObj(
				i -> new Customer("Test" + i, "Test" + i)
			).toList()
		);
	}
}
