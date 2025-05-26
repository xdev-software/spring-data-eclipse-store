package software.xdev.spring.data.eclipse.store.jpa;

import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreCrudRepository;


/**
 * To be able to properly coexist with JPA in one project, the repositories must be declared as specific JPA- or
 * EclipseStore-Repositories.
 */
public interface CustomerInEclipseStoreRepository extends EclipseStoreCrudRepository<CustomerInEclipseStore, String>
{
}
