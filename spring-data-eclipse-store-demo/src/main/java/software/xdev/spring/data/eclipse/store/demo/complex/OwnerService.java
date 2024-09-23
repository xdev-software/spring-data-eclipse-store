package software.xdev.spring.data.eclipse.store.demo.complex;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import software.xdev.spring.data.eclipse.store.demo.complex.owner.Owner;
import software.xdev.spring.data.eclipse.store.demo.complex.owner.OwnerRepository;
import software.xdev.spring.data.eclipse.store.demo.complex.owner.Pet;
import software.xdev.spring.data.eclipse.store.demo.complex.owner.PetType;
import software.xdev.spring.data.eclipse.store.demo.complex.owner.Visit;


@Service
public class OwnerService
{
	private static final Logger LOG = LoggerFactory.getLogger(OwnerService.class);
	private final OwnerRepository ownerRepository;
	private final PlatformTransactionManager transactionManager;
	
	@Autowired
	public OwnerService(final OwnerRepository ownerRepository, final PlatformTransactionManager transactionManager)
	{
		this.ownerRepository = ownerRepository;
		this.transactionManager = transactionManager;
	}
	
	/**
	 * Non transactional
	 */
	public void logOwners()
	{
		LOG.info("----All current stored owners----");
		this.ownerRepository.findAll(Pageable.unpaged()).forEach(i -> LOG.info(i.toString()));
	}
	
	/**
	 * Transactional
	 */
	public void deleteAll()
	{
		new TransactionTemplate(this.transactionManager).execute(
			status ->
			{
				this.ownerRepository.deleteAll();
				LOG.info("----Deleted all owners----");
				return null;
			});
	}
	
	/**
	 * Non transactional
	 */
	public void logOwnersAndVisits()
	{
		LOG.info("----All owners with last name Nicks----");
		this.ownerRepository
			.findByLastName("Nicks", Pageable.unpaged())
			.forEach(i ->
				{
					LOG.info(i.toString());
					i.getPets().forEach(p -> {
							LOG.info(p.toString());
							p.getVisits().forEach(v -> LOG.info(v.toString()));
						}
					);
				}
			);
		
		LOG.info("----Owner-Lazy Pet loading----");
		this.ownerRepository.findAll().forEach(
			o -> o.getPets().forEach(
				pet -> LOG.info(String.format(
					"Pet %s has owner %s %s",
					pet.getName(),
					o.getFirstName(),
					o.getLastName()))
			)
		);
	}
	
	/**
	 * Transactional
	 */
	public void createNewOwnerAndVisit()
	{
		new TransactionTemplate(this.transactionManager).execute(
			status ->
			{
				final Owner owner = this.createOwner();
				this.ownerRepository.save(owner);
				
				final Visit visit = this.createVisit();
				owner.addVisit("Peter", visit);
				this.ownerRepository.save(owner);
				LOG.info("----Stored new owner and visit----");
				return null;
			});
	}
	
	private Visit createVisit()
	{
		final Visit visit = new Visit();
		visit.setDate(LocalDate.now());
		visit.setDescription("Peter got his first parvovirus vaccine");
		return visit;
	}
	
	@SuppressWarnings("checkstyle:MagicNumber")
	private Owner createOwner()
	{
		final Owner owner = new Owner();
		owner.setFirstName("Stevie");
		owner.setLastName("Nicks");
		final Pet pet = new Pet();
		pet.setBirthDate(LocalDate.now().minusWeeks(6));
		pet.setName("Peter");
		final PetType petType = new PetType();
		petType.setName("Dog");
		pet.setType(petType);
		owner.addPet(pet);
		return owner;
	}
}
