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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.keywords;

import org.eclipse.serializer.reflect.ClassLoaderProvider;
import org.eclipse.store.integrations.spring.boot.types.configuration.EclipseStoreProperties;
import org.eclipse.store.integrations.spring.boot.types.factories.EmbeddedStorageFoundationFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import software.xdev.spring.data.eclipse.store.integration.TestConfiguration;
import software.xdev.spring.data.eclipse.store.repository.config.EnableEclipseStoreRepositories;


@Configuration
@EnableEclipseStoreRepositories
public class KeywordsTestConfiguration extends TestConfiguration
{
	@Autowired
	protected KeywordsTestConfiguration(
		final EclipseStoreProperties defaultEclipseStoreProperties,
		final EmbeddedStorageFoundationFactory defaultEclipseStoreProvider,
		final ClassLoaderProvider classLoaderProvider)
	{
		super(defaultEclipseStoreProperties, defaultEclipseStoreProvider, classLoaderProvider);
	}
}
