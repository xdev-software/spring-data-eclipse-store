package software.xdev.spring.data.eclipse.store.repository.support.copier.copier;

import org.eclipse.serializer.SerializerFoundation;
import org.eclipse.serializer.persistence.binary.types.Binary;
import org.eclipse.serializer.persistence.types.PersistenceManager;


@FunctionalInterface
public interface PersistenceManagerProvider
{
	PersistenceManager<Binary> createPersistenceManager(SerializerFoundation<?> serializerFoundation);
}
