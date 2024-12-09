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
import software.xdev.spring.data.eclipse.store.repository.root.RootDataV2;
import software.xdev.spring.data.eclipse.store.repository.root.VersionedRoot;


/**
 * Copies elements from the old Root({@link software.xdev.spring.data.eclipse.store.repository.Root} to the newer
 * version of Root({@link RootDataV2}).
 * <p>
 * <b>All migration scripts must be added to
 * {@link software.xdev.spring.data.eclipse.store.repository.EclipseStoreMigrator#SCRIPTS}!</b>
 */
@SuppressWarnings({"checkstyle:TypeName", "deprecation"})
public class v2_4_0_InitializeLazy extends LoggingUpdateScript
{
	private static final Logger LOG = LoggerFactory.getLogger(v2_4_0_InitializeLazy.class);
	
	@Override
	public void loggedMigrate(final Context<VersionedRoot, MigrationEmbeddedStorageManager> context)
	{
		final VersionedRoot versionedRoot = context.getMigratingObject();
		if(versionedRoot.getRootDataV2() != null)
		{
			versionedRoot.getRootDataV2().getEntityListsToStore().forEach(
				(entityName, entities) ->
				{
					final software.xdev.spring.data.eclipse.store.repository.root.v2_4.EntityData<Object, Object>
						newEntityData = versionedRoot.getCurrentRootData().getEntityData(entityName);
					entities.getEntities().forEach(newEntityData::ensureEntityAndReturnObjectsToStore);
					newEntityData.setLastId(entities.getLastId());
					context.getStorageManager().getNativeStorageManager().storeAll(newEntityData.getObjectsToStore());
					LOG.info("Migrated entities {}.", entityName);
				}
			);
		}
		versionedRoot.clearOldRootData();
		context.getStorageManager().store(versionedRoot);
	}
}
