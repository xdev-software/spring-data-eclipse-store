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
package software.xdev.spring.data.eclipse.store.jpa.integration;

import java.nio.file.Path;

import org.eclipse.serializer.reflect.ClassLoaderProvider;
import org.eclipse.store.integrations.spring.boot.types.configuration.EclipseStoreProperties;
import org.eclipse.store.integrations.spring.boot.types.factories.EmbeddedStorageFoundationFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.util.FileSystemUtils;

import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;
import software.xdev.spring.data.eclipse.store.repository.config.EnableEclipseStoreRepositories;


@Configuration
@EnableEclipseStoreRepositories
@SpringBootConfiguration
@EnableAutoConfiguration
public class TestConfiguration extends EclipseStoreClientConfiguration implements DisposableBean
{
	@Value("${org.eclipse.store.storage-directory}")
	private String storageDirectory;
	
	@Autowired
	protected TestConfiguration(
		final EclipseStoreProperties defaultEclipseStoreProperties,
		final EmbeddedStorageFoundationFactory defaultEclipseStoreProvider,
		final ClassLoaderProvider classLoaderProvider)
	{
		super(defaultEclipseStoreProperties, defaultEclipseStoreProvider, classLoaderProvider);
	}
	
	@EventListener
	public void handleContextRefresh(final ContextRefreshedEvent event)
	{
		// Init with empty root object
		this.getStorageInstance().clearData();
	}
	
	@Override
	public void destroy() throws Exception
	{
		// End with empty root object
		this.getStorageInstance().clearData();
		this.getStorageInstance().stop();
		FileSystemUtils.deleteRecursively(Path.of(this.storageDirectory));
	}
}
