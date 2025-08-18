package software.xdev.spring.data.eclipse.store.benchmark.benchmarks.simple.customer;

import java.util.stream.IntStream;

import org.openjdk.jmh.annotations.Benchmark;

import software.xdev.spring.data.eclipse.store.benchmark.SpringState;


@SuppressWarnings("checkstyle:MagicNumber")
public class StoringSimpleCustomerBenchmark extends AbstractStoringSimpleCustomerBenchmark
{
	@Benchmark
	public void saveSingleCustomer(final SpringState state)
	{
		final CustomerRepository customerRepository = state.getBean(CustomerRepository.class);
		customerRepository.save(new Customer("Test", "Test"));
	}
	
	@Benchmark
	public void save10000CustomerInForEach(final SpringState state)
	{
		this.saveCustomerInForEach(state, 10_000);
	}
	
	@Benchmark
	public void save10000CustomerInForEachParallel(final SpringState state)
	{
		this.saveCustomerInForEachParallel(state, 10_000);
	}
	
	@Override
	protected void saveCustomerInForEach(final SpringState state, final int entityCount)
	{
		final CustomerRepository customerRepository = state.getBean(CustomerRepository.class);
		IntStream.range(0, entityCount).forEach(
			i -> customerRepository.save(new Customer("Test" + i, "Test" + i))
		);
	}
	
	private void saveCustomerInForEachParallel(final SpringState state, final int entityCount)
	{
		final CustomerRepository customerRepository = state.getBean(CustomerRepository.class);
		IntStream.range(0, entityCount).parallel().forEach(
			i -> customerRepository.save(new Customer("Test" + i, "Test" + i))
		);
	}
	
	@Override
	protected void saveCustomerInSaveAll(final SpringState state, final int entityCount)
	{
		final CustomerRepository customerRepository = state.getBean(CustomerRepository.class);
		customerRepository.saveAll(
			IntStream.range(0, entityCount).mapToObj(
				i -> new Customer("Test" + i, "Test" + i)
			).toList()
		);
	}
}
