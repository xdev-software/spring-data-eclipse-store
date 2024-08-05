package software.xdev.spring.data.eclipse.store.repository.query.antlr;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.ReflectiveAttribute;
import com.googlecode.cqengine.query.parser.sql.SQLParser;
import com.googlecode.cqengine.resultset.ResultSet;

import software.xdev.spring.data.eclipse.store.core.EntityListProvider;
import software.xdev.spring.data.eclipse.store.repository.access.AccessHelper;


public class HSqlQueryExecutor<T>
{
	private final SQLParser<T> parser;
	private final EntityListProvider entityListProvider;
	private final Class<T> domainClass;
	
	public HSqlQueryExecutor(final Class<T> domainClass, final EntityListProvider entityListProvider)
	{
		this.domainClass = domainClass;
		this.parser = SQLParser.forPojoWithAttributes(domainClass, this.createAttributes(domainClass));
		this.entityListProvider = entityListProvider;
	}
	
	public Object execute(final String sqlValue, final Object[] parameters)
	{
		final IndexedCollection<T> entities = new ConcurrentIndexedCollection<>();
		entities.addAll(this.entityListProvider.getEntityProvider(this.domainClass).toCollection());
		final String sqlStringWithReplacedValues = this.replacePlaceholders(sqlValue, parameters);
		final ResultSet<T> retrieve = this.parser.retrieve(entities, sqlStringWithReplacedValues);
		return retrieve.stream().toList();
	}
	
	private String replacePlaceholders(String sqlValue, final Object[] parameters)
	{
		// Replace positional placeholders with actual parameter values
		for(int i = 0; i < parameters.length; i++)
		{
			final String placeholder = "\\?" + (i + 1);
			String value = parameters[i].toString();
			if(parameters[i] instanceof String)
			{
				value = "'" + value + "'";
			}
			if(parameters[i] instanceof final Collection collection)
			{
				value =
					collection.stream()
						.map(o -> "'" + o.toString() + "'")
						.collect(Collectors.joining(", ", "(", ")"))
						.toString();
			}
			sqlValue = sqlValue.replaceAll(placeholder, value);
		}
		return sqlValue;
	}
	
	private <O> Map<String, ? extends Attribute<O, ?>> createAttributes(final Class<O> domainClass)
	{
		final Map<String, Attribute<O, ?>> attributes = new TreeMap<>();
		AccessHelper.getInheritedPrivateFieldsByName(domainClass).forEach(
			(fieldName, field) -> attributes.put(
				fieldName,
				new ReflectiveAttribute<>(
					domainClass,
					field.getType(),
					fieldName
				)
			)
		);
		return attributes;
	}
}
