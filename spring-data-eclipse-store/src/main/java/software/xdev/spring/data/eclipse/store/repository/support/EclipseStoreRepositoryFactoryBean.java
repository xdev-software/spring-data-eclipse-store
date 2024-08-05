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
import java.util.Objects;

import jakarta.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.stereotype.Component;

import software.xdev.spring.data.eclipse.store.repository.config.DefaultEclipseStoreClientConfiguration;
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
	private static final Logger LOG = LoggerFactory.getLogger(EclipseStoreRepositoryFactoryBean.class);
	private EclipseStoreClientConfiguration configuration;
	private BeanFactory beanFactory;
	private Class<?> configurationClass;
	
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
	public void setBeanFactory(final BeanFactory beanFactory)
	{
		super.setBeanFactory(beanFactory);
		this.beanFactory = beanFactory;
	}
	
	@Override
	@Nonnull
	protected RepositoryFactorySupport createRepositoryFactory()
	{
		final EclipseStoreClientConfiguration ensuredConfiguration = this.ensureConfiguration();
		return new EclipseStoreRepositoryFactory(
			ensuredConfiguration.getStorageInstance(),
			ensuredConfiguration.getTransactionManagerInstance()
		);
	}
	
	private EclipseStoreClientConfiguration ensureConfiguration()
	{
		if(this.configuration == null)
		{
			this.configuration = this.createConfiguration();
		}
		return this.configuration;
	}
	
	/**
	 * This is surely not the perfect way to get the correct configuration of that context, but it works with multiple
	 * configurations, with no configuration and with a single configuration.
	 * <p>
	 * It checks if there is a configuration class defined (through
	 * {@link EclipseStoreRepositoryConfigurationExtension}) and then tries to get the bean for it. If no configuration
	 * is set, the {@link DefaultEclipseStoreClientConfiguration} is used.
	 * </p>
	 */
	private EclipseStoreClientConfiguration createConfiguration()
	{
		Objects.requireNonNull(this.beanFactory);
		try
		{
			if(this.configurationClass != null
				&& this.beanFactory.getBean(this.configurationClass)
				instanceof final EclipseStoreClientConfiguration eclipseStoreConfiguration
			)
			{
				return eclipseStoreConfiguration;
			}
		}
		catch(final BeansException ex)
		{
			LOG.warn(
				"Could not initiate Bean %s. Using %s instead."
					.formatted(
						this.configurationClass.getSimpleName(),
						DefaultEclipseStoreClientConfiguration.class.getSimpleName()
					),
				ex);
		}
		return this.beanFactory.getBean(DefaultEclipseStoreClientConfiguration.class);
	}
}
