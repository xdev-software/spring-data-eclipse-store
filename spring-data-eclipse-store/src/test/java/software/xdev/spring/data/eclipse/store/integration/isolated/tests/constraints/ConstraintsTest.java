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

import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;


/**
 * These tests should show that all or most of the following constraints are available in this library: <a
 * href="https://jakarta.ee/learn/docs/jakartaee-tutorial/current/beanvalidation/bean-validation/bean-validation
 * .html#_using_jakarta_bean_validation_constraints">Jakarta Bean Validation Constraints</a>
 */
@IsolatedTestAnnotations
@ContextConfiguration(classes = {ConstraintsTestConfiguration.class})
class ConstraintsTest
{
	@Autowired
	private ConstraintsTestConfiguration configuration;
	@Autowired
	private ConstraintsRepository repository;
	
	@Test
	void assertFalseWithTrue()
	{
		final ConstraintDaoObject constraintDaoObject = new ConstraintDaoObject();
		constraintDaoObject.setAlwaysFalse(true);
		Assertions.assertThrows(
			ConstraintViolationException.class,
			() -> this.repository.save(constraintDaoObject)
		);
	}
	
	@Test
	void assertFalseWithFalse()
	{
		final ConstraintDaoObject constraintDaoObject = new ConstraintDaoObject();
		constraintDaoObject.setAlwaysFalse(false);
		Assertions.assertDoesNotThrow(() -> this.repository.save(constraintDaoObject));
	}
}
