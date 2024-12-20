package software.xdev.spring.data.eclipse.store.repository.root.data.version;

import org.springframework.stereotype.Component;

import software.xdev.micromigration.eclipsestore.MigrationEmbeddedStorageManager;
import software.xdev.micromigration.scripts.ReflectiveVersionMigrationScript;
import software.xdev.spring.data.eclipse.store.repository.root.VersionedRoot;


@Component
public abstract class DataMigrationScript
	extends ReflectiveVersionMigrationScript<VersionedRoot, MigrationEmbeddedStorageManager>
{
}
