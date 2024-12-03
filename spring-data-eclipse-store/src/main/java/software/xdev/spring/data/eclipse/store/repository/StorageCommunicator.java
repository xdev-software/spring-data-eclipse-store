package software.xdev.spring.data.eclipse.store.repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import software.xdev.spring.data.eclipse.store.core.EntityProvider;
import software.xdev.spring.data.eclipse.store.repository.support.concurrency.ReadWriteLock;


public interface StorageCommunicator
{
	ReadWriteLock getReadWriteLock();
	
	<T> void store(Set<Object> nonEntitiesToStore, Class<T> domainClass, List<T> entitiesToStore);
	
	<T, ID> EntityProvider<T, ID> getEntityProvider(final Class<T> clazz);
	
	<T> long getEntityCount(Class<T> domainClass);
	
	<T> void delete(Class<T> domainClass, T foundEntity);
	
	<T> void deleteAll(Class<T> domainClass);
}
