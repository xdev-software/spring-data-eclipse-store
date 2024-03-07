/*
 * Copyright © 2023 XDEV Software (https://xdev.software)
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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.keywords;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestConfiguration;


/**
 * These tests should show that all or most of the following keywords are available in this library: <a
 * href="https://docs.spring.io/spring-data/jpa/reference/repositories/query-keywords-reference.html">Repository query
 * keywords</a>
 */
@IsolatedTestAnnotations
class KeywordsTest
{
	@Autowired
	private IsolatedTestConfiguration configuration;
	
	@Test
	@Disabled("For now we don't need 'existsBy'")
	void simpleStoreAndRead(final MinimalRepository repository)
	{
		repository.save(new MinimalDaoObject("1"));
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				Assertions.assertTrue(repository.existsByValue("1"));
				Assertions.assertFalse(repository.existsByValue("2"));
			}
		);
	}
}
