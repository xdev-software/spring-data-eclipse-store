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

import org.eclipse.store.integrations.spring.boot.types.configuration.EclipseStoreProperties;
import org.eclipse.store.integrations.spring.boot.types.factories.EmbeddedStorageFoundationFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


/**
 * Provides the Default implementation of an {@link EclipseStoreClientConfiguration}. The class can't be final because
 * of the {@link Configuration}-Annotation. It's mostly used to check if the user wants the default implementation or if
 * he created a different {@link EclipseStoreClientConfiguration}.
 */
@Configuration(proxyBeanMethods = false)
public class DefaultEclipseStoreClientConfiguration extends EclipseStoreClientConfiguration
{
	@Autowired
	protected DefaultEclipseStoreClientConfiguration(
		final EclipseStoreProperties defaultEclipseStoreProperties,
		final EmbeddedStorageFoundationFactory defaultEclipseStoreProvider)
	{
		super(defaultEclipseStoreProperties, defaultEclipseStoreProvider);
	}
}
