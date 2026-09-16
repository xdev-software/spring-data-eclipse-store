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

package software.xdev.spring.data.eclipse.store.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import software.xdev.spring.data.eclipse.store.repository.config.EnableEclipseStoreRepositories;


/**
 * In this example we want to coexist with Spring data JPA. This is possible by using
 * {@link software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository}s instead of the generic
 * {@link org.springframework.data.repository.Repository}s of the Spring framework.
 */
@SpringBootApplication
@EnableEclipseStoreRepositories
public class JpaDemoApplication implements CommandLineRunner
{
	private static final Logger LOG = LoggerFactory.getLogger(JpaDemoApplication.class);
	private final CustomerInEclipseStoreRepository eclipseStoreRepository;
	private final CustomerInJpaRepository jpaRepository;
	
	public JpaDemoApplication(
		final CustomerInEclipseStoreRepository eclipseStoreRepository,
		final CustomerInJpaRepository jpaRepository
	)
	{
		this.eclipseStoreRepository = eclipseStoreRepository;
		this.jpaRepository = jpaRepository;
	}
	
	public static void main(final String[] args)
	{
		SpringApplication.run(JpaDemoApplication.class, args);
	}
	
	@Override
	public void run(final String... args)
	{
		this.saveEntityInEclipseStoreRepository();
		this.saveEntityInJpaRepository();
	}
	
	private void saveEntityInEclipseStoreRepository()
	{
		LOG.info("-------- EclipseStore-Actions --------");
		this.eclipseStoreRepository.deleteAll();
		
		// save a couple of customers
		this.eclipseStoreRepository.save(new CustomerInEclipseStore("1", "Stevie", "Nicks"));
		this.eclipseStoreRepository.save(new CustomerInEclipseStore("2", "Mick", "Fleetwood"));
		
		// fetch all customers
		LOG.info("Customers found with findAll() in EclipseStore:");
		this.eclipseStoreRepository.findAll().forEach(c -> LOG.info(c.toString()));
	}
	
	private void saveEntityInJpaRepository()
	{
		LOG.info("-------- JPA-Actions --------");
		this.jpaRepository.deleteAll();
		
		// save a couple of customers
		this.jpaRepository.save(new CustomerInJpa("1", "Stevie", "Nicks"));
		this.jpaRepository.save(new CustomerInJpa("2", "Mick", "Fleetwood"));
		
		// fetch all customers
		LOG.info("Customers found with findAll() in JPA:");
		this.jpaRepository.findAll().forEach(c -> LOG.info(c.toString()));
	}
}
