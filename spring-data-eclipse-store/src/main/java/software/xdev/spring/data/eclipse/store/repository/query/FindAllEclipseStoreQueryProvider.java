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
package software.xdev.spring.data.eclipse.store.repository.query;

import java.lang.reflect.Method;

import jakarta.annotation.Nonnull;

import org.springframework.data.core.TypeInformation;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;

import software.xdev.spring.data.eclipse.store.core.EntityListProvider;
import software.xdev.spring.data.eclipse.store.repository.Query;
import software.xdev.spring.data.eclipse.store.repository.query.criteria.Criteria;
import software.xdev.spring.data.eclipse.store.repository.query.executors.QueryExecutor;
import software.xdev.spring.data.eclipse.store.repository.query.executors.QueryExecutorCreator;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopier;


/**
 * Creates queries for specific entities with "findAll" and executes them through the
 * {@link QueryExecutor}.
 * <p>
 * Is needed for special use if {@link Query} is used with
 * {@code findAll}.
 * </p>
 *
 * @param <T> entity type to create query for
 */
public class FindAllEclipseStoreQueryProvider<T> implements RepositoryQuery
{
	private final EntityListProvider entityListProvider;
	private final Class<T> domainClass;
	private final TypeInformation<?> typeInformation;
	private final WorkingCopier<T> copier;
	private final QueryMethod queryMethod;
	
	public FindAllEclipseStoreQueryProvider(
		final QueryMethod queryMethod,
		final Method method,
		final Class<T> domainClass,
		final EntityListProvider entityListProvider,
		final WorkingCopier<T> copier)
	{
		this.queryMethod = queryMethod;
		this.domainClass = domainClass;
		this.entityListProvider = entityListProvider;
		this.typeInformation = TypeInformation.fromReturnTypeOf(method);
		this.copier = copier;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Creates a new {@link EclipseStoreQueryCreator} and executes it with empty criteria.
	 * </p>
	 *
	 * @param values must not be {@literal null}.
	 * @return queried entities/entity wrapped in the correct class depending on the {@link #typeInformation}
	 */
	@Override
	@Nonnull
	public Object execute(@Nonnull final Object[] values)
	{
		return
			QueryExecutorCreator
				.createQuery(this.typeInformation, this.copier, Criteria.createNoCriteria(), null)
				.execute(this.domainClass, this.entityListProvider.getEntityProvider(this.domainClass), values);
	}
	
	@Override
	@Nonnull
	public QueryMethod getQueryMethod()
	{
		return this.queryMethod;
	}
}
