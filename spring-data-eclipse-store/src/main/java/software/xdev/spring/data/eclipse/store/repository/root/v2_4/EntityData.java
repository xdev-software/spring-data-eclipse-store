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
package software.xdev.spring.data.eclipse.store.repository.root.v2_4;

import java.util.Collection;
import java.util.stream.Stream;

import software.xdev.spring.data.eclipse.store.repository.support.id.IdGetter;


/**
 * @param <T>  type of entity to store
 * @param <ID> type of id of the entity to store. Can be {@link Void} if no ID is used.
 */
public interface EntityData<T, ID>
{
	
	/**
	 * Accepts {@code null} if no id field is defined
	 */
	void setIdGetter(final IdGetter<T, ID> idGetter);
	
	Stream<T> getEntitiesAsStream();
	
	boolean containsEntity(final T entity);
	
	ID getLastId();
	
	long getEntityCount();
	
	void setLastId(final Object lastId);
	
	Collection<Object> ensureEntityAndReturnObjectsToStore(final T entityToStore);
	
	Collection<Object> getObjectsToStore();
	
	Collection<Object> removeEntityAndReturnObjectsToStore(final T entityToRemove);
	
	Collection<Object> removeAllEntitiesAndReturnObjectsToStore();
	
	T getEntityById(ID id);
}
