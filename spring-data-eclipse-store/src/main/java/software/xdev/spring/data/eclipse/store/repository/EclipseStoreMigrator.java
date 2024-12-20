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
package software.xdev.spring.data.eclipse.store.repository;

import java.util.TreeSet;

import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;

import software.xdev.micromigration.eclipsestore.MigrationManager;
import software.xdev.micromigration.migrater.ExplicitMigrater;
import software.xdev.micromigration.migrater.MicroMigrater;
import software.xdev.micromigration.scripts.VersionAgnosticMigrationScript;
import software.xdev.micromigration.version.MigrationVersion;
import software.xdev.spring.data.eclipse.store.repository.root.VersionedRoot;
import software.xdev.spring.data.eclipse.store.repository.root.data.version.DataVersion;
import software.xdev.spring.data.eclipse.store.repository.root.update.scripts.v2_0_0_InitializeVersioning;
import software.xdev.spring.data.eclipse.store.repository.root.update.scripts.v2_4_0_InitializeLazy;


public final class EclipseStoreMigrator
{
	public static final VersionAgnosticMigrationScript<?, ?>[] SCRIPTS =
		new VersionAgnosticMigrationScript[]{
			new v2_0_0_InitializeVersioning(),
			new v2_4_0_InitializeLazy()
		};
	
	private EclipseStoreMigrator()
	{
	}
	
	public static void migrateStructure(final VersionedRoot versionedRoot, final EmbeddedStorageManager storageManager)
	{
		final ExplicitMigrater migrater = new ExplicitMigrater(SCRIPTS);
		new MigrationManager(versionedRoot, migrater, storageManager).migrate(versionedRoot);
	}
	
	public static void migrateData(
		final DataVersion versionedData,
		final MicroMigrater migrater,
		final EmbeddedStorageManager storageManager)
	{
		if(migrater != null)
		{
			new MigrationManager(versionedData, migrater, storageManager).migrate(versionedData);
		}
	}
	
	public static MigrationVersion getLatestVersion()
	{
		final ExplicitMigrater migrater = new ExplicitMigrater(SCRIPTS);
		final TreeSet<VersionAgnosticMigrationScript<?, ?>> sortedScripts = migrater.getSortedScripts();
		return sortedScripts.isEmpty() ? new MigrationVersion(0, 0, 0) : sortedScripts.last().getTargetVersion();
	}
}
