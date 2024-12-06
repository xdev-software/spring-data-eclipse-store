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
package software.xdev.spring.data.eclipse.store.repository;

import java.util.Collection;

import software.xdev.spring.data.eclipse.store.core.EntityProvider;
import software.xdev.spring.data.eclipse.store.repository.support.SimpleEclipseStoreRepository;
import software.xdev.spring.data.eclipse.store.repository.support.concurrency.ReadWriteLock;


public interface StorageCommunicator
{
	ReadWriteLock getReadWriteLock();
	
	<T> void store(
		final Collection<Object> nonEntitiesToStore, final Class<T> clazz,
		final Iterable<T> entitiesToStore);
	
	<T, ID> EntityProvider<T, ID> getEntityProvider(final Class<T> clazz);
	
	<T> long getEntityCount(Class<T> domainClass);
	
	<T> void delete(Class<T> domainClass, T foundEntity);
	
	<T> void deleteAll(Class<T> domainClass);
	
	<T, ID> void registerEntity(Class<T> domainClass, SimpleEclipseStoreRepository<T, ID> repository);
}
