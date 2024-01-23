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
package software.xdev.spring.data.eclipse.store.repository.support.reposyncer;

import java.util.Collection;

import software.xdev.spring.data.eclipse.store.core.IdentitySet;


/**
 * Since EclipseStore is not table-oriented, but the access through the repositories is, we must keep the lists/tables of
 * entities in sync with the existing entities in the graph.
 * <p>
 * With every store we must check, if there is a new entity in the graph, that isn't yet in the entity-list.
 */
public interface RepositorySynchronizer
{
	/**
	 * @return a list of entity-lists that have been changed and now must be stored.
	 */
	Collection<IdentitySet<Object>> syncAndReturnChangedObjectLists(Object objectToStore);
}
