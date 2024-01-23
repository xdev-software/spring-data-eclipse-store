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
package software.xdev.spring.data.eclipse.store.repository.query;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;
import org.springframework.data.repository.query.DefaultParameters;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.ParametersSource;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.data.util.TypeInformation;

import software.xdev.spring.data.eclipse.store.helper.DummyWorkingCopier;
import software.xdev.spring.data.eclipse.store.helper.TestData;


public class QueryCreatorUtil
{
	
	public final static Collection<Customer> DATA_CUSTOMERS_EMPTY =
		new ArrayList<>();
	public final static Collection<Customer> DATA_CUSTOMERS_ONE =
		List.of(new Customer(1, TestData.FIRST_NAME, TestData.LAST_NAME, new Incomparable(""), true));
	public final static Collection<Customer> DATA_CUSTOMERS_TWO = List.of(
		new Customer(1, TestData.FIRST_NAME, TestData.LAST_NAME, new Incomparable(""), true),
		new Customer(
			2,
			TestData.FIRST_NAME_ALTERNATIVE,
			TestData.LAST_NAME_ALTERNATIVE, new Incomparable(""), false));
	public final static Collection<Customer> DATA_CUSTOMERS_THREE = List.of(
		new Customer(1, TestData.FIRST_NAME, TestData.LAST_NAME, new Incomparable(""), true),
		new Customer(
			2,
			TestData.FIRST_NAME_ALTERNATIVE,
			TestData.LAST_NAME_ALTERNATIVE, new Incomparable(""), false),
		new Customer(3, TestData.FIRST_NAME, TestData.LAST_NAME_ALTERNATIVE, null, false));
	public final static Collection<Customer> DATA_CUSTOMERS_DABC_ABCD = List.of(
		new Customer(1, "D", "A", new Incomparable(""), true),
		new Customer(2, "A", "B", new Incomparable(""), false),
		new Customer(3, "B", "C", new Incomparable(""), true),
		new Customer(4, "C", "D", null, false));
	public final static Collection<Customer> DATA_CUSTOMERS_WITH_NULL_STRING = List.of(
		new Customer(1, TestData.FIRST_NAME, TestData.LAST_NAME, new Incomparable(""), true),
		new Customer(2, null, TestData.LAST_NAME_ALTERNATIVE, new Incomparable(""), false),
		new Customer(3, TestData.FIRST_NAME_ALTERNATIVE, null, new Incomparable(""), true),
		new Customer(4, TestData.FIRST_NAME_ALTERNATIVE, TestData.LAST_NAME_ALTERNATIVE, null, false));
	
	@SuppressWarnings("unchecked")
	public static <T> Collection<T> executeQuery(
		final Collection<T> entities,
		final Class<T> domainClass,
		final Method method,
		final Object[] values)
	{
		final PartTree tree = new PartTree(method.getName(), domainClass);
		final TypeInformation<T> typeInformation = (TypeInformation<T>)TypeInformation.fromReturnTypeOf(method);
		
		final DefaultParameters parameters =
			new DefaultParameters(ParametersSource.of(
				new DefaultRepositoryMetadata(method.getDeclaringClass()),
				method));
		
		final ParametersParameterAccessor accessor = new ParametersParameterAccessor(parameters, values);
		final EclipseStoreQueryCreator<T> creator =
			new EclipseStoreQueryCreator<>(domainClass, typeInformation, new DummyWorkingCopier<>(), tree, accessor);
		
		return (Collection<T>)creator.createQuery().execute(domainClass, entities, values);
	}
	
	public record Customer(int id, String firstName, String lastName, Incomparable incomparable, boolean enabled)
	{
	}
	
	
	public record Incomparable(String value)
	{
	}
}
