/*
 * Copyright © 2024 XDEV Software (https://xdev.software)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.constraints;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Consumer;

import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;


/**
 * These tests should show that all or most of the following constraints are available in this library: <a
 * href="https://jakarta.ee/learn/docs/jakartaee-tutorial/current/beanvalidation/bean-validation/bean-validation
 * .html#_using_jakarta_bean_validation_constraints">Jakarta Bean Validation Constraints</a>
 */
@IsolatedTestAnnotations
@ContextConfiguration(classes = {ConstraintsTestConfiguration.class})
@SuppressWarnings("checkstyle:MethodName")
@DirtiesContext(classMode = BEFORE_CLASS)
class ConstraintsTest
{
	@Autowired
	private ConstraintsTestConfiguration configuration;
	@Autowired
	private ConstraintsRepository repository;
	
	@Test
	void assertFalseWithTrue()
	{
		this.assertConstraintViolationChange(o -> o.setAlwaysFalse(true));
	}
	
	@Test
	void assertFalseWithFalse()
	{
		this.assertGoodChange(o -> o.setAlwaysFalse(false));
	}
	
	@Test
	void assertTrueWithTrue()
	{
		this.assertGoodChange(o -> o.setAlwaysTrue(true));
	}
	
	@Test
	void assertTrueWithFalse()
	{
		this.assertConstraintViolationChange(o -> o.setAlwaysTrue(false));
	}
	
	@Test
	void assertDigitsGood()
	{
		this.assertGoodChange(o -> o.setPrice(BigDecimal.valueOf(123456.78)));
	}
	
	@Test
	void assertDigitsTooMuchInteger()
	{
		this.assertConstraintViolationChange(o -> o.setPrice(BigDecimal.valueOf(1234567)));
	}
	
	@Test
	void assertDigitsTooMuchFraction()
	{
		this.assertConstraintViolationChange(o -> o.setPrice(BigDecimal.valueOf(0.123)));
	}
	
	@Test
	void assertDecimalMinGoodExact()
	{
		this.assertGoodChange(o -> o.setDiscountMin5(BigDecimal.valueOf(5)));
	}
	
	@Test
	void assertDecimalMinGoodGreater()
	{
		this.assertGoodChange(o -> o.setDiscountMin5(BigDecimal.valueOf(6)));
	}
	
	@Test
	void assertDecimalMinBadSmaller()
	{
		this.assertConstraintViolationChange(o -> o.setDiscountMin5(BigDecimal.valueOf(4)));
	}
	
	@Test
	void assertDecimalMinBadNegative()
	{
		this.assertConstraintViolationChange(o -> o.setDiscountMin5(BigDecimal.valueOf(-1)));
	}
	
	@Test
	void assertDecimalMaxGoodExact()
	{
		this.assertGoodChange(o -> o.setDiscountMax20(BigDecimal.valueOf(20)));
	}
	
	@Test
	void assertDecimalMaxGoodSmaller()
	{
		this.assertGoodChange(o -> o.setDiscountMax20(BigDecimal.ONE));
	}
	
	@Test
	void assertDecimalMaxGoodZero()
	{
		this.assertGoodChange(o -> o.setDiscountMax20(BigDecimal.ZERO));
	}
	
	@Test
	void assertDecimalMaxBadBigger()
	{
		this.assertConstraintViolationChange(o -> o.setDiscountMax20(BigDecimal.valueOf(21)));
	}
	
	@Test
	void assertDecimalMaxBadBiggest()
	{
		this.assertConstraintViolationChange(o -> o.setDiscountMax20(BigDecimal.valueOf(Double.MAX_VALUE)));
	}
	
	@Test
	void assertDecimalMaxGoodNegative()
	{
		this.assertGoodChange(o -> o.setDiscountMax20(BigDecimal.valueOf(-1)));
	}
	
	@Test
	void assertEmailGood()
	{
		this.assertGoodChange(o -> o.setEmailField("a@b.c"));
	}
	
	@Test
	void assertEmailBad_MissingAt()
	{
		this.assertConstraintViolationChange(o -> o.setEmailField("a.b"));
	}
	
	@Test
	void assertEmailGood_MissingDot()
	{
		this.assertGoodChange(o -> o.setEmailField("a@b"));
	}
	
	@Test
	void assertEmailBad_NoPrefixBeforeAt()
	{
		this.assertConstraintViolationChange(o -> o.setEmailField("@b.c"));
	}
	
