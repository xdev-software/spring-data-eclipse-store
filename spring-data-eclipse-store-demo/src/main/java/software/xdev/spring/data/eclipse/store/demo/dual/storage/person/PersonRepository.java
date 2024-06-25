package software.xdev.spring.data.eclipse.store.demo.dual.storage.person;

import org.springframework.data.repository.CrudRepository;


public interface PersonRepository extends CrudRepository<Person, Integer>
{
}
