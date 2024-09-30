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
package software.xdev.spring.data.eclipse.store.repository.root.update.scripts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.xdev.micromigration.eclipsestore.MigrationEmbeddedStorageManager;
import software.xdev.micromigration.scripts.Context;
import software.xdev.micromigration.scripts.ReflectiveVersionMigrationScript;
import software.xdev.spring.data.eclipse.store.repository.root.VersionedRoot;


public abstract class LoggingUpdateScript
	extends ReflectiveVersionMigrationScript<VersionedRoot, MigrationEmbeddedStorageManager>
{
	private static final Logger LOG = LoggerFactory.getLogger(LoggingUpdateScript.class);
	
	@Override
	public void migrate(final Context<VersionedRoot, MigrationEmbeddedStorageManager> context)
	{
		LOG.info("Applying update {}...", this.getClass().getSimpleName());
		this.loggedMigrate(context);
		LOG.info("Applied update {}.", this.getClass().getSimpleName());
	}
	
	public abstract void loggedMigrate(final Context<VersionedRoot, MigrationEmbeddedStorageManager> context);
}
