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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.data.migration.with.migrater;

import java.util.TreeSet;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import software.xdev.micromigration.migrater.ExplicitMigrater;
import software.xdev.micromigration.migrater.MicroMigrater;
import software.xdev.micromigration.notification.ScriptExecutionNotificationWithScriptReference;
import software.xdev.micromigration.scripts.VersionAgnosticMigrationScript;
import software.xdev.micromigration.version.MigrationVersion;
import software.xdev.micromigration.versionagnostic.VersionAgnosticMigrationEmbeddedStorageManager;


@Component
public class CustomMigrater implements MicroMigrater
{
	private ExplicitMigrater explicitMigrater;
	final PersistedEntityRepository repository;
	
	@Autowired
	public CustomMigrater(final PersistedEntityRepository repository)
	{
		this.repository = repository;
	}
	
	private ExplicitMigrater ensureExplicitMigrater()
	{
		if(this.explicitMigrater == null)
		{
			this.explicitMigrater = new ExplicitMigrater(new v1_0_0_Init(this.repository));
		}
		return this.explicitMigrater;
	}
	
	@Override
	public TreeSet<? extends VersionAgnosticMigrationScript<?, ?>> getSortedScripts()
	{
		return this.ensureExplicitMigrater().getSortedScripts();
	}
	
	@Override
	public <E extends VersionAgnosticMigrationEmbeddedStorageManager<?, ?>> MigrationVersion migrateToNewest(
		final MigrationVersion fromVersion,
		final E storageManager,
		final Object root)
	{
		return this.ensureExplicitMigrater().migrateToNewest(fromVersion, storageManager, root);
	}
	
	@Override
	public <E extends VersionAgnosticMigrationEmbeddedStorageManager<?, ?>> MigrationVersion migrateToVersion(
		final MigrationVersion fromVersion,
		final MigrationVersion targetVersion,
		final E storageManager,
		final Object objectToMigrate)
	{
		return this.ensureExplicitMigrater()
			.migrateToVersion(fromVersion, targetVersion, storageManager, objectToMigrate);
	}
	
	@Override
	public void registerNotificationConsumer(
		final Consumer<ScriptExecutionNotificationWithScriptReference> notificationConsumer
	)
	{
		this.ensureExplicitMigrater().registerNotificationConsumer(notificationConsumer);
	}
}
