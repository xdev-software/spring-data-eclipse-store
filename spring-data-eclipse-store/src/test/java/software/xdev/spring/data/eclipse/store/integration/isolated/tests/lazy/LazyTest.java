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

import org.eclipse.serializer.collections.lazy.LazyArrayList;
import org.eclipse.serializer.collections.lazy.LazyList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {LazyTestConfiguration.class})
class LazyTest
{
	@Autowired
	private LazyTestConfiguration configuration;
	
	@Test
	void lazyListStore(final ObjectWithLazyListRepository repository)
	{
		final ObjectWithLazyList newList = new ObjectWithLazyList();
		final SimpleObject objectToStore = new SimpleObject("Test");
		final LazyArrayList lazyArrayList = new LazyArrayList();
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
}
