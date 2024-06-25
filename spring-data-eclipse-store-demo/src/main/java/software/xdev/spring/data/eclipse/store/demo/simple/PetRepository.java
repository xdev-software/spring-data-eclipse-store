package software.xdev.spring.data.eclipse.store.demo.simple;

import org.springframework.data.repository.CrudRepository;


public interface PetRepository extends CrudRepository<Pet, String>
{
}
