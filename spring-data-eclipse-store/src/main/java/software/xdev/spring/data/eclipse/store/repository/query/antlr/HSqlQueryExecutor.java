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
package software.xdev.spring.data.eclipse.store.repository.query.antlr;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
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
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopier;


public class HSqlQueryExecutor<T>
{
	private final SQLParser<T> parser;
	private final EntityListProvider entityListProvider;
	private final Class<T> domainClass;
	private final WorkingCopier<T> copier;
	
	public HSqlQueryExecutor(
		final Class<T> domainClass,
		final EntityListProvider entityListProvider,
		final WorkingCopier<T> copier)
	{
		this.domainClass = domainClass;
		this.parser = SQLParser.forPojoWithAttributes(domainClass, this.createAttributes(domainClass));
		this.entityListProvider = entityListProvider;
		this.copier = copier;
	}
	
	public List<T> execute(final String sqlValue, final Object[] parameters)
	{
		final IndexedCollection<T> entities = new ConcurrentIndexedCollection<>();
		entities.addAll(this.entityListProvider.getEntityProvider(this.domainClass).toCollection());
		final String sqlStringWithReplacedValues = this.replacePlaceholders(sqlValue, parameters);
		final ResultSet<T> retrieve = this.parser.retrieve(entities, sqlStringWithReplacedValues);
		final List<T> results = retrieve.stream().toList();
		return this.copier.copy(results);
	}
	
	private String replacePlaceholders(final String sqlValue, final Object[] parameters)
	{
		String stringWithReplacedValues = sqlValue;
		// Replace positional placeholders with actual parameter values
		for(int i = 0; i < parameters.length; i++)
		{
			final String placeholder = "\\?" + (i + 1);
			String value = parameters[i].toString();
			if(parameters[i] instanceof final Collection collection)
			{
				value =
					collection.stream()
						.map(o -> "'" + o.toString() + "'")
						.collect(Collectors.joining(", ", "(", ")"))
						.toString();
			}
			if(parameters[i] instanceof final LocalDate localDate)
			{
				value = localDate.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));
			}
			stringWithReplacedValues = stringWithReplacedValues.replaceAll(placeholder, value);
		}
		return stringWithReplacedValues;
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
