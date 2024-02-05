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

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jakarta.annotation.Nonnull;

import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;

import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreCrudRepository;
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
			EclipseStorePagingAndSortingRepositoryRepository.class,
			EclipseStoreListPagingAndSortingRepositoryRepository.class,
			EclipseStoreCrudRepository.class,
			EclipseStoreListCrudRepository.class
		);
	}
}
