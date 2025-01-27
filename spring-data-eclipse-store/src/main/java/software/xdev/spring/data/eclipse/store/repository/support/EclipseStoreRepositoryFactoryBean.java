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
import java.util.List;

import jakarta.annotation.Nonnull;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;
import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreRepositoryConfigurationExtension;


@ComponentScan({
	"software.xdev.spring.data.eclipse.store.repository",
	"software.xdev.spring.data.eclipse.store.transactions"
})
@Component
@SuppressWarnings("java:S119")
public class EclipseStoreRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
	extends RepositoryFactoryBeanSupport<T, S, ID>
{
	private Class<?> configurationClass;
	@Autowired
	private List<EclipseStoreClientConfiguration> configurations;
	
	public EclipseStoreRepositoryFactoryBean(
		final Class<? extends T> repositoryInterface)
	{
		super(repositoryInterface);
	}
	
	/**
	 * Called by
	 * {@link EclipseStoreRepositoryConfigurationExtension#postProcess(BeanDefinitionBuilder,
	 * AnnotationRepositoryConfigurationSource)}
	 */
	public void setConfigurationClass(final Class<?> configurationClass)
	{
		this.configurationClass = configurationClass;
	}
	
	@Override
	@Nonnull
	protected RepositoryFactorySupport createRepositoryFactory()
	{
		final EclipseStoreClientConfiguration ensuredConfiguration = this.getSuitableConfiguration();
		return new EclipseStoreRepositoryFactory(
			ensuredConfiguration.getStorageInstance(),
			ensuredConfiguration.getTransactionManagerInstance(),
			ensuredConfiguration.getValidator()
		);
	}
	
	/**
	 * This is surely not the perfect way to get the correct configuration of that context, but it works with multiple
	 * configurations, with no configuration and with a single configuration.
	 * <p>
	 * It checks if any of the available configurations are suitable from the {@link #configurationClass}
	 * and if so, returns it. Otherwise the
	 * {@link software.xdev.spring.data.eclipse.store.repository.config.DefaultEclipseStoreClientConfiguration}
	 * should be injected into the {@link #configurations} and get returned.
	 * </p>
	 */
	@SuppressWarnings("java:S1872")
	private EclipseStoreClientConfiguration getSuitableConfiguration()
	{
		if(this.configurationClass == null && this.configurations.size() == 1)
		{
			return this.configurations.get(0);
		}
		
		return this.configurations
			.stream()
			.filter(configuration -> this.configurationClass != null
				&& this.configurationClass.getName().equals(ClassUtils.getUserClass(configuration).getName()))
			.findAny()
			.orElseThrow(() -> new NoSuchBeanDefinitionException(this.configurationClass));
	}
}
