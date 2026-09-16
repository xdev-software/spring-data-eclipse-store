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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.duplicated.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;


/**
 * These tests should show that all or most of the following keywords are available in this library: <a
 * href="https://docs.spring.io/spring-data/jpa/reference/repositories/query-keywords-reference.html">Repository query
 * keywords</a>
 */
@IsolatedTestAnnotations
@ContextConfiguration(classes = {DuplicatedRepositoriesTestConfiguration.class})
class DuplicatedRepositoriesTest
{
	@Autowired
	private DuplicatedRepositoriesTestConfiguration configuration;
	@Autowired
	private DaoFirstRepository firstRepository;
	@Autowired
	private DaoSecondRepository secondRepository;
	
	@Test
	void deleteReferencedObject()
	{
		this.firstRepository.save(new DaoObject(TestData.FIRST_NAME));
		this.secondRepository.save(new DaoObject(TestData.FIRST_NAME_ALTERNATIVE));
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				Assertions.assertEquals(2, TestUtil.iterableToList(this.firstRepository.findAll()).size());
				Assertions.assertEquals(2, TestUtil.iterableToList(this.secondRepository.findAll()).size());
			}
		);
	}
}
