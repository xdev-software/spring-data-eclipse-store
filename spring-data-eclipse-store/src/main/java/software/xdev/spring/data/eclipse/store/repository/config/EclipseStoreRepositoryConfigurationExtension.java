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
package software.xdev.spring.data.eclipse.store.repository.config;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.core.RepositoryMetadata;

import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreCrudRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreCustomRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreListCrudRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreListPagingAndSortingRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStorePagingAndSortingRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.lazy.LazyEclipseStoreCrudRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.lazy.LazyEclipseStoreCustomRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.lazy.LazyEclipseStoreListCrudRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.lazy.LazyEclipseStoreListPagingAndSortingRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.lazy.LazyEclipseStorePagingAndSortingRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.lazy.LazyEclipseStoreRepository;
import software.xdev.spring.data.eclipse.store.repository.support.EclipseStoreRepositoryFactoryBean;


/**
 * {@link RepositoryConfigurationExtension} for EclipseStore.
 */
public class EclipseStoreRepositoryConfigurationExtension extends RepositoryConfigurationExtensionSupport
{
	private static final Logger LOG = LoggerFactory.getLogger(EclipseStoreRepositoryConfigurationExtension.class);
	@Override
	@Nonnull
	public String getModuleName()
	{
		return "EclipseStoreDatastore";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@Nonnull
	@SuppressWarnings("deprecation")
	public String getModulePrefix()
	{
		return this.getModuleIdentifier();
	}
	
	@Override
	@Nonnull
	public String getRepositoryFactoryBeanClassName()
	{
		return EclipseStoreRepositoryFactoryBean.class.getName();
	}
	
	/**
	 * This is surely not the perfect way to get the correct configuration of that context, but it works with multiple
	 * configurations, with no configuration and with a single configuration.
	 */
	@SuppressWarnings("NullableProblems")
	@Override
	public void postProcess(final BeanDefinitionBuilder builder, final AnnotationRepositoryConfigurationSource config)
	{
		if(config.getSource() instanceof final AnnotationMetadata classMetadata)
		{
			try
			{
				final Class<?> possibleConfigurationClass = Class.forName(classMetadata.getClassName());
				if(EclipseStoreClientConfiguration.class.isAssignableFrom(possibleConfigurationClass))
				{
					builder.addPropertyValue("configurationClass", possibleConfigurationClass);
				}
			}
			catch(final ClassNotFoundException e)
			{
				LOG.warn(
					"Could not use {} as configuration.",
					classMetadata.getClassName()
				);
			}
		}
	}
	
	@Override
	@Nonnull
	public String getModuleIdentifier()
	{
		return "EclipseStore";
	}
	
	@Override
	@Nonnull
	public Collection<Class<? extends Annotation>> getIdentifyingAnnotations()
	{
		return Collections.emptyList();
	}
	
	@Override
	@Nonnull
	protected Collection<Class<?>> getIdentifyingTypes()
	{
		return List.of(
			EclipseStoreRepository.class,
			EclipseStoreCustomRepository.class,
			EclipseStorePagingAndSortingRepository.class,
			EclipseStoreListPagingAndSortingRepository.class,
			EclipseStoreCrudRepository.class,
			EclipseStoreListCrudRepository.class,
			LazyEclipseStoreRepository.class,
			LazyEclipseStoreCustomRepository.class,
			LazyEclipseStorePagingAndSortingRepository.class,
			LazyEclipseStoreListPagingAndSortingRepository.class,
			LazyEclipseStoreCrudRepository.class,
			LazyEclipseStoreListCrudRepository.class
		);
	}
	
	@Override
	protected boolean useRepositoryConfiguration(final RepositoryMetadata metadata)
	{
		return super.useRepositoryConfiguration(metadata);
	}
}
