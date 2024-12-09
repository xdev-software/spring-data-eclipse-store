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
package software.xdev.spring.data.eclipse.store.repository.root;

import software.xdev.micromigration.version.MigrationVersion;
import software.xdev.micromigration.version.Versioned;
import software.xdev.spring.data.eclipse.store.repository.EclipseStoreMigrator;
import software.xdev.spring.data.eclipse.store.repository.Root;


public class VersionedRoot implements Versioned
{
	private MigrationVersion version;
	
	private Root rootDataV1;
	
	private final RootDataV2 rootDataV2;
	
	public VersionedRoot()
	{
		this(null);
	}
	
	/**
	 * @param rootDataV1 is only filled if this is a old version <2.0.0 and needs upgrading
	 */
	public VersionedRoot(final Root rootDataV1)
	{
		this.rootDataV1 = rootDataV1;
		this.rootDataV2 = new RootDataV2();
		if(rootDataV1 != null)
		{
			this.version = new MigrationVersion(0, 0, 0);
		}
		else
		{
			this.version = EclipseStoreMigrator.getLatestVersion();
		}
	}
	
	public Root getRootDataV1()
	{
		return this.rootDataV1;
	}
	
	public RootDataV2 getRootDataV2()
	{
		return this.rootDataV2;
	}
	
	public void clearOldRootData()
	{
		this.rootDataV1 = null;
	}
	
	public RootDataV2 getCurrentRootData()
	{
		return this.rootDataV2;
	}
	
	@Override
	public void setVersion(final MigrationVersion migrationVersion)
	{
		this.version = migrationVersion;
	}
	
	@Override
	public MigrationVersion getVersion()
	{
		return this.version;
	}
}
