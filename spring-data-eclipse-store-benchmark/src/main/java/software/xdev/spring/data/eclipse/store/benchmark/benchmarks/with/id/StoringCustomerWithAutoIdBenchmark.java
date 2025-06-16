package software.xdev.spring.data.eclipse.store.benchmark.benchmarks.with.id;

import java.util.stream.IntStream;

import org.openjdk.jmh.annotations.Benchmark;

import software.xdev.spring.data.eclipse.store.benchmark.SpringState;


public class StoringCustomerWithAutoIdBenchmark
{
	@Benchmark
	public void saveSingleCustomerWithAutoId(final SpringState state)
	{
		final CustomerWithAutoIdRepository customerRepository =
			state.getBean(CustomerWithAutoIdRepository.class);
		customerRepository.save(new CustomerWithAutoId("Test", "Test"));
	}
	
	@Benchmark
	public void save100CustomerInForEach(final SpringState state)
	{
		final CustomerWithAutoIdRepository customerRepository =
			state.getBean(CustomerWithAutoIdRepository.class);
		IntStream.range(0, 100).forEach(
			i -> customerRepository.save(new CustomerWithAutoId("Test" + i, "Test" + i))
		);
	}
	
	@Benchmark
	public void save100CustomerInSaveAll(final SpringState state)
	{
		final CustomerWithAutoIdRepository customerRepository =
			state.getBean(CustomerWithAutoIdRepository.class);
		customerRepository.saveAll(
			IntStream.range(0, 100).mapToObj(
				i -> new CustomerWithAutoId("Test" + i, "Test" + i)
			).toList()
		);
	}
	
	@Benchmark
	public void save1000CustomerInForEach(final SpringState state)
	{
		final CustomerWithAutoIdRepository customerRepository =
			state.getBean(CustomerWithAutoIdRepository.class);
		IntStream.range(0, 1000).forEach(
			i -> customerRepository.save(new CustomerWithAutoId("Test" + i, "Test" + i))
		);
	}
	
	@Benchmark
	public void save1000CustomerInForEachParallel(final SpringState state)
	{
		final CustomerWithAutoIdRepository customerRepository =
			state.getBean(CustomerWithAutoIdRepository.class);
		IntStream.range(0, 1000).parallel().forEach(
			i -> customerRepository.save(new CustomerWithAutoId("Test" + i, "Test" + i))
		);
	}
	
	@Benchmark
	public void save1000CustomerInSaveAll(final SpringState state)
	{
		final CustomerWithAutoIdRepository customerRepository =
			state.getBean(CustomerWithAutoIdRepository.class);
		customerRepository.saveAll(
			IntStream.range(0, 1000).mapToObj(
				i -> new CustomerWithAutoId("Test" + i, "Test" + i)
			).toList()
		);
	}
}
