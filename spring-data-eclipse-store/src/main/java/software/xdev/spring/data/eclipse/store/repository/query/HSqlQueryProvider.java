package software.xdev.spring.data.eclipse.store.repository.query;

import java.lang.reflect.Method;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.query.Parameters;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.util.TypeInformation;

import software.xdev.spring.data.eclipse.store.core.EntityListProvider;
import software.xdev.spring.data.eclipse.store.repository.query.antlr.HSqlQueryExecutor;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopier;


public class HSqlQueryProvider<T> implements RepositoryQuery
{
	private static final Logger LOG = LoggerFactory.getLogger(HSqlQueryProvider.class);
	
	// private final PartTree tree;
	private final Parameters<?, ?> parameters;
	private final EntityListProvider entityListProvider;
	private final Class<T> domainClass;
	private final TypeInformation<?> typeInformation;
	private final WorkingCopier<T> copier;
	private final QueryMethod queryMethod;
	private final HSqlQueryExecutor executor;
	private final String sqlValue;
	
	public HSqlQueryProvider(
		final String sqlValue,
		final QueryMethod queryMethod,
		final Method method,
		final Class<T> domainClass,
		final EntityListProvider entityListProvider,
		final WorkingCopier<T> copier
	)
	{
		Objects.requireNonNull(method);
		this.queryMethod = queryMethod;
		this.domainClass = Objects.requireNonNull(domainClass);
		this.entityListProvider = Objects.requireNonNull(entityListProvider);
		this.typeInformation = TypeInformation.fromReturnTypeOf(method);
		this.parameters = queryMethod.getParameters();
		this.copier = Objects.requireNonNull(copier);
		this.executor = new HSqlQueryExecutor(this.domainClass, this.entityListProvider);
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
		return null;
	}
}
