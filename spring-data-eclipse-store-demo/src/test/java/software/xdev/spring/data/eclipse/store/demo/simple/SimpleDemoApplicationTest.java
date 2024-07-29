package software.xdev.spring.data.eclipse.store.demo.simple;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes = SimpleDemoApplication.class)
class SimpleDemoApplicationTest
{
	@Test
	void checkPossibilityToSimplyStartApplication()
	{
		assertTrue(true);
	}
}
