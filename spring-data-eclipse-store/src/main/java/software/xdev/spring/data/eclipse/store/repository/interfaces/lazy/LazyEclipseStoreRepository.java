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
package software.xdev.spring.data.eclipse.store.repository.interfaces.lazy;

import org.springframework.data.repository.NoRepositoryBean;
import org.eclipse.serializer.reference.Lazy;

import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository;


@SuppressWarnings("java:S119")
@NoRepositoryBean
public interface LazyEclipseStoreRepository<T, ID>
	extends
	LazyEclipseStoreListCrudRepository<T, ID>,
	LazyEclipseStoreListPagingAndSortingRepositoryRepository<T, ID>,
	LazyEclipseStoreQueryByExampleExecutor<T>,
	EclipseStoreRepository<Lazy<T>, ID>
{
}
