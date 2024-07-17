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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.version;

import java.util.List;

import jakarta.persistence.OptimisticLockException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {VersionTestConfiguration.class})
class VersionTest
{
	private final VersionTestConfiguration configuration;
	
	@Autowired
	public VersionTest(final VersionTestConfiguration configuration)
	{
		this.configuration = configuration;
	}
	
	@Test
	void simpleSave(@Autowired final VersionedEntityWithIntegerRepository repository)
	{
		final VersionedEntityWithInteger entity = new VersionedEntityWithInteger(TestData.FIRST_NAME);
		repository.save(entity);
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<VersionedEntityWithInteger> allEntities = repository.findAll();
				Assertions.assertEquals(1, allEntities.size());
				Assertions.assertEquals(1, allEntities.get(0).getVersion());
			}
		);
	}
	
	@Test
	void doubleSave(@Autowired final VersionedEntityWithIntegerRepository repository)
	{
		final VersionedEntityWithInteger entity = new VersionedEntityWithInteger(TestData.FIRST_NAME);
		repository.save(entity);
		repository.save(repository.findAll().get(0));
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<VersionedEntityWithInteger> allEntities = repository.findAll();
				Assertions.assertEquals(1, allEntities.size());
				Assertions.assertEquals(2, allEntities.get(0).getVersion());
			}
		);
	}
	
	@Test
	void saveButLocked(@Autowired final VersionedEntityWithIntegerRepository repository)
	{
		final VersionedEntityWithInteger entity = new VersionedEntityWithInteger(TestData.FIRST_NAME);
		repository.save(entity);
		
		final VersionedEntityWithInteger firstLoadedEntry = repository.findAll().get(0);
		final VersionedEntityWithInteger secondLoadedEntry = repository.findAll().get(0);
		
		firstLoadedEntry.setName(TestData.FIRST_NAME_ALTERNATIVE);
		repository.save(firstLoadedEntry);
		
		Assertions.assertThrows(OptimisticLockException.class, () -> repository.save(secondLoadedEntry));
	}
	
	@Test
	void simpleSaveWithChild(@Autowired final VersionedEntityWithIntegerAndVersionedChildRepository repository)
	{
		final VersionedChildEntityWithLong child = new VersionedChildEntityWithLong(TestData.FIRST_NAME);
		final VersionedEntityWithIntegerAndVersionedChild entity =
			new VersionedEntityWithIntegerAndVersionedChild(TestData.FIRST_NAME_ALTERNATIVE, child);
		repository.save(entity);
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<VersionedEntityWithIntegerAndVersionedChild> allEntities = repository.findAll();
				Assertions.assertEquals(1, allEntities.size());
				Assertions.assertEquals(1, allEntities.get(0).getVersion());
				Assertions.assertEquals(1L, allEntities.get(0).getChild().getVersion());
			}
		);
	}
	
	@Test
	void doubleSaveWithChild(@Autowired final VersionedEntityWithIntegerAndVersionedChildRepository repository)
	{
		final VersionedChildEntityWithLong child = new VersionedChildEntityWithLong(TestData.FIRST_NAME);
		final VersionedEntityWithIntegerAndVersionedChild entity =
			new VersionedEntityWithIntegerAndVersionedChild(TestData.FIRST_NAME_ALTERNATIVE, child);
		repository.save(entity);
		repository.save(repository.findAll().get(0));
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<VersionedEntityWithIntegerAndVersionedChild> allEntities = repository.findAll();
				Assertions.assertEquals(1, allEntities.size());
				Assertions.assertEquals(2, allEntities.get(0).getVersion());
				Assertions.assertEquals(2L, allEntities.get(0).getChild().getVersion());
			}
		);
	}
	
	@Test
	void saveButLockedWithChild(@Autowired final VersionedEntityWithIntegerAndVersionedChildRepository repository)
	{
		final VersionedChildEntityWithLong child = new VersionedChildEntityWithLong(TestData.FIRST_NAME);
		final VersionedEntityWithIntegerAndVersionedChild entity =
			new VersionedEntityWithIntegerAndVersionedChild(TestData.FIRST_NAME_ALTERNATIVE, child);
		repository.save(entity);
		
		final VersionedEntityWithIntegerAndVersionedChild firstLoadedEntry = repository.findAll().get(0);
		final VersionedEntityWithIntegerAndVersionedChild secondLoadedEntry = repository.findAll().get(0);
		
		firstLoadedEntry.getChild().setName(TestData.FIRST_NAME_ALTERNATIVE);
		repository.save(firstLoadedEntry);
		
		Assertions.assertThrows(OptimisticLockException.class, () -> repository.save(secondLoadedEntry));
	}
	
	@Test
	void saveButLockedWithSameChild(@Autowired final VersionedEntityWithIntegerAndVersionedChildRepository repository)
	{
		final VersionedChildEntityWithLong child = new VersionedChildEntityWithLong(TestData.FIRST_NAME);
		final VersionedEntityWithIntegerAndVersionedChild entity =
			new VersionedEntityWithIntegerAndVersionedChild(TestData.FIRST_NAME_ALTERNATIVE, child);
		repository.save(entity);
		
		final VersionedEntityWithIntegerAndVersionedChild firstLoadedEntry = repository.findAll().get(0);
		final VersionedEntityWithIntegerAndVersionedChild secondLoadedEntry = repository.findAll().get(0);
		
		firstLoadedEntry.setChild(secondLoadedEntry.getChild());
		repository.save(firstLoadedEntry);
		
		Assertions.assertThrows(OptimisticLockException.class, () -> repository.save(secondLoadedEntry));
	}
}
