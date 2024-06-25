package software.xdev.spring.data.eclipse.store.jpa;

import org.springframework.data.jpa.repository.JpaRepository;


/**
 * To be able to properly coexist with JPA in one project, the repositories must be declared as specific JPA- or
 * EclipseStore-Repositories.
 */
public interface CustomerInJpaRepository extends JpaRepository<CustomerInJpa, String>
{
}
