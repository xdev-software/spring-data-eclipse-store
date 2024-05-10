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

package software.xdev.spring.data.eclipse.store.demo.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import software.xdev.spring.data.eclipse.store.repository.config.EnableEclipseStoreRepositories;


@SpringBootApplication
@EnableEclipseStoreRepositories
public class SimpleDemoApplication implements CommandLineRunner
{
	private static final Logger LOG = LoggerFactory.getLogger(SimpleDemoApplication.class);
	private final CustomerRepository customerRepository;
	private final PetRepository petRepository;
	
	public SimpleDemoApplication(
		final CustomerRepository customerRepository,
		final PetRepository petRepository
	)
	{
		this.customerRepository = customerRepository;
		this.petRepository = petRepository;
	}
	
	public static void main(final String[] args)
	{
		final ConfigurableApplicationContext run = SpringApplication.run(SimpleDemoApplication.class, args);
		run.close();
	}
	
	@Override
	public void run(final String... args)
	{
		this.customerRepository.deleteAll();
		
		// save a couple of customers
		this.customerRepository.save(new Customer("Stevie", "Nicks"));
		this.customerRepository.save(new Customer("Mick", "Fleetwood"));
		
		// fetch all customers
		LOG.info("Customers found with findAll():");
		this.customerRepository.findAll().forEach(c -> LOG.info(c.toString()));
		
		// save a pet
		this.petRepository.save(new Pet("Peter", 2));
		
		// fetch all pets
		LOG.info("Pets found with findAll():");
		this.petRepository.findAll().forEach(p -> LOG.info(p.toString()));
	}
}
