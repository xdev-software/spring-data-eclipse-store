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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import software.xdev.micromigration.eclipsestore.MigrationEmbeddedStorageManager;
import software.xdev.micromigration.scripts.Context;
import software.xdev.spring.data.eclipse.store.repository.root.VersionedRoot;
import software.xdev.spring.data.eclipse.store.repository.root.data.version.ReflectiveDataMigrationScript;


@SuppressWarnings({"checkstyle:TypeName", "java:S101"})
@Component
public class v1_0_0_Init extends ReflectiveDataMigrationScript
{
	private final PersistedEntityRepository repository;
	
	public v1_0_0_Init(@Autowired final PersistedEntityRepository repository)
	{
		this.repository = repository;
	}
	
	@Override
	public void migrate(final Context<VersionedRoot, MigrationEmbeddedStorageManager> context)
	{
		this.repository.save(new PersistedEntity());
	}
}
