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
package software.xdev.spring.data.eclipse.store.repository.root.data.version;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import software.xdev.micromigration.notification.ScriptExecutionNotificationWithoutScriptReference;
import software.xdev.micromigration.version.MigrationVersion;
import software.xdev.micromigration.version.Versioned;
import software.xdev.micromigration.version.VersionedAndKeeperOfHistory;


public class DataVersion implements Versioned, VersionedAndKeeperOfHistory
{
	private MigrationVersion currentVersion;
	private final List<ScriptExecutionNotificationWithoutScriptReference> migrationHistory = new ArrayList<>();
	
	@Override
	public void addExecutedScript(final ScriptExecutionNotificationWithoutScriptReference executedScriptInformation)
	{
		this.migrationHistory.add(Objects.requireNonNull(executedScriptInformation));
	}
	
	@Override
	public List<ScriptExecutionNotificationWithoutScriptReference> getMigrationHistory()
	{
		return this.migrationHistory;
	}
	
	@Override
	public void setVersion(final MigrationVersion version)
	{
		Objects.requireNonNull(version);
		this.currentVersion = version;
	}
	
	@Override
	public MigrationVersion getVersion()
	{
		return this.currentVersion;
	}
}
