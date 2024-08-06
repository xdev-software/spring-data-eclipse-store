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

import java.util.Objects;

import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;

import software.xdev.spring.data.eclipse.store.core.EntityListProvider;
import software.xdev.spring.data.eclipse.store.repository.query.antlr.HSqlQueryExecutor;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopier;


public class HSqlQueryProvider<T> implements RepositoryQuery
{
	private final HSqlQueryExecutor executor;
	private final String sqlValue;
	private final QueryMethod queryMethod;
	
	public HSqlQueryProvider(
		final String sqlValue,
		final QueryMethod queryMethod,
		final Class<T> domainClass,
		final EntityListProvider entityListProvider,
		final WorkingCopier<T> copier
	)
	{
		this.queryMethod = queryMethod;
		this.executor = new HSqlQueryExecutor(
			Objects.requireNonNull(domainClass),
			Objects.requireNonNull(entityListProvider),
			copier
		);
		this.sqlValue = sqlValue;
	}
	
	@Override
	public Object execute(final Object[] parameters)
	{
		return this.executor.execute(this.sqlValue, parameters);
	}
	
	@Override
	public QueryMethod getQueryMethod()
	{
		return this.queryMethod;
	}
}
