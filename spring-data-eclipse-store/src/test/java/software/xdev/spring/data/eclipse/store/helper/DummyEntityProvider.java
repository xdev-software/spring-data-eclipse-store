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
package software.xdev.spring.data.eclipse.store.helper;

import java.util.Collection;
import java.util.List;

import software.xdev.spring.data.eclipse.store.core.EntityProvider;
import software.xdev.spring.data.eclipse.store.repository.root.v2_4.EntityData;
import software.xdev.spring.data.eclipse.store.repository.root.v2_4.NonLazyEntityData;


public class DummyEntityProvider<T> extends EntityProvider<T, Void>
{
	public DummyEntityProvider(final Collection<T> collection)
	{
		super();
		final EntityData<T, Void> objects = new NonLazyEntityData<>();
		objects.setIdGetter(i -> null);
		collection.forEach(objects::ensureEntityAndReturnObjectsToStore);
		this.addEntityData(objects);
	}
	
	public static <E, Void> DummyEntityProvider<E> of(final E... entities)
	{
		return new DummyEntityProvider<>(List.of(entities));
	}
}
