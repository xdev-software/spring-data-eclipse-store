/*
 * Copyright © 2024 XDEV Software (https://xdev.software)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.deletion;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;

@IsolatedTestAnnotations
@ContextConfiguration(classes = {DeletionTestConfiguration.class})
class DeletionTest
{
	@Autowired
	private DeletionTestConfiguration configuration;
	
	/**
	 * JPA would throw a {@code JdbcSQLIntegrityConstraintViolationException} but it would be very expensive to search
	 * the referencedObject in the complete object tree and throw the exception.
	 * <p>
	 * That is why this library simply removes the element from the repository, but if it is still referenced in
	 * another
	 * object, this reference is still working and pointing to the object. That means that in fact this the object to
	 * remove could very well stay in the storage if it is referenced.
	 * </p>
	 */
	@Test
	void deleteReferencedObject(
		@Autowired final ReferencedRepository referencedRepository,
		@Autowired final ReferencingRepository referencingRepository)
	{
		final ReferencedDaoObject referencedObject = new ReferencedDaoObject("someValue");
		final ReferencingDaoObject referencingDaoObject = new ReferencingDaoObject(referencedObject);
		referencingRepository.save(referencingDaoObject);
		
		Assertions.assertEquals(1, referencingRepository.findAll().size());
		Assertions.assertEquals(1, referencedRepository.findAll().size());
		
		referencedRepository.delete(referencedObject);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				Assertions.assertTrue(referencedRepository.findAll().isEmpty());
				final List<ReferencingDaoObject> referencingDaoObjects = referencingRepository.findAll();
				Assertions.assertEquals(1, referencingDaoObjects.size());
				Assertions.assertNotNull(referencingDaoObjects.get(0).getValue());
			}
		);
	}
	
	@Test
	void restoreDeletedReferencedObject(
		@Autowired final ReferencedRepository referencedRepository,
		@Autowired final ReferencingRepository referencingRepository)
	{
		final ReferencedDaoObject referencedObject = new ReferencedDaoObject("someValue");
		final ReferencingDaoObject referencingDaoObject = new ReferencingDaoObject(referencedObject);
		referencingRepository.save(referencingDaoObject);
		
		referencedRepository.delete(referencedObject);
		
		referencingRepository.save(referencingDaoObject);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				Assertions.assertEquals(1, referencingRepository.findAll().size());
				Assertions.assertEquals(1, referencedRepository.findAll().size());
			}
		);
	}
}
