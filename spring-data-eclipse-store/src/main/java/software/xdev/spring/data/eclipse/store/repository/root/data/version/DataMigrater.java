package software.xdev.spring.data.eclipse.store.repository.root.data.version;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import software.xdev.micromigration.migrater.ExplicitMigrater;
import software.xdev.micromigration.migrater.VersionAlreadyRegisteredException;


@Component
public class DataMigrater extends ExplicitMigrater
{
	public DataMigrater(@Autowired final List<DataMigrationScript> scripts) throws VersionAlreadyRegisteredException
	{
		super(scripts.toArray(DataMigrationScript[]::new));
	}
}
