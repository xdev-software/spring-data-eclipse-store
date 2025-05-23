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

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.eclipse.serializer.reflect.ClassLoaderProvider;
import org.eclipse.store.integrations.spring.boot.types.configuration.EclipseStoreProperties;
import org.eclipse.store.integrations.spring.boot.types.factories.EmbeddedStorageFoundationFactory;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageFoundation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.PlatformTransactionManager;

import software.xdev.micromigration.migrater.MicroMigrater;
import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;
import software.xdev.spring.data.eclipse.store.repository.root.EclipseStoreMigrator;
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
@ComponentScan({
	"org.eclipse.store.integrations.spring.boot.types",
	"software.xdev.spring.data.eclipse.store.importer"
})
public abstract class EclipseStoreClientConfiguration implements EclipseStoreStorageFoundationProvider
{
	private static final Logger LOG = LoggerFactory.getLogger(EclipseStoreClientConfiguration.class);
	
	protected final EclipseStoreProperties defaultEclipseStoreProperties;
	protected final EmbeddedStorageFoundationFactory defaultEclipseStoreProvider;
	protected final ClassLoaderProvider classLoaderProvider;
	
	protected EclipseStoreStorage storageInstance;
	protected EclipseStoreTransactionManager transactionManager;
	
	@Value("${spring-data-eclipse-store.context-close-shutdown-storage.enabled:true}")
	protected boolean contextCloseShutdownStorageEnabled;
	
	@Value("${spring-data-eclipse-store.context-close-shutdown-storage.only-when-dev-tools:true}")
	protected boolean contextCloseShutdownStorageOnlyWhenDevTools;
	
	/**
	 * Upstream value from Spring Boot DevTools.
	 *
	 * @see org.springframework.boot.devtools.autoconfigure.DevToolsProperties.Restart
	 */
	@Value("${spring.devtools.restart.enabled:true}")
	protected boolean springDevtoolsRestartEnabled;
	
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	@Lazy
	@Autowired
	protected EclipseStoreClientConfiguration(
		final EclipseStoreProperties defaultEclipseStoreProperties,
		final EmbeddedStorageFoundationFactory defaultEclipseStoreProvider,
		final ClassLoaderProvider classLoaderProvider)
	{
		this.defaultEclipseStoreProperties = defaultEclipseStoreProperties;
		this.classLoaderProvider = classLoaderProvider;
		this.defaultEclipseStoreProperties.setAutoStart(false);
		this.defaultEclipseStoreProvider = defaultEclipseStoreProvider;
	}
	
	public EclipseStoreProperties getEclipseStoreProperties()
	{
		return this.defaultEclipseStoreProperties;
	}
	
	public EmbeddedStorageFoundationFactory getStoreProvider()
	{
		return this.defaultEclipseStoreProvider;
	}
	
	public ClassLoaderProvider getClassLoaderProvider()
	{
		return this.classLoaderProvider;
	}
	
	/**
	 * Creates a {@link EmbeddedStorageFoundation} out of the two other provided functions {@link #getStoreProvider()}
	 * and {@link #getEclipseStoreProperties()}.
	 */
	@Override
	public EmbeddedStorageFoundation<?> createEmbeddedStorageFoundation()
	{
		return this.getStoreProvider().createStorageFoundation(this.getEclipseStoreProperties());
	}
	
	public EclipseStoreStorage getStorageInstance()
	{
		if(this.storageInstance == null)
		{
			this.storageInstance = new EclipseStoreStorage(this);
		}
		return this.storageInstance;
	}
	
	public PlatformTransactionManager transactionManager(
		final ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers)
	{
		final EclipseStoreTransactionManager tm = this.getTransactionManagerInstance();
		transactionManagerCustomizers.ifAvailable(customizers -> customizers.customize(tm));
		return tm;
	}
	
	public EclipseStoreTransactionManager getTransactionManagerInstance()
	{
		if(this.transactionManager == null)
		{
			this.transactionManager = new EclipseStoreTransactionManager();
		}
		return this.transactionManager;
	}
	
	protected boolean shouldShutdownStorageOnContextClosed()
	{
		// Did the user disable support for this?
		if(!this.contextCloseShutdownStorageEnabled)
		{
			return false;
		}
		
		// Always or only for DevTools?
		if(!this.contextCloseShutdownStorageOnlyWhenDevTools)
		{
			return true;
		}
		
		// Spring DevTools loaded?
		try
		{
			Class.forName("org.springframework.boot.devtools.autoconfigure.DevToolsProperties");
		}
		catch(final ClassNotFoundException e)
		{
			return false;
		}
		
		// Spring Boot DevTools Restart enabled?
		final boolean enabled = this.springDevtoolsRestartEnabled;
		if(enabled)
		{
			LOG.warn("Will shut down storage because Spring Boot DevTools Restarting is active. "
				+ "This may cause some unexpected behavior. "
				+ "For more information have a look at "
				+ "https://spring-eclipsestore.xdev.software/known-issues.html#spring-dev-tools");
		}
		return enabled;
	}
	
	/**
	 * Invoked when the application is "shut down" - or parts of it during a DevTools restart.
	 * <p>
	 * Shuts down the storage when it's present and {@link #shouldShutdownStorageOnContextClosed()} is
	 * <code>true</code>
	 * </p>
	 *
	 * <p>
	 * This is required for the DevTools restart as it otherwise crashes with <code>StorageExceptionInitialization:
	 * Active storage for ... already exists</code>
	 * </p>
	 */
	@EventListener
	public void shutdownStorageOnContextClosed(final ContextClosedEvent event)
	{
		if(this.storageInstance != null && this.shouldShutdownStorageOnContextClosed())
		{
			this.storageInstance.stop();
		}
	}

	@Bean
	public Validator getValidator()
	{
		try(final ValidatorFactory factory = Validation.buildDefaultValidatorFactory())
		{
			return factory.getValidator();
		}
	}
	
	/**
	 * <i>"Why don't you migrate the data wherever you call EclipseStoreMigrator.migrateStructure?"</i> - Because in
	 * order to be able to access repositories in DataMigrationScripts, we can't have the migration-method block the
	 * start of the storage. That would lead to a deadlock, and we don't want that.
	 */
	@EventListener
	public void migrateDataOnContextStarted(final ContextRefreshedEvent event)
	{
		try
		{
			final MicroMigrater dataMigrater = event.getApplicationContext().getBean(MicroMigrater.class);
			this.getStorageInstance().start(); // In case the storage hasn't started yet.
			EclipseStoreMigrator.migrateData(
				this.getStorageInstance().getRoot(),
				dataMigrater,
				this.getStorageInstance().getInstanceOfStorageManager()
			);
		}
		catch(final NoSuchBeanDefinitionException e)
		{
			LOG.info("No migration of data needed since there is no migrater defined.");
		}
	}
}
