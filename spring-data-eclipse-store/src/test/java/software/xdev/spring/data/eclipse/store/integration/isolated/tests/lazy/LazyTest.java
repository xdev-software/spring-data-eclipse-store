/*
 * Copyright © 2023 XDEV Software (https://xdev.software)
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

import org.eclipse.serializer.collections.lazy.LazyArrayList;
import org.eclipse.serializer.collections.lazy.LazyList;
import org.eclipse.serializer.reference.Lazy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;
import software.xdev.spring.data.eclipse.store.repository.lazy.SpringDataEclipseStoreLazy;


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
	void lazyStoreComplexObject(@Autowired final ObjectWithLazyRepository<ComplexLazyObject> repository)
	{
		final ObjectWithLazy<ComplexLazyObject> newLazy = new ObjectWithLazy<>();
		final ComplexLazyObject objectToStore = new ComplexLazyObject(
			SpringDataEclipseStoreLazy.build(new SimpleObject(TestData.DUMMY_STRING)),
			new ArrayList<>(Arrays.asList(
				SpringDataEclipseStoreLazy.build(new ArrayList<>(Arrays.asList(TestData.DUMMY_STRING))),
				SpringDataEclipseStoreLazy.build(new ArrayList<>(Arrays.asList(TestData.DUMMY_STRING_ALTERNATIVE)))
			))
		);
		newLazy.setLazy(SpringDataEclipseStoreLazy.build(objectToStore));
		repository.save(newLazy);
		
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
	void lazyClearBeforeSave()
	{
		final ObjectWithLazy<SimpleObject> newLazy = new ObjectWithLazy<>();
		final SimpleObject objectToStore = new SimpleObject(TestData.DUMMY_STRING);
		newLazy.setLazy(SpringDataEclipseStoreLazy.build(objectToStore));
		Assertions.assertThrows(IllegalStateException.class, () -> newLazy.getLazy().clear());
	}
	
	@Test
	@Disabled("This should work at some point. At least a warning should be displayed.")
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
		repository.save(newLazy);
		newLazy.getLazy().clear();
		
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
}
