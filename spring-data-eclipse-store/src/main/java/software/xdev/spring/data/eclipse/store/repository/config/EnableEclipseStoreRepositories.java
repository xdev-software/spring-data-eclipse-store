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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.config.DefaultRepositoryBaseClass;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;

import software.xdev.spring.data.eclipse.store.repository.support.EclipseStoreRepositoryFactoryBean;


/**
 * Enables the user to enable EclipseStore and configure it to some extent.
 */
@SuppressWarnings("unused")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(EclipseStoreRepositoriesRegistrar.class)
@ComponentScan({
	"software.xdev.spring.data.eclipse.store.importer",
	"software.xdev.spring.data.eclipse.store.repository",
	"org.eclipse.store.integrations.spring.boot.types"})
public @interface EnableEclipseStoreRepositories
{
	
	/**
	 * Alias for the {@link #basePackages()} attribute. Allows for more concise annotation declarations e.g.:
	 * {@code @EnableEclipseStoreRepositories("org.my.pkg")} instead of
	 * {@code @EnableEclipseStoreRepositories(basePackages="org.my.pkg")}.
	 */
	String[] value() default {};
	
	/**
	 * Base packages to scan for annotated components. {@link #value()} is an alias for (and mutually exclusive with)
	 * this attribute. Use {@link #basePackageClasses()} for a type-safe alternative to String-based package names.
	 */
	String[] basePackages() default {};
	
	/**
	 * Type-safe alternative to {@link #basePackages()} for specifying the packages to scan for annotated components.
	 * The package of each class specified will be scanned. Consider creating a special no-op marker class or interface
	 * in each package that serves no purpose other than being referenced by this attribute.
	 */
	Class<?>[] basePackageClasses() default {};
	
	/**
	 * Specifies which types are eligible for component scanning. Narrows the set of candidate components from
	 * everything in {@link #basePackages()} to everything in the base packages that matches the given filter or
	 * filters.
	 */
	Filter[] includeFilters() default {};
	
	/**
	 * Specifies which types are not eligible for component scanning.
	 */
	Filter[] excludeFilters() default {};
	
	String CLIENT_CONFIGURATION_ANNOTATION_VALUE = "clientConfiguration";
	
	String clientConfiguration() default "defaultEclipseStoreClientConfiguration";
	
	String CLIENT_CONFIGURATION_CLASS_ANNOTATION_VALUE = "clientConfigurationClass";
	
	Class<? extends EclipseStoreClientConfiguration> clientConfigurationClass() default DefaultEclipseStoreClientConfiguration.class;
	
	/**
	 * Returns the postfix to be used when looking up custom repository implementations. Defaults to {@literal Impl} .
	 * So for a repository named {@code PersonRepository} the corresponding implementation class will be looked up
	 * scanning for {@code PersonRepositoryImpl}.
	 *
	 * @return {@literal Impl} by default.
	 */
	String repositoryImplementationPostfix() default "Impl";
	
	/**
	 * Configures the location of where to find the Spring Data named queries properties file. Will default to
	 * {@code META-INFO/eclipse-store-named-queries.properties}.
	 *
	 * @return empty {@link String} by default.
	 */
	String namedQueriesLocation() default "";
	
	/**
	 * Returns the key of the {@link QueryLookupStrategy} to be used for lookup queries for query methods. Defaults to
	 * {@link Key#CREATE_IF_NOT_FOUND}.
	 *
	 * @return {@link Key#CREATE_IF_NOT_FOUND} by default.
	 */
	Key queryLookupStrategy() default Key.CREATE_IF_NOT_FOUND;
	
	/**
	 * Returns the {@link FactoryBean} class to be used for each repository instance. Defaults to
	 * {@link EclipseStoreRepositoryFactoryBean}.
	 *
	 * @return {@link EclipseStoreRepositoryFactoryBean} by default.
	 */
	Class<?> repositoryFactoryBeanClass() default EclipseStoreRepositoryFactoryBean.class;
	
	/**
	 * Configure the repository base class to be used to create repository proxies for this particular configuration.
	 *
	 * @return {@link DefaultRepositoryBaseClass} by default.
	 */
	Class<?> repositoryBaseClass() default DefaultRepositoryBaseClass.class;
	
	/**
	 * Configures the name of the EclipseStoreTemplate bean to be used with the repositories detected.
	 *
	 * @return {@literal eclipse-storeTemplate} by default.
	 */
	String eclipseStoreTemplateRef() default "eclipseStoreTemplate";
	
	/**
	 * Whether to automatically create indexes for query methods defined in the repository interface.
	 *
	 * @return {@literal false} by default.
	 */
	boolean createIndexesForQueryMethods() default false;
	
	/**
	 * Configures whether nested repository-interfaces (e.g. defined as inner classes) should be discovered by the
	 * repositories infrastructure.
	 *
	 * @return {@literal false} by default.
	 */
	boolean considerNestedRepositories() default false;
}
