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
package software.xdev.spring.data.eclipse.store.repository.support;

import java.io.Serializable;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import jakarta.annotation.Nonnull;
import jakarta.inject.Inject;
import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;


@ComponentScan({
	"software.xdev.spring.data.eclipse.store.repository",
	"org.eclipse.store.integrations.spring.boot.types"})
public class EclipseStoreRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
	extends RepositoryFactoryBeanSupport<T, S, ID>
{
	private final EclipseStoreStorage storage;
	
	@Inject
	public EclipseStoreRepositoryFactoryBean(
		final Class<? extends T> repositoryInterface,
		final EclipseStoreStorage storage)
	{
		super(repositoryInterface);
		this.storage = storage;
	}
	
	@Override
	@Nonnull
	protected RepositoryFactorySupport createRepositoryFactory()
	{
		return new EclipseStoreRepositoryFactory(this.storage);
	}
}
