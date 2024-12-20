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
