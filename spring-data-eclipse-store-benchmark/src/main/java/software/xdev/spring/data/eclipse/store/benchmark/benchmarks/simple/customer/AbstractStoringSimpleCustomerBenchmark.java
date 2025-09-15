package software.xdev.spring.data.eclipse.store.benchmark.benchmarks.simple.customer;

import org.openjdk.jmh.annotations.Benchmark;

import software.xdev.spring.data.eclipse.store.benchmark.SpringState;


@SuppressWarnings("checkstyle:MagicNumber")
public abstract class AbstractStoringSimpleCustomerBenchmark
{
	protected abstract void saveCustomerInForEach(final SpringState state, final int entityCount);
	
	protected abstract void saveCustomerInSaveAll(final SpringState state, final int entityCount);
	
	@Benchmark
	public void save100CustomerInForEach(final SpringState state)
	{
		this.saveCustomerInForEach(state, 100);
	}
	
	@Benchmark
	public void save1000CustomerInForEach(final SpringState state)
	{
		this.saveCustomerInForEach(state, 1_000);
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
	public void save100000CustomerInSaveAll(final SpringState state)
	{
		this.saveCustomerInSaveAll(state, 100_000);
	}
}
