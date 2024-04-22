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
package software.xdev.spring.data.eclipse.store.repository.support;

import java.io.Serializable;

import jakarta.annotation.Nonnull;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.stereotype.Component;

import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;


@ComponentScan({
	"software.xdev.spring.data.eclipse.store.repository",
	"org.eclipse.store.integrations.spring.boot.types"})
@Component
public class EclipseStoreRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
	extends RepositoryFactoryBeanSupport<T, S, ID>
{
	private EclipseStoreClientConfiguration configuration;
	
	public EclipseStoreRepositoryFactoryBean(final Class<? extends T> repositoryInterface)
	{
		super(repositoryInterface);
	}
	
	public void setConfiguration(final EclipseStoreClientConfiguration configuration)
	{
		this.configuration = configuration;
	}
	
	@Override
	@Nonnull
	protected RepositoryFactorySupport createRepositoryFactory()
	{
		return new EclipseStoreRepositoryFactory(this.configuration.getStorageInstance());
	}
}
