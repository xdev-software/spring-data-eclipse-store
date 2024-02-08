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

package software.xdev.spring.data.eclipse.store.jpa.integration;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import software.xdev.spring.data.eclipse.store.importer.EclipseStoreDataImporterComponent;
import software.xdev.spring.data.eclipse.store.jpa.DefaultTestAnnotations;
import software.xdev.spring.data.eclipse.store.jpa.integration.repository.PersonToTestInEclipseStore;
import software.xdev.spring.data.eclipse.store.jpa.integration.repository.PersonToTestInEclipseStoreRepository;
import software.xdev.spring.data.eclipse.store.jpa.integration.repository.PersonToTestInJpa;
import software.xdev.spring.data.eclipse.store.jpa.integration.repository.PersonToTestInJpaRepository;
import software.xdev.spring.data.eclipse.store.repository.support.SimpleEclipseStoreRepository;


@DefaultTestAnnotations
public class IntegrationTest
{
	@Autowired
	private PersonToTestInEclipseStoreRepository personToTestInEclipseStoreRepository;
	
	@Autowired
	private PersonToTestInJpaRepository personToTestInJpaRepository;
	
	@Autowired
	private EclipseStoreDataImporterComponent eclipseStoreDataImporter;
	
	/**
	 * Super simple test if there are any start-up errors when running parallel to a JPA configuration
	 */
	@Test
	void testBasicSaveAndFindSingleRecords()
	{
		final PersonToTestInEclipseStore customer = new PersonToTestInEclipseStore("", "");
		this.personToTestInEclipseStoreRepository.save(customer);
		
		final List<PersonToTestInEclipseStore> customers = this.personToTestInEclipseStoreRepository.findAll();
		Assertions.assertEquals(1, customers.size());
		Assertions.assertEquals(customer, customers.get(0));
	}
	
	@Test
	void testEclipseStoreImport()
	{
		final PersonToTestInJpa customer = new PersonToTestInJpa("", "");
		this.personToTestInJpaRepository.save(customer);
		
		final List<SimpleEclipseStoreRepository<?, ?>> simpleEclipseStoreRepositories =
			this.eclipseStoreDataImporter.importData();
		Assertions.assertEquals(1, simpleEclipseStoreRepositories.size());
		final List<?> allEntities = simpleEclipseStoreRepositories.get(0).findAll();
		Assertions.assertEquals(1, allEntities.size());
	}
}
