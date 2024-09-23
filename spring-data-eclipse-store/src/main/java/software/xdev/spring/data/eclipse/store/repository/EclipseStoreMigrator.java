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
import software.xdev.micromigration.migrater.reflection.ReflectiveMigrater;
import software.xdev.micromigration.scripts.VersionAgnosticMigrationScript;
import software.xdev.micromigration.version.MigrationVersion;
import software.xdev.spring.data.eclipse.store.repository.root.VersionedRoot;
import software.xdev.spring.data.eclipse.store.repository.root.update.scripts.v2_0_0_InitalizeVersioning;


public final class EclipseStoreMigrator
{
	public static final Class<?> FIRST_UPDATE_SCRIPT = v2_0_0_InitalizeVersioning.class;
	
	private EclipseStoreMigrator()
	{
	}
	
	public static void migrate(final VersionedRoot versionedRoot, final EmbeddedStorageManager storageManager)
	{
		final ReflectiveMigrater migrater =
			new ReflectiveMigrater(FIRST_UPDATE_SCRIPT.getPackageName());
		new MigrationManager(versionedRoot, migrater, storageManager)
			.migrate(versionedRoot);
	}
	
	public static MigrationVersion getLatestVersion()
	{
		final ReflectiveMigrater migrater =
			new ReflectiveMigrater(FIRST_UPDATE_SCRIPT.getPackageName());
		final TreeSet<VersionAgnosticMigrationScript<?, ?>> sortedScripts = migrater.getSortedScripts();
		return sortedScripts.isEmpty() ? new MigrationVersion(0, 0, 0) : sortedScripts.last().getTargetVersion();
	}
}
