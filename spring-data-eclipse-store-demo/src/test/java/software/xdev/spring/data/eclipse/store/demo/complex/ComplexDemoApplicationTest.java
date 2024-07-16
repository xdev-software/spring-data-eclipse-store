package software.xdev.spring.data.eclipse.store.demo.complex;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes = ComplexDemoApplication.class)
class ComplexDemoApplicationTest
{
	@Test
	void checkPossibilityToSimplyStartApplication()
	{
		assertTrue(true);
	}
}