	@Test
	void assertEmailGood_Empty()
	{
		this.assertGoodChange(o -> o.setEmailField(""));
	}
	
	@Test
	void assertEmailGood_Null()
	{
		this.assertGoodChange(o -> o.setEmailField(null));
	}
	
	@Test
	void assertFutureFieldGood()
	{
		this.assertGoodChange(o -> o.setFutureField(Date.from(Instant.now().plus(1, ChronoUnit.DAYS))));
	}
	
	@Test
	void assertFutureFieldBad_Now()
	{
		this.assertConstraintViolationChange(o -> o.setFutureField(Date.from(Instant.now())));
	}
	
	@Test
	void assertFutureFieldBad_Yesterday()
	{
		this.assertConstraintViolationChange(o -> o.setFutureField(Date.from(Instant.now().minus(1, ChronoUnit.DAYS))));
	}
	
	@Test
	void assertFutureOrPresentFieldGood()
	{
		this.assertGoodChange(o -> o.setFutureOrPresentField(Date.from(Instant.now().plus(1, ChronoUnit.DAYS))));
	}
	
	@Test
	void assertFutureOrPresentFieldBad_Yesterday()
	{
		this.assertConstraintViolationChange(o -> o.setFutureOrPresentField(Date.from(Instant.now()
			.minus(1, ChronoUnit.DAYS))));
	}
	
	@Test
	void assertPastFieldBad()
	{
		this.assertConstraintViolationChange(o -> o.setPastField(Date.from(Instant.now().plus(1, ChronoUnit.DAYS))));
	}
	
	@Test
	void assertPastFieldGood_Now()
	{
		this.assertGoodChange(o -> o.setPastField(Date.from(Instant.now())));
	}
	
	@Test
	void assertPastFieldGood_Yesterday()
	{
		this.assertGoodChange(o -> o.setPastField(Date.from(Instant.now().minus(1, ChronoUnit.DAYS))));
	}
	
	@Test
	void assertPastOrPresentFieldBad()
	{
		this.assertConstraintViolationChange(o -> o.setPastOrPresentField(Date.from(Instant.now()
			.plus(1, ChronoUnit.DAYS))));
	}
	
	@Test
	void assertPastOrPresentFieldGood_Now()
	{
		this.assertGoodChange(o -> o.setPastOrPresentField(Date.from(Instant.now())));
	}
	
	@Test
	void assertPastOrPresentFieldGood_Yesterday()
	{
		this.assertGoodChange(o -> o.setPastOrPresentField(Date.from(Instant.now().minus(1, ChronoUnit.DAYS))));
	}
	
	@Test
	void assertMaxGood()
	{
		this.assertGoodChange(o -> o.setQuantityMax10(10));
	}
	
	@Test
	void assertMaxBad()
	{
		this.assertConstraintViolationChange(o -> o.setQuantityMax10(11));
	}
	
	@Test
	void assertMinGood()
	{
		this.assertGoodChange(o -> o.setQuantityMin5(5));
	}
	
	@Test
	void assertMinBad()
	{
		this.assertConstraintViolationChange(o -> o.setQuantityMin5(3));
	}
	
	@Test
	void assertNegativeFieldGood()
	{
		this.assertGoodChange(o -> o.setNegativeField(-1));
	}
	
	@Test
	void assertNegativeFieldBad()
	{
		this.assertConstraintViolationChange(o -> o.setNegativeField(1));
	}
	
	@Test
	void assertNegativeFieldBad_Zero()
	{
		this.assertConstraintViolationChange(o -> o.setNegativeField(0));
	}
	
	@Test
	void assertNegativeOrZeroFieldGood_Zero()
	{
		this.assertGoodChange(o -> o.setNegativeOrZeroField(0));
	}
	
	@Test
	void assertNegativeOrZeroFieldGood()
	{
		this.assertGoodChange(o -> o.setNegativeOrZeroField(-1));
	}
	
	@Test
	void assertNegativeOrZeroFieldBad()
	{
		this.assertConstraintViolationChange(o -> o.setNegativeOrZeroField(1));
	}
	
	@Test
	void assertNotBlankGood()
	{
		this.assertGoodChange(o -> o.setMessageNotBlank("a"));
	}
	
	@Test
	void assertNotBlankBad_Null()
	{
		this.assertConstraintViolationChange(o -> o.setMessageNotBlank(null));
	}
	
