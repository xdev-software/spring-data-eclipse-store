/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package software.xdev.spring.data.eclipse.store.demo.complex;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.Pageable;

import software.xdev.spring.data.eclipse.store.demo.complex.owner.Owner;
import software.xdev.spring.data.eclipse.store.demo.complex.owner.OwnerRepository;
import software.xdev.spring.data.eclipse.store.demo.complex.owner.Pet;
import software.xdev.spring.data.eclipse.store.demo.complex.owner.PetType;
import software.xdev.spring.data.eclipse.store.demo.complex.owner.Visit;


@SpringBootApplication
public class ComplexDemoApplication implements CommandLineRunner
{
	private static final Logger LOG = LoggerFactory.getLogger(ComplexDemoApplication.class);
	private final OwnerRepository ownerRepository;
	private final VetService vetService;
	
	@Autowired
	public ComplexDemoApplication(
		final OwnerRepository ownerRepository,
		final VetService vetService
	)
	{
		this.ownerRepository = ownerRepository;
		this.vetService = vetService;
	}
	
	public static void main(final String[] args)
	{
		final ConfigurableApplicationContext run = SpringApplication.run(ComplexDemoApplication.class, args);
		run.close();
	}
	
	@Override
	public void run(final String... args)
	{
		this.transactionalVetCalls();
		
		this.ownerCalls();
	}
	
	private void ownerCalls()
	{
		LOG.info("----Owner-BeforeDeleteAll----");
		this.ownerRepository.findAll(Pageable.unpaged()).forEach(i -> LOG.info(i.toString()));
		this.ownerRepository.deleteAll();
		
		LOG.info("----Owner-AfterDeleteAll----");
		this.ownerRepository.findAll(Pageable.unpaged()).forEach(i -> LOG.info(i.toString()));
		
		final Owner owner = createOwner();
		this.ownerRepository.save(owner);
		
		LOG.info("----Owner-AfterSave----");
		this.ownerRepository.findAll(Pageable.unpaged()).forEach(i -> LOG.info(i.toString()));
		
		final Visit visit = createVisit();
		owner.addVisit("Peter", visit);
		this.ownerRepository.save(owner);
		
		LOG.info("----Owner-AfterVisit----");
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
	 * Each of these calls are one transaction.
	 */
	private void transactionalVetCalls()
	{
		this.vetService.logVetEntries();
		this.vetService.deleteAll();
		this.vetService.logVetEntries();
		this.vetService.saveNewEntries();
		this.vetService.logVetEntries();
	}
	
	private static Visit createVisit()
	{
		final Visit visit = new Visit();
		visit.setDate(LocalDate.now());
		visit.setDescription("Peter got his first parvovirus vaccine");
		return visit;
	}
	
	@SuppressWarnings("checkstyle:MagicNumber")
	private static Owner createOwner()
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
