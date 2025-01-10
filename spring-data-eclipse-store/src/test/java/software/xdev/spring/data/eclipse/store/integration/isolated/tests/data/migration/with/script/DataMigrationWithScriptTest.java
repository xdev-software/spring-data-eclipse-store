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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.data.migration.with.script;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.micromigration.version.MigrationVersion;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {DataMigrationWithScriptTestConfiguration.class})
class DataMigrationWithScriptTest
{
	@Autowired
	private DataMigrationWithScriptTestConfiguration configuration;
	@Autowired
	private PersistedEntityRepository repository;
	
	@Test
	void assertUpdateV1Executed()
	{
		Assertions.assertEquals(1, this.repository.count());
		Assertions.assertEquals(
			new MigrationVersion(1, 0, 0),
			this.configuration.getStorageInstance().getRoot().getDataVersion().getVersion());
	}
	
	@Test
	void assertNotUpdatedAfterMigration()
	{
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				Assertions.assertEquals(1, this.repository.count());
				Assertions.assertEquals(
					new MigrationVersion(1, 0, 0),
					this.configuration.getStorageInstance().getRoot().getDataVersion().getVersion());
			}
		);
	}
}