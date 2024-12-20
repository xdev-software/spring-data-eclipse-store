package software.xdev.spring.data.eclipse.store.integration.isolated.tests.data.migration.own.migrator;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


public class PersistedEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
}
