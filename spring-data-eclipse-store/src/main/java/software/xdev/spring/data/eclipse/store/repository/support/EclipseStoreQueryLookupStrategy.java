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
package software.xdev.spring.data.eclipse.store.repository.support;

import java.lang.reflect.Method;

import jakarta.annotation.Nonnull;

import org.eclipse.serializer.reference.Lazy;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;

import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;
import software.xdev.spring.data.eclipse.store.repository.Query;
import software.xdev.spring.data.eclipse.store.repository.query.FindAllEclipseStoreQueryProvider;
import software.xdev.spring.data.eclipse.store.repository.query.HSqlQueryProvider;
import software.xdev.spring.data.eclipse.store.repository.query.StringBasedEclipseStoreQueryProvider;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopierCreator;


public class EclipseStoreQueryLookupStrategy implements QueryLookupStrategy
{
	private final EclipseStoreStorage storage;
	private final WorkingCopierCreator workingCopierCreator;
	
	public EclipseStoreQueryLookupStrategy(
		final EclipseStoreStorage storage,
		final WorkingCopierCreator workingCopierCreator)
	{
		this.storage = storage;
		this.workingCopierCreator = workingCopierCreator;
	}
	
	@Override
	@Nonnull
	public RepositoryQuery resolveQuery(
		@Nonnull final Method method,
		@Nonnull final RepositoryMetadata metadata,
		@Nonnull final ProjectionFactory factory,
		@Nonnull final NamedQueries namedQueries)
	{
		final QueryMethod queryMethod = new QueryMethod(method, metadata, factory);
		
		Class<?> domainType = metadata.getDomainType();
		if(domainType.equals(Lazy.class))
		{
			domainType = metadata.getDomainTypeInformation().getTypeArguments().get(0).getType();
		}
		
		final Query queryAnnotation = method.getAnnotation(Query.class);
		if(queryAnnotation != null)
		{
			if(method.getName().equalsIgnoreCase("findall"))
			{
				// Special case for Queries that have findAll and are annotated with Query
				return this.createFindAllEclipseStoreQueryProvider(
					domainType,
					queryMethod,
					method
				);
			}
			
			return this.createHSqlQueryProvider(
				queryAnnotation.value(),
				domainType,
				queryMethod
			);
		}
		return this.createStringBasedEclipseStoreQueryProvider(
			domainType,
			queryMethod,
			method
		);
	}
	
	private <T> RepositoryQuery createFindAllEclipseStoreQueryProvider(
		final Class<T> domainType,
		final QueryMethod queryMethod,
		final Method method
	)
	{
		return new FindAllEclipseStoreQueryProvider<>(
			queryMethod,
			method,
			domainType,
			this.storage,
			this.workingCopierCreator.createWorkingCopier(domainType, this.storage)
		);
	}
	
	private <T> RepositoryQuery createStringBasedEclipseStoreQueryProvider(
		final Class<T> domainType,
		final QueryMethod queryMethod,
		final Method method
	)
	{
		return new StringBasedEclipseStoreQueryProvider<>(
			queryMethod,
			method,
			domainType,
			this.storage,
			this.workingCopierCreator.createWorkingCopier(domainType, this.storage)
		);
	}
	
	private <T> RepositoryQuery createHSqlQueryProvider(
		final String sqlString,
		final Class<T> domainType,
		final QueryMethod queryMethod
	)
	{
		return new HSqlQueryProvider<>(
			sqlString,
			queryMethod,
			domainType,
			this.storage,
			this.workingCopierCreator.createWorkingCopier(domainType, this.storage)
		);
	}
}
