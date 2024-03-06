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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests;

import java.nio.file.Path;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.util.FileSystemUtils;

import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;
import software.xdev.spring.data.eclipse.store.repository.config.EnableEclipseStoreRepositories;


@Configuration
@EnableEclipseStoreRepositories
public class IsolatedTestConfiguration implements DisposableBean
{
	@Autowired
	EclipseStoreClientConfiguration configuration;
	
	@Value("${org.eclipse.store.storage-directory}")
	private String storageDirectory;
	
	@EventListener
	public void handleContextRefresh(final ContextRefreshedEvent event)
	{
		// Init with empty root object
		this.configuration.getStorageInstance().clearData();
	}
	
	@Override
	public void destroy() throws Exception
	{
		// End with empty root object
		this.configuration.getStorageInstance().clearData();
		this.configuration.getStorageInstance().stop();
		FileSystemUtils.deleteRecursively(Path.of(this.storageDirectory));
	}
}
