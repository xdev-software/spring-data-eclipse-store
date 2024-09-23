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
package software.xdev.spring.data.eclipse.store.repository.support.reposyncer;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.serializer.util.traversing.ObjectGraphTraverser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.xdev.spring.data.eclipse.store.repository.root.EntityData;
import software.xdev.spring.data.eclipse.store.repository.root.RootDataV2;


public class SimpleRepositorySynchronizer implements RepositorySynchronizer
{
	private static final Logger LOG = LoggerFactory.getLogger(SimpleRepositorySynchronizer.class);
	private final RootDataV2 root;
	private final HashSet<EntityData<Object, Object>> listsToStore;
	private final ObjectGraphTraverser buildObjectGraphTraverser;
	
	public SimpleRepositorySynchronizer(final RootDataV2 root)
	{
		this.root = root;
		this.listsToStore = new HashSet<>();
		
		this.buildObjectGraphTraverser = ObjectGraphTraverser.Builder()
			.modeFull()
			.fieldPredicate(field -> !Modifier.isTransient(field.getModifiers()))
			.acceptorLogic(
				objectInGraph ->
				{
					if(objectInGraph == null)
					{
						return;
					}
					final Class<Object> objectInGraphClass = (Class<Object>)objectInGraph.getClass();
					final EntityData<Object, Object> entityDataForCurrentObject =
						this.root.getEntityData(objectInGraphClass);
					if(entityDataForCurrentObject != null
						&& !entityDataForCurrentObject.getEntities().contains(objectInGraph))
					{
						entityDataForCurrentObject.ensureEntityAndReturnObjectsToStore(objectInGraph);
						this.listsToStore.add(entityDataForCurrentObject);
					}
				}
			).buildObjectGraphTraverser();
	}
	
	@Override
	public Collection<EntityData<Object, Object>> syncAndReturnChangedObjectLists(final Object objectToStore)
	{
		this.listsToStore.clear();
		this.buildObjectGraphTraverser.traverse(objectToStore);
		if(LOG.isTraceEnabled())
		{
			LOG.trace("Amount of changed entities: {}", this.listsToStore.size());
		}
		final HashSet<EntityData<Object, Object>> setOfChangedObjects = new HashSet<>(this.listsToStore);
		this.listsToStore.clear();
		return setOfChangedObjects;
	}
}
