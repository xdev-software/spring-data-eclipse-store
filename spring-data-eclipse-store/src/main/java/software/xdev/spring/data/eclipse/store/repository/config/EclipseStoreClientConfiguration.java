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

import org.eclipse.store.integrations.spring.boot.types.EclipseStoreProvider;
import org.eclipse.store.integrations.spring.boot.types.EclipseStoreProviderImpl;
import org.eclipse.store.integrations.spring.boot.types.configuration.EclipseStoreProperties;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageFoundation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;


@Configuration(proxyBeanMethods = false)
public abstract class EclipseStoreClientConfiguration implements EclipseStoreStorageFoundationProvider
{
	@Autowired
	private EclipseStoreProperties defaultEclipseStoreProperties;
	
	@Autowired
	private EclipseStoreProviderImpl defaultEclipseStoreProvider;
	
	private EclipseStoreStorage storageInstance;
	
	public EclipseStoreProperties getStoreConfiguration()
	{
		return this.defaultEclipseStoreProperties;
	}
	
	public EclipseStoreProvider getStoreProvider()
	{
		return this.defaultEclipseStoreProvider;
	}
	
	@Override
	public EmbeddedStorageFoundation<?> getEmbeddedStorageFoundation()
	{
		return this.getStoreProvider().createStorageFoundation(this.getStoreConfiguration());
	}
	
	public EclipseStoreStorage getStorageInstance()
	{
		if(this.storageInstance == null)
		{
			this.storageInstance = new EclipseStoreStorage(this);
		}
		return this.storageInstance;
	}
}
