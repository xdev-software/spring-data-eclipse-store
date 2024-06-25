package software.xdev.spring.data.eclipse.store.benchmark;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;


public final class BenchmarkRunner
{
	private BenchmarkRunner()
	{
	}
	
	@SuppressWarnings("checkstyle:MagicNumber")
	public static void main(final String[] args) throws RunnerException
	{
		final Options opt = new OptionsBuilder()
			.mode(Mode.AverageTime)
			.forks(1)
			.warmupTime(TimeValue.seconds(1))
			.warmupIterations(8)
			.measurementTime(TimeValue.seconds(1))
			.measurementIterations(10)
			.timeUnit(TimeUnit.MILLISECONDS)
			// Example for executing a single method:
			// .include(
			// 	"software.xdev.spring.data.eclipse.store.benchmark.benchmarks.simple.customer"
			// 		+ ".StoringSimpleCustomerBenchmark.save1000CustomerInSaveAll")
			.build();
		
		new Runner(opt).run();
	}
}

