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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.lazy;

import static software.xdev.spring.data.eclipse.store.helper.TestUtil.restartDatastore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.eclipse.serializer.collections.lazy.LazyArrayList;
import org.eclipse.serializer.collections.lazy.LazyList;
import org.eclipse.serializer.reference.Lazy;
import org.eclipse.serializer.reference.LazyReferenceManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.exceptions.NoIdFieldFoundException;
import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;
import software.xdev.spring.data.eclipse.store.repository.lazy.SpringDataEclipseStoreLazy;
import software.xdev.spring.data.eclipse.store.repository.root.v2_4.EntityData;
import software.xdev.spring.data.eclipse.store.repository.root.v2_4.LazyEntityData;


@SuppressWarnings("checkstyle:MethodName")
@IsolatedTestAnnotations
@ContextConfiguration(classes = {LazyTestConfiguration.class})
class LazyTest
{
	@Autowired
	private LazyTestConfiguration configuration;
	
	@Test
	@Disabled("This should work at some point. At least a warning should be displayed.")
	void lazyListStore(@Autowired final ObjectWithLazyListRepository repository)
	{
		final ObjectWithLazyList newList = new ObjectWithLazyList();
		final SimpleObject objectToStore = new SimpleObject(TestData.DUMMY_STRING);
		final LazyArrayList<SimpleObject> lazyArrayList = new LazyArrayList<>();
		lazyArrayList.add(objectToStore);
		newList.setLazyList(lazyArrayList);
		repository.save(newList);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				Assertions.assertEquals(1, repository.findAll().size());
				final LazyList<SimpleObject> loadedLazyList = repository.findAll().get(0).getLazyList();
				Assertions.assertEquals(1, loadedLazyList.size());
				Assertions.assertEquals(objectToStore, loadedLazyList.get(0));
			}
		);
	}
	
	@Test
	void lazyStore(@Autowired final ObjectWithLazyRepository<SimpleObject> repository)
	{
		final ObjectWithLazy<SimpleObject> newLazy = new ObjectWithLazy<>();
		final SimpleObject objectToStore = new SimpleObject(TestData.DUMMY_STRING);
		newLazy.setLazy(SpringDataEclipseStoreLazy.build(objectToStore));
		repository.save(newLazy);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				Assertions.assertEquals(1, repository.findAll().size());
				final Lazy<SimpleObject> lazy = repository.findAll().get(0).getLazy();
				Assertions.assertEquals(objectToStore, lazy.get());
			}
		);
	}
	
	@Test
	void lazyGetsCleared(@Autowired final ObjectWithLazyRepository<SimpleObject> repository)
	{
		final ObjectWithLazy<SimpleObject> newLazy = new ObjectWithLazy<>();
		final SimpleObject objectToStore = new SimpleObject(TestData.DUMMY_STRING);
		newLazy.setLazy(SpringDataEclipseStoreLazy.build(objectToStore));
		repository.save(newLazy);
		
		restartDatastore(this.configuration);
		
		final List<ObjectWithLazy<SimpleObject>> loadedObjects = repository.findAll();
		Assertions.assertEquals(1, loadedObjects.size());
		Assertions.assertFalse(loadedObjects.get(0).getLazy().isLoaded());
		Assertions.assertNotNull(loadedObjects.get(0).getLazy().get());
		Assertions.assertTrue(loadedObjects.get(0).getLazy().isLoaded());
		
		LazyReferenceManager.get().clear();
		Assertions.assertFalse(repository.findAll().get(0).getLazy().isLoaded());
	}
	
	@Test
	void lazyWorkingCopyIsCreated(@Autowired final ObjectWithLazyRepository<SimpleObject> repository)
	{
		final ObjectWithLazy<SimpleObject> newLazy = new ObjectWithLazy<>();
		final SimpleObject objectToStore = new SimpleObject(TestData.DUMMY_STRING);
		newLazy.setLazy(SpringDataEclipseStoreLazy.build(objectToStore));
		repository.save(newLazy);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final SimpleObject storedObject = newLazy.getLazy().get();
				final SimpleObject loadedObject = repository.findAll().get(0).getLazy().get();
				Assertions.assertNotSame(storedObject, loadedObject);
				Assertions.assertEquals(storedObject, loadedObject);
			}
		);
	}
	
	@Test
	void lazyDifferentWorkingCopiesAreCreated(@Autowired final ObjectWithLazyRepository<SimpleObject> repository)
	{
		final ObjectWithLazy<SimpleObject> newLazy = new ObjectWithLazy<>();
		final SimpleObject objectToStore = new SimpleObject(TestData.DUMMY_STRING);
		newLazy.setLazy(SpringDataEclipseStoreLazy.build(objectToStore));
		repository.save(newLazy);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final ObjectWithLazy<SimpleObject> workingCopy1 = repository.findAll().get(0);
				final ObjectWithLazy<SimpleObject> workingCopy2 = repository.findAll().get(0);
				Assertions.assertNotSame(workingCopy1.getLazy(), workingCopy2.getLazy());
				Assertions.assertNotSame(workingCopy1.getLazy().get(), workingCopy2.getLazy().get());
				Assertions.assertEquals(workingCopy1.getLazy().get(), workingCopy2.getLazy().get());
			}
		);
	}
	
	@Test
	void lazyWorkingCopyChange(@Autowired final ObjectWithLazyRepository<SimpleObject> repository)
	{
		final ObjectWithLazy<SimpleObject> newLazy = new ObjectWithLazy<>();
		final SimpleObject objectToStore = new SimpleObject(TestData.DUMMY_STRING);
		newLazy.setLazy(SpringDataEclipseStoreLazy.build(objectToStore));
		repository.save(newLazy);
		
		final SimpleObject changedObjectToStore = new SimpleObject(TestData.DUMMY_STRING_ALTERNATIVE);
		newLazy.setLazy(SpringDataEclipseStoreLazy.build(changedObjectToStore));
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				Assertions.assertNotEquals(newLazy.getLazy().get(), repository.findAll().get(0).getLazy().get());
			}
		);
	}
	
	@Test
	void lazyWorkingCopyChangeAfterRestart(@Autowired final ObjectWithLazyRepository<SimpleObject> repository)
	{
		final ObjectWithLazy<SimpleObject> newLazy = new ObjectWithLazy<>();
		final SimpleObject objectToStore = new SimpleObject(TestData.DUMMY_STRING);
		newLazy.setLazy(SpringDataEclipseStoreLazy.build(objectToStore));
		repository.save(newLazy);
		
		restartDatastore(this.configuration);
		
		final SimpleObject changedObjectToStore = new SimpleObject(TestData.DUMMY_STRING_ALTERNATIVE);
		final ObjectWithLazy<SimpleObject> objectToChange = repository.findAll().get(0);
		objectToChange.setLazy(SpringDataEclipseStoreLazy.build(changedObjectToStore));
		
		Assertions.assertNotEquals(objectToChange.getLazy().get(), repository.findAll().get(0).getLazy().get());
		
		repository.save(objectToChange);
		
		final ObjectWithLazy<SimpleObject> workingCopy1 = repository.findAll().get(0);
		final ObjectWithLazy<SimpleObject> workingCopy2 = repository.findAll().get(0);
		Assertions.assertEquals(workingCopy1.getLazy().get(), workingCopy2.getLazy().get());
	}
	
	@Test
	void lazyStoreComplexObject(@Autowired final ObjectWithLazyRepository<ComplexLazyObject> repository)
	{
		final ComplexLazyObject objectToStore =
			prepareLazyComplexObject(repository);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				Assertions.assertEquals(1, repository.findAll().size());
				final Lazy<ComplexLazyObject> lazy = repository.findAll().get(0).getLazy();
				Assertions.assertNotNull(lazy.get());
				Assertions.assertEquals(objectToStore.getSimpleObject().get(), lazy.get().getSimpleObject().get());
				Assertions.assertEquals(
					objectToStore.getListOfLazyListOfString().size(),
					lazy.get().getListOfLazyListOfString().size());
				Assertions.assertEquals(
					objectToStore.getListOfLazyListOfString().get(0).get(),
					lazy.get().getListOfLazyListOfString().get(0).get());
				Assertions.assertEquals(
					objectToStore.getListOfLazyListOfString().get(1).get(),
					lazy.get().getListOfLazyListOfString().get(1).get());
			}
		);
	}
	
	@Test
	void lazyChangeComplexObject(@Autowired final ObjectWithLazyRepository<ComplexLazyObject> repository)
	{
		prepareLazyComplexObject(repository);
		
		restartDatastore(this.configuration);
		
		final ObjectWithLazy<ComplexLazyObject> loadedObjectToChange = repository.findAll().get(0);
		loadedObjectToChange
			.getLazy()
			.get()
			.getListOfLazyListOfString()
			.add(SpringDataEclipseStoreLazy.build(List.of(TestData.DUMMY_STRING_ALTERNATIVE)));
		repository.save(loadedObjectToChange);
		
		this.validateLazyComplexObject(repository, loadedObjectToChange);
	}
	
	@Test
	void lazyReloadAndRestoreComplexObject(@Autowired final ObjectWithLazyRepository<ComplexLazyObject> repository)
	{
		prepareLazyComplexObject(repository);
		
		restartDatastore(this.configuration);
		
		final ObjectWithLazy<ComplexLazyObject> loadedObjectToChange = repository.findAll().get(0);
		repository.save(loadedObjectToChange);
		
		this.validateLazyComplexObject(repository, loadedObjectToChange);
	}
	
	private static ComplexLazyObject prepareLazyComplexObject(
		final ObjectWithLazyRepository<ComplexLazyObject> repository)
	{
		final ObjectWithLazy<ComplexLazyObject> newLazy = new ObjectWithLazy<>();
		final ComplexLazyObject objectToStore = new ComplexLazyObject(
			SpringDataEclipseStoreLazy.build(new SimpleObject(TestData.DUMMY_STRING)),
			new ArrayList<>(Arrays.asList(
				SpringDataEclipseStoreLazy.build(new ArrayList<>(List.of(TestData.DUMMY_STRING))),
				SpringDataEclipseStoreLazy.build(new ArrayList<>(List.of(TestData.DUMMY_STRING_ALTERNATIVE)))
			))
		);
		newLazy.setLazy(SpringDataEclipseStoreLazy.build(objectToStore));
		repository.save(newLazy);
		
		return objectToStore;
	}
	
	private void validateLazyComplexObject(
		final ObjectWithLazyRepository<ComplexLazyObject> repository,
		final ObjectWithLazy<ComplexLazyObject> loadedObjectToChange)
	{
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final ObjectWithLazy<ComplexLazyObject> loadedObject = repository.findAll().get(0);
				Assertions.assertNotSame(loadedObjectToChange, loadedObject);
				Assertions.assertNotSame(
					loadedObjectToChange.getLazy().get(),
					loadedObject.getLazy().get()
				);
				Assertions.assertEquals(
					loadedObjectToChange.getLazy().get().getListOfLazyListOfString().size(),
					loadedObject.getLazy().get().getListOfLazyListOfString().size()
				);
				Assertions.assertEquals(
					loadedObjectToChange.getLazy().get().getListOfLazyListOfString().get(0).get().size(),
					loadedObject.getLazy().get().getListOfLazyListOfString().get(0).get().size()
				);
				Assertions.assertEquals(
					loadedObjectToChange.getLazy().get().getListOfLazyListOfString().get(0).get().get(0),
					loadedObject.getLazy().get().getListOfLazyListOfString().get(0).get().get(0)
				);
			}
		);
	}
	
	@Test
	void lazyClearBeforeSave()
	{
		final ObjectWithLazy<SimpleObject> newLazy = new ObjectWithLazy<>();
		final SimpleObject objectToStore = new SimpleObject(TestData.DUMMY_STRING);
		newLazy.setLazy(SpringDataEclipseStoreLazy.build(objectToStore));
		final Lazy<SimpleObject> lazy = newLazy.getLazy();
		Assertions.assertThrows(IllegalStateException.class, () -> lazy.clear());
	}
	
	@Test
	void lazyUseEclipseStoreLazy(@Autowired final ObjectWithLazyRepository<SimpleObject> repository)
	{
		final ObjectWithLazy<SimpleObject> newLazy = new ObjectWithLazy<>();
		final SimpleObject objectToStore = new SimpleObject(TestData.DUMMY_STRING);
		newLazy.setLazy(Lazy.Reference(objectToStore));
		Assertions.assertThrows(Exception.class, () -> repository.save(newLazy));
	}
	
	@Test
	void lazyClearAfterSave(@Autowired final ObjectWithLazyRepository<SimpleObject> repository)
	{
		final ObjectWithLazy<SimpleObject> newLazy = new ObjectWithLazy<>();
		final SimpleObject objectToStore = new SimpleObject(TestData.DUMMY_STRING);
		newLazy.setLazy(SpringDataEclipseStoreLazy.build(objectToStore));
		
		Assertions.assertTrue(newLazy.getLazy().isLoaded());
		Assertions.assertFalse(newLazy.getLazy().isStored());
		
		repository.save(newLazy);
		
		Assertions.assertTrue(newLazy.getLazy().isLoaded());
		Assertions.assertTrue(newLazy.getLazy().isStored());
		
		newLazy.getLazy().clear();
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				Assertions.assertEquals(1, repository.findAll().size());
				final Lazy<SimpleObject> lazy = repository.findAll().get(0).getLazy();
				Assertions.assertFalse(lazy.isLoaded());
				Assertions.assertEquals(objectToStore, lazy.get());
				Assertions.assertTrue(lazy.isLoaded());
			}
		);
	}
	
	@Test
	@Disabled("It's unclear why this is sometimes not working. Seems to be an EclipseStore issue.")
	void lazyClearThroughLazyManagerBeforeSave()
	{
		LazyReferenceManager.get().stop();
		this.configuration.getStorageInstance().start();
		final ObjectWithLazy<SimpleObject> newLazy = new ObjectWithLazy<>();
		final SimpleObject objectToStore = new SimpleObject(TestData.DUMMY_STRING);
		newLazy.setLazy(SpringDataEclipseStoreLazy.build(objectToStore));
		Assertions.assertTrue(newLazy.getLazy().isLoaded());
		Assertions.assertFalse(newLazy.getLazy().isStored());
		LazyReferenceManager.get().cleanUp();
		Assertions.assertTrue(newLazy.getLazy().isLoaded());
		Assertions.assertFalse(newLazy.getLazy().isStored());
	}
	
	@Test
	void lazyClearThroughLazyManagerAfterSave(@Autowired final ObjectWithLazyRepository<SimpleObject> repository)
	{
		final ObjectWithLazy<SimpleObject> newLazy = new ObjectWithLazy<>();
		final SimpleObject objectToStore = new SimpleObject(TestData.DUMMY_STRING);
		newLazy.setLazy(SpringDataEclipseStoreLazy.build(objectToStore));
		repository.save(newLazy);
		LazyReferenceManager.get().cleanUp();
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				Assertions.assertEquals(1, repository.findAll().size());
				final Lazy<SimpleObject> lazy = repository.findAll().get(0).getLazy();
				Assertions.assertFalse(lazy.isLoaded());
				Assertions.assertEquals(objectToStore, lazy.get());
				Assertions.assertTrue(lazy.isLoaded());
			}
		);
	}
	
	@Test
	void lazyClearAfterRestart(@Autowired final ObjectWithLazyRepository<SimpleObject> repository)
	{
		final ObjectWithLazy<SimpleObject> newLazy = new ObjectWithLazy<>();
		final SimpleObject objectToStore = new SimpleObject(TestData.DUMMY_STRING);
		newLazy.setLazy(SpringDataEclipseStoreLazy.build(objectToStore));
		repository.save(newLazy);
		
		restartDatastore(this.configuration);
		
		Assertions.assertDoesNotThrow(() -> newLazy.getLazy().clear());
	}
	
	@Test
	void lazyChangeAfterSave(@Autowired final ObjectWithLazyRepository<SimpleObject> repository)
	{
		final ObjectWithLazy<SimpleObject> newLazy = new ObjectWithLazy<>();
		final SimpleObject objectToStore = new SimpleObject(TestData.DUMMY_STRING);
		newLazy.setLazy(SpringDataEclipseStoreLazy.build(objectToStore));
		repository.save(newLazy);
		
		final SimpleObject objectToStore2 = new SimpleObject(TestData.DUMMY_STRING_ALTERNATIVE);
		newLazy.setLazy(SpringDataEclipseStoreLazy.build(objectToStore2));
		repository.save(newLazy);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				Assertions.assertEquals(1, repository.findAll().size());
				final Lazy<SimpleObject> lazy = repository.findAll().get(0).getLazy();
				Assertions.assertEquals(objectToStore2, lazy.get());
			}
		);
	}
	
	@Test
	void lazyChangeBeforeSave(@Autowired final ObjectWithLazyRepository<SimpleObject> repository)
	{
		final ObjectWithLazy<SimpleObject> newLazy = new ObjectWithLazy<>();
		final SimpleObject objectToStore = new SimpleObject(TestData.DUMMY_STRING);
		newLazy.setLazy(SpringDataEclipseStoreLazy.build(objectToStore));
		
		final SimpleObject objectToStore2 = new SimpleObject(TestData.DUMMY_STRING_ALTERNATIVE);
		newLazy.setLazy(SpringDataEclipseStoreLazy.build(objectToStore2));
		repository.save(newLazy);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				Assertions.assertEquals(1, repository.findAll().size());
				final Lazy<SimpleObject> lazy = repository.findAll().get(0).getLazy();
				Assertions.assertEquals(objectToStore2, lazy.get());
			}
		);
	}
	
	@Test
	void lazyChangeAfterRestart(@Autowired final ObjectWithLazyRepository<SimpleObject> repository)
	{
		final ObjectWithLazy<SimpleObject> newLazy = new ObjectWithLazy<>();
		final SimpleObject objectToStore = new SimpleObject(TestData.DUMMY_STRING);
		newLazy.setLazy(SpringDataEclipseStoreLazy.build(objectToStore));
		repository.save(newLazy);
		
		restartDatastore(this.configuration);
		
		Assertions.assertEquals(1, repository.findAll().size());
		final ObjectWithLazy<SimpleObject> reloadedObjectWithLazy = repository.findAll().get(0);
		Assertions.assertEquals(objectToStore, reloadedObjectWithLazy.getLazy().get());
		
		final SimpleObject objectToStore2 = new SimpleObject(TestData.DUMMY_STRING_ALTERNATIVE);
		reloadedObjectWithLazy.setLazy(SpringDataEclipseStoreLazy.build(objectToStore2));
		repository.save(reloadedObjectWithLazy);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				Assertions.assertEquals(1, repository.findAll().size());
				final Lazy<SimpleObject> lazy = repository.findAll().get(0).getLazy();
				Assertions.assertEquals(objectToStore2, lazy.get());
			}
		);
	}
	
	@Test
	void simpleEntityWithIdLazyWrappedRepository_PersistetAfterRestart(
		@Autowired final SimpleEntityWithIdLazyWrappedRepository repository)
	{
		final SimpleEntityWithId objectToStore = new SimpleEntityWithId(TestData.DUMMY_STRING);
		final SpringDataEclipseStoreLazy.Default<SimpleEntityWithId> lazyObject =
			SpringDataEclipseStoreLazy.build(objectToStore);
		repository.save(lazyObject);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				Assertions.assertEquals(1, repository.findAll().size());
				final Lazy<SimpleEntityWithId> reloadedObject = repository.findAll().get(0);
				Assertions.assertEquals(objectToStore.getId(), reloadedObject.get().getId());
				Assertions.assertEquals(objectToStore.getName(), reloadedObject.get().getName());
			}
		);
	}
	
	@Test
	void simpleEntityWithIdLazyWrappedRepository_OnlyLoadOnDemand(
		@Autowired final SimpleEntityWithIdLazyWrappedRepository repository)
	{
		final SimpleEntityWithId objectToStore = new SimpleEntityWithId(TestData.DUMMY_STRING);
		final SpringDataEclipseStoreLazy.Default<SimpleEntityWithId> lazyObject =
			SpringDataEclipseStoreLazy.build(objectToStore);
		repository.save(lazyObject);
		
		restartDatastore(this.configuration);
		
		final Lazy<SimpleEntityWithId> reloadedObject = repository.findAll().get(0);
		Assertions.assertFalse(reloadedObject.isLoaded());
		Assertions.assertEquals(objectToStore.getId(), reloadedObject.get().getId());
		Assertions.assertEquals(objectToStore.getName(), reloadedObject.get().getName());
		Assertions.assertTrue(reloadedObject.isLoaded());
	}
	
	@Test
	void simpleEntityWithIdLazyWrappedRepository_FindById(
		@Autowired final SimpleEntityWithIdLazyWrappedRepository repository)
	{
		final SimpleEntityWithId objectToStore1 = new SimpleEntityWithId(TestData.DUMMY_STRING);
		repository.save(SpringDataEclipseStoreLazy.build(objectToStore1));
		
		final List<Lazy<SimpleEntityWithId>> all = repository.findAll();
		
		Assertions.assertThrows(NoIdFieldFoundException.class, () -> repository.findById(all.get(0).get().getId()));
	}
	
	@Test
	void simpleEntityWithIdLazyRepository_FindById(@Autowired final SimpleEntityWithIdLazyRepository repository)
	{
		final SimpleEntityWithId objectToStore1 = new SimpleEntityWithId(TestData.DUMMY_STRING);
		final Long object1Id = repository.save(objectToStore1).getId();
		final SimpleEntityWithId objectToStore2 = new SimpleEntityWithId(TestData.DUMMY_STRING);
		final Long object2Id = repository.save(objectToStore2).getId();
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				LazyReferenceManager.get().cleanUp();
				final Optional<SimpleEntityWithId> reloadedObject = repository.findById(object1Id);
				Assertions.assertTrue(reloadedObject.isPresent());
			}
		);
		final EntityData<SimpleEntityWithId, Long> entityData = this.configuration.getStorageInstance()
			.getRoot()
			.getCurrentRootData()
			.getEntityData(SimpleEntityWithId.class);
		final HashMap<Long, Lazy<SimpleEntityWithId>> lazyEntitiesById =
			((LazyEntityData<SimpleEntityWithId, Long>)entityData).getNativeLazyEntitiesById();
		Assertions.assertTrue(lazyEntitiesById.get(object1Id).isLoaded());
		Assertions.assertFalse(lazyEntitiesById.get(object2Id).isLoaded());
	}
}
