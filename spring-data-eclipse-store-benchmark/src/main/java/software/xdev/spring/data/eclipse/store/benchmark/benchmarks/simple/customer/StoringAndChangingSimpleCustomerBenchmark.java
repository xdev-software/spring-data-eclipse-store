package software.xdev.spring.data.eclipse.store.benchmark.benchmarks.simple.customer;

import java.util.stream.IntStream;

import org.openjdk.jmh.annotations.Benchmark;

import software.xdev.spring.data.eclipse.store.benchmark.SpringState;
import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;


public class StoringAndChangingSimpleCustomerBenchmark extends AbstractStoringSimpleCustomerBenchmark
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
				this.changeCustomerName(customer);
				customerRepository2.save(customer);
			});
	}
	
	@Override
	protected void saveCustomerInForEach(final SpringState state, final int entityCount)
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
				this.changeCustomerName(customer);
				customerRepository2.save(customer);
			});
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
		
		state.getBean(EclipseStoreClientConfiguration.class).getStorageInstance().stop();
		
		final CustomerRepository customerRepository2 = state.getBean(CustomerRepository.class);
		final Iterable<Customer> all = customerRepository2.findAll();
		all.forEach(this::changeCustomerName);
		
		customerRepository2.saveAll(all);
	}
	
	private void changeCustomerName(final Customer customer)
	{
		customer.setFirstName("Another" + customer.getFirstName());
		customer.setLastName("Another" + customer.getLastName());
	}
}
