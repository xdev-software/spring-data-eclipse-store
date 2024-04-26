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

import org.eclipse.store.integrations.spring.boot.types.EclipseStoreProvider;
import org.eclipse.store.integrations.spring.boot.types.EclipseStoreProviderImpl;
import org.eclipse.store.integrations.spring.boot.types.configuration.EclipseStoreProperties;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageFoundation;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;

import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;
import software.xdev.spring.data.eclipse.store.transactions.EclipseStoreTransactionManager;


/**
 * Defines the configuration of a single EclipseStore-Storage.
 * <p>
 * Configuration is possible through default EclipseStore-Behavior.<br/> See <a
 * href="https://docs.eclipsestore.io/manual/misc/integrations/spring-boot.html">EclipseStore documentation</a>
 * </p>
 * <p>
 * It's also possible to inherit this configuration and override {@link #createEmbeddedStorageFoundation()}
 * </p>
 * <p>
 * Also creates a singleton reference to a {@link EclipseStoreStorage}. This is to only create one EclipseStore-Storage
 * for one configuration.
 * </p>
 */
@Configuration(proxyBeanMethods = false)
public abstract class EclipseStoreClientConfiguration implements EclipseStoreStorageFoundationProvider
{
	@Autowired
	@Qualifier("eclipseStoreProperties")
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
	
	/**
	 * Creates a {@link EmbeddedStorageFoundation} out of the two other provided functions {@link #getStoreProvider()}
	 * and {@link #getStoreConfiguration()}.
	 */
	@Override
	public EmbeddedStorageFoundation<?> createEmbeddedStorageFoundation()
	{
		return this.getStoreProvider().createStorageFoundation(this.getStoreConfiguration());
	}
	
	@Bean
	@ConditionalOnMissingBean(TransactionManager.class)
	public PlatformTransactionManager transactionManager(
		final ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers)
	{
		final EclipseStoreTransactionManager transactionManager = new EclipseStoreTransactionManager();
		transactionManagerCustomizers.ifAvailable((customizers) -> customizers.customize((TransactionManager)transactionManager));
		return transactionManager;
	}
	
	@Bean
	public EclipseStoreStorage getStorageInstance()
	{
		if(this.storageInstance == null)
		{
			this.storageInstance = new EclipseStoreStorage(this);
		}
		return this.storageInstance;
	}
}
