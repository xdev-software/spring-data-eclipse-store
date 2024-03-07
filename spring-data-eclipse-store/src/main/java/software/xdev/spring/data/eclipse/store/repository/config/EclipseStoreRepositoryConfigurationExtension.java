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
package software.xdev.spring.data.eclipse.store.repository.config;

import static software.xdev.spring.data.eclipse.store.repository.config.EnableEclipseStoreRepositories.CLIENT_CONFIGURATION_ANNOTATION_VALUE;
import static software.xdev.spring.data.eclipse.store.repository.config.EnableEclipseStoreRepositories.CLIENT_CONFIGURATION_CLASS_ANNOTATION_VALUE;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Nonnull;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.util.ClassUtils;

import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreCrudRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreCustomRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreListCrudRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreListPagingAndSortingRepositoryRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStorePagingAndSortingRepositoryRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository;
import software.xdev.spring.data.eclipse.store.repository.support.EclipseStoreRepositoryFactoryBean;


/**
 * {@link RepositoryConfigurationExtension} for EclipseStore.
 */
public class EclipseStoreRepositoryConfigurationExtension extends RepositoryConfigurationExtensionSupport
{
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
	 * This method puts the {@link EclipseStoreRepositoryFactoryBean#configuration} in the created
	 * {@link EclipseStoreRepositoryFactoryBean}. This is important to link
	 * {@link EnableEclipseStoreRepositories#clientConfiguration()} with the actual
	 * {@link software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage}.
	 */
	@Override
	public void postProcess(final BeanDefinitionBuilder builder, final AnnotationRepositoryConfigurationSource config)
	{
		final AnnotationAttributes attributes = config.getAttributes();
		final Class<?> configurationClass = attributes.getClass(CLIENT_CONFIGURATION_CLASS_ANNOTATION_VALUE);
		String configurationString = attributes.getString(CLIENT_CONFIGURATION_ANNOTATION_VALUE);
		if(!configurationClass.equals(DefaultEclipseStoreClientConfiguration.class))
		{
			configurationString = ClassUtils.getShortNameAsProperty(configurationClass);
		}
		builder.addPropertyReference("configuration", configurationString);
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
			EclipseStorePagingAndSortingRepositoryRepository.class,
			EclipseStoreListPagingAndSortingRepositoryRepository.class,
			EclipseStoreCrudRepository.class,
			EclipseStoreListCrudRepository.class
		);
	}
}
