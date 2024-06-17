package software.xdev.spring.data.eclipse.store.benchmark.benchmarks.simple.customer;

import java.util.stream.IntStream;

import org.openjdk.jmh.annotations.Benchmark;

import software.xdev.spring.data.eclipse.store.benchmark.SpringState;
import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;


@SuppressWarnings("checkstyle:MagicNumber")
public class StoringAndChangingSimpleCustomerBenchmark
{
	@Benchmark
	public void saveSingleCustomer(final SpringState state)
	{
		final CustomerRepository customerRepository = state.getBean(CustomerRepository.class);
		customerRepository.save(new Customer("Test", "Test"));
		
		state.getBean(EclipseStoreClientConfiguration.class).getStorageInstance().stop();
		
		final CustomerRepository customerRepository2 = state.getBean(CustomerRepository.class);
		customerRepository2.findAll().forEach(
			customer ->
			{
				customer.setFirstName("Another" + customer.getFirstName());
				customer.setLastName("Another" + customer.getLastName());
				customerRepository2.save(customer);
			});
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
	public void save100000CustomerInSaveAll(final SpringState state)
	{
		this.saveCustomerInSaveAll(state, 100_000);
	}
	
	private void saveCustomerInForEach(final SpringState state, final int entityCount)
	{
		final CustomerRepository customerRepository1 = state.getBean(CustomerRepository.class);
		IntStream.range(0, entityCount).forEach(
			i -> customerRepository1.save(new Customer("Test" + i, "Test" + i))
		);
		
		state.getBean(EclipseStoreClientConfiguration.class).getStorageInstance().stop();
		
		final CustomerRepository customerRepository2 = state.getBean(CustomerRepository.class);
		customerRepository2.findAll().forEach(
			customer ->
			{
				customer.setFirstName("Another" + customer.getFirstName());
				customer.setLastName("Another" + customer.getLastName());
				customerRepository2.save(customer);
			});
	}
	
	private void saveCustomerInSaveAll(final SpringState state, final int entityCount)
	{
		final CustomerRepository customerRepository = state.getBean(CustomerRepository.class);
		customerRepository.saveAll(
			IntStream.range(0, entityCount).mapToObj(
				i -> new Customer("Test" + i, "Test" + i)
			).toList()
		);
		
		state.getBean(EclipseStoreClientConfiguration.class).getStorageInstance().stop();
		
		final CustomerRepository customerRepository2 = state.getBean(CustomerRepository.class);
		final Iterable<Customer> all = customerRepository2.findAll();
		all.forEach(
			customer ->
			{
				customer.setFirstName("Another" + customer.getFirstName());
				customer.setLastName("Another" + customer.getLastName());
			});
		
		customerRepository2.saveAll(all);
	}
}