	@Test
	void assertNotBlankBad_Empty()
	{
		this.assertConstraintViolationChange(o -> o.setMessageNotBlank(""));
	}
	
	@Test
	void assertNotBlankBad_Space()
	{
		this.assertConstraintViolationChange(o -> o.setMessageNotBlank(" "));
	}
	
	@Test
	void assertNotEmptyGood()
	{
		this.assertGoodChange(o -> o.setMessageNotEmpty("a"));
	}
	
	@Test
	void assertNotEmptyBad_Null()
	{
		this.assertConstraintViolationChange(o -> o.setMessageNotEmpty(null));
	}
	
	@Test
	void assertNotEmptyBad_Empty()
	{
		this.assertConstraintViolationChange(o -> o.setMessageNotEmpty(""));
	}
	
	@Test
	void assertNotEmptyGood_Space()
	{
		this.assertGoodChange(o -> o.setUsername(" "));
	}
	
	@Test
	void assertNotNullGood()
	{
		this.assertGoodChange(o -> o.setUsername("a"));
	}
	
	@Test
	void assertNotNullBad_Null()
	{
		this.assertConstraintViolationChange(o -> o.setUsername(null));
	}
	
	@Test
	void assertNotNullGood_Empty()
	{
		this.assertGoodChange(o -> o.setUsername(""));
	}
	
	@Test
	void assertNotNullGood_Space()
	{
		this.assertGoodChange(o -> o.setUsername(" "));
	}
	
	@Test
	void assertPatternGood()
	{
		this.assertGoodChange(o -> o.setPhoneNumber("(123)456-7890"));
	}
	
	@Test
	void assertPatternBad()
	{
		this.assertConstraintViolationChange(o -> o.setPhoneNumber("456-7890"));
	}
	
	@Test
	void assertPositiveGood()
	{
		this.assertGoodChange(o -> o.setArea(BigDecimal.ONE));
	}
	
	@Test
	void assertPositiveBad()
	{
		this.assertConstraintViolationChange(o -> o.setArea(BigDecimal.valueOf(-1)));
	}
	
	@Test
	void assertPositiveBad_Zero()
	{
		this.assertConstraintViolationChange(o -> o.setArea(BigDecimal.ZERO));
	}
	
	@Test
	void assertPositiveOrZeroGood()
	{
		this.assertGoodChange(o -> o.setPositiveOrZeroField(1));
	}
	
	@Test
	void assertPositiveOrZeroBad()
	{
		this.assertConstraintViolationChange(o -> o.setPositiveOrZeroField(-1));
	}
	
	@Test
	void assertPositiveOrZeroGood_Zero()
	{
		this.assertGoodChange(o -> o.setPositiveOrZeroField(0));
	}
	
	@Test
	void assertStringMinAndMaxGood()
	{
		this.assertGoodChange(o -> o.setMessageMin2AndMax10("123"));
	}
	
	@Test
	void assertStringMinAndMaxBad_TooSmall()
	{
		this.assertConstraintViolationChange(o -> o.setMessageMin2AndMax10("1"));
	}
	
	@Test
	void assertStringMinAndMaxBad_TooBig()
	{
		this.assertConstraintViolationChange(o -> o.setMessageMin2AndMax10("1234567890-"));
	}
	
	/// ////-------------Help Functions -------------//////////////
	private void assertGoodChange(final Consumer<ConstraintDaoObject> change)
	{
		final ConstraintDaoObject constraintDaoObject = new ConstraintDaoObject();
		change.accept(constraintDaoObject);
		this.assertGoodSave(constraintDaoObject);
	}
	
	private void assertGoodSave(final ConstraintDaoObject constraintDaoObject)
	{
		Assertions.assertDoesNotThrow(() -> this.repository.save(constraintDaoObject));
	}
	
	private void assertConstraintViolationChange(final Consumer<ConstraintDaoObject> change)
	{
		final ConstraintDaoObject constraintDaoObject = new ConstraintDaoObject();
		change.accept(constraintDaoObject);
		this.assertConstraintViolationSave(constraintDaoObject);
	}
	
	private void assertConstraintViolationSave(final ConstraintDaoObject constraintDaoObject)
	{
		Assertions.assertThrows(
			ConstraintViolationException.class,
			() -> this.repository.save(constraintDaoObject)
		);
	}
}
