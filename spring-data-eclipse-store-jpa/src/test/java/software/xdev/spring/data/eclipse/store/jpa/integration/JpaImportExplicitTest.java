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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import software.xdev.spring.data.eclipse.store.importer.EclipseStoreDataImporter;
import software.xdev.spring.data.eclipse.store.jpa.integration.repository.PersonToTestInEclipseStoreRepository;
import software.xdev.spring.data.eclipse.store.jpa.integration.repository.PersonToTestInJpa;
import software.xdev.spring.data.eclipse.store.jpa.integration.repository.PersonToTestInJpaRepository;
import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;
import software.xdev.spring.data.eclipse.store.repository.support.SimpleEclipseStoreRepository;


@DefaultTestAnnotations
class JpaImportExplicitTest
{
	@Autowired
	private PersonToTestInEclipseStoreRepository personToTestInEclipseStoreRepository;
	
	@Autowired
	private PersonToTestInJpaRepository personToTestInJpaRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private EclipseStoreClientConfiguration configuration;
	
	@Test
	void testEclipseStoreImportExplicitNoComponent()
	{
		final PersonToTestInJpa customer = new PersonToTestInJpa("", "");
		this.personToTestInJpaRepository.save(customer);
		
		final List<SimpleEclipseStoreRepository<?, ?>> simpleEclipseStoreRepositories =
			new EclipseStoreDataImporter(this.configuration).importData(this.entityManager);
		Assertions.assertEquals(1, simpleEclipseStoreRepositories.size());
		final List<?> allEntities = simpleEclipseStoreRepositories.get(0).findAll();
		Assertions.assertEquals(1, allEntities.size());
		
		this.configuration.getStorageInstance().stop();
		Assertions.assertEquals(
			1,
			this.configuration.getStorageInstance().getNonLazyCommunicator().getEntityCount(PersonToTestInJpa.class),
			"After restart the imported entities are not there anymore.");
	}
}
