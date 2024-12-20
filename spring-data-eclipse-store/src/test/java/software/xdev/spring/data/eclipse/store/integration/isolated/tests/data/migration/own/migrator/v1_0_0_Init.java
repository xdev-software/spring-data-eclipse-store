package software.xdev.spring.data.eclipse.store.integration.isolated.tests.data.migration.own.migrator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import software.xdev.micromigration.eclipsestore.MigrationEmbeddedStorageManager;
import software.xdev.micromigration.scripts.Context;
import software.xdev.spring.data.eclipse.store.repository.root.VersionedRoot;
import software.xdev.spring.data.eclipse.store.repository.root.data.version.DataMigrationScript;


@SuppressWarnings("CheckStyle")
@Component
public class v1_0_0_Init extends DataMigrationScript
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
