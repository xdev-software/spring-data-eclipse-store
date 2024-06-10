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
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.data.util.TypeInformation;

import jakarta.annotation.Nonnull;
import software.xdev.spring.data.eclipse.store.repository.EntityListProvider;
import software.xdev.spring.data.eclipse.store.repository.query.executors.QueryExecutor;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopier;


/**
 * Creates queries for specific entities and executes them through the
 * {@link QueryExecutor}.
 *
 * @param <T> entity type to create query for
 */
public class StringBasedEclipseStoreQueryProvider<T> implements RepositoryQuery
{
	private static final Logger LOG = LoggerFactory.getLogger(StringBasedEclipseStoreQueryProvider.class);
	private final PartTree tree;
	private final Parameters<?, ?> parameters;
	private final EntityListProvider entityListProvider;
	private final Class<T> domainClass;
	private final TypeInformation<?> typeInformation;
	private final WorkingCopier<T> copier;
	private final QueryMethod queryMethod;
	
	public StringBasedEclipseStoreQueryProvider(
		final QueryMethod queryMethod,
		final Method method,
		final Class<T> domainClass,
		final EntityListProvider entityListProvider,
		final WorkingCopier<T> copier)
	{
		Objects.requireNonNull(method);
		this.queryMethod = queryMethod;
		this.domainClass = Objects.requireNonNull(domainClass);
		this.entityListProvider = Objects.requireNonNull(entityListProvider);
		this.tree = new PartTree(method.getName(), domainClass);
		this.typeInformation = TypeInformation.fromReturnTypeOf(method);
		this.parameters = queryMethod.getParameters();
		this.copier = Objects.requireNonNull(copier);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Creates a new {@link EclipseStoreQueryCreator} and executes it with given values.
	 * </p>
	 *
	 * @param values must not be {@literal null}.
	 * @return queried entities/entity wrapped in the correct class depending on the {@link #typeInformation}
	 */
	@Override
	@Nonnull
	public Object execute(@Nonnull final Object[] values)
	{
		Objects.requireNonNull(values);
		final ParametersParameterAccessor accessor = new ParametersParameterAccessor(this.parameters, values);
		final EclipseStoreQueryCreator<T> creator =
			new EclipseStoreQueryCreator<>(this.domainClass, this.typeInformation, this.copier, this.tree, accessor);
		
		if(LOG.isDebugEnabled())
		{
			LOG.debug("Executing query {}...", this.queryMethod);
		}
		final Object result = creator.createQuery()
			.execute(this.domainClass, this.entityListProvider.getEntityList(this.domainClass), values);
		if(LOG.isDebugEnabled())
		{
			LOG.debug("Done executing query {}.", this.queryMethod);
		}
		return result;
	}
	
	@Override
	@Nonnull
	public QueryMethod getQueryMethod()
	{
		return this.queryMethod;
	}
}
