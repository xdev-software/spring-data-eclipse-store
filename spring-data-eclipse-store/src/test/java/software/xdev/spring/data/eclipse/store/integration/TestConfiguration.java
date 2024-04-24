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
package software.xdev.spring.data.eclipse.store.integration;

import static org.eclipse.store.storage.embedded.types.EmbeddedStorage.Foundation;

import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.store.storage.embedded.types.EmbeddedStorageFoundation;
import org.eclipse.store.storage.types.Storage;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.util.FileSystemUtils;

import software.xdev.spring.data.eclipse.store.helper.StorageDirectoryNameProvider;
import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;


@Configuration
public class TestConfiguration extends EclipseStoreClientConfiguration
{
	private final String storageDirectory = StorageDirectoryNameProvider.getNewStorageDirectoryPath();
	
	@Override
	public EmbeddedStorageFoundation<?> createEmbeddedStorageFoundation()
	{
		return Foundation(Storage.Configuration(Storage.FileProvider(Path.of(this.storageDirectory))));
	}
	
	@EventListener
	public void handleContextRefresh(final ContextRefreshedEvent event)
	{
		// Init with empty root object
		this.getStorageInstance().clearData();
	}
	
	@EventListener
	public void handleContextClosed(final ContextClosedEvent event) throws IOException
	{
		// End with empty root object
		this.getStorageInstance().clearData();
		this.getStorageInstance().stop();
		FileSystemUtils.deleteRecursively(Path.of(this.storageDirectory));
	}
}
