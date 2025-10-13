package software.xdev.spring.data.eclipse.store.demo.complex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import software.xdev.spring.data.eclipse.store.demo.complex.vet.Specialty;
import software.xdev.spring.data.eclipse.store.demo.complex.vet.Vet;
import software.xdev.spring.data.eclipse.store.demo.complex.vet.VetRepository;


@Service
@Transactional
public class VetService
{
	private static final Logger LOG = LoggerFactory.getLogger(VetService.class);
	private final VetRepository vetRepository;
	
	@Autowired
	public VetService(final VetRepository vetRepository)
	{
		this.vetRepository = vetRepository;
	}
	
	public void deleteAll()
	{
		this.vetRepository.deleteAll();
		LOG.info("----Deleted all vets----");
	}
	
	public void saveNewEntries()
	{
		final Vet vet = this.createVet();
		this.vetRepository.save(vet);
		LOG.info("----Stored new vet----");
	}
	
	public void logVetEntries()
	{
		LOG.info("----All current stored vets----");
		this.vetRepository.findAll().forEach(i -> LOG.info(i.toString()));
	}
	
	private Vet createVet()
	{
		final Vet vet = new Vet();
		vet.setFirstName("Mick");
		vet.setLastName("Fleetwood");
		final Specialty specialty = new Specialty();
		specialty.setName("Vaccination");
		vet.addSpecialty(specialty);
		return vet;
	}
}
