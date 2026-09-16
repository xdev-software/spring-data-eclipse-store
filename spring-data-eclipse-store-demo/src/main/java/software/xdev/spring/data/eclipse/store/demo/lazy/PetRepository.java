package software.xdev.spring.data.eclipse.store.demo.lazy;

import software.xdev.spring.data.eclipse.store.repository.interfaces.lazy.LazyEclipseStoreCrudRepository;


public interface PetRepository extends LazyEclipseStoreCrudRepository<Pet, String>
{
}
