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
package software.xdev.spring.data.eclipse.store.repository.query.criteria;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import software.xdev.spring.data.eclipse.store.repository.access.AccessHelper;
import software.xdev.spring.data.eclipse.store.repository.query.ReflectedField;


/**
 * Creates a criteria from {@link Example}s. Needed to implement {@link QueryByExampleExecutor}.
 */
public class CriteriaByExample<T, S extends T> implements Criteria<T>
{
	private static final Map<String, Pattern> REGEX_EXAMPLE_CACHE = Collections.synchronizedMap(new WeakHashMap<>());
	private final Predicate<T> predicate;
	
	public CriteriaByExample(final Example<S> example)
	{
		if(example.getMatcher().isAllMatching())
		{
			this.predicate = entity -> this.getDefinedOrDefaultSpecifiers(example)
				.stream()
				.allMatch(this.createPredicateForSpecifier(example, entity));
		}
		else
		{
			this.predicate = entity -> this.getDefinedOrDefaultSpecifiers(example)
				.stream()
				.anyMatch(this.createPredicateForSpecifier(example, entity));
		}
	}
	
	private Collection<ExampleMatcher.PropertySpecifier> getDefinedOrDefaultSpecifiers(final Example<S> example)
	{
		final Collection<ExampleMatcher.PropertySpecifier> specifiers =
			example.getMatcher().getPropertySpecifiers().getSpecifiers();
		if(!specifiers.isEmpty())
		{
			return specifiers;
		}
		
		ExampleMatcher matcher = ExampleMatcher.matching();
		
		final Map<String, Field> allFields = AccessHelper.getInheritedPrivateFieldsByName(example.getProbeType());
		for(final String fieldName : allFields.keySet())
		{
			matcher = matcher.withMatcher(fieldName, ExampleMatcher.GenericPropertyMatchers.exact());
		}
		return matcher.getPropertySpecifiers().getSpecifiers();
	}
	
	@Override
	public boolean evaluate(final T object)
	{
		return this.predicate.test(object);
	}
	
	@SuppressWarnings("unchecked")
	private <E> Predicate<ExampleMatcher.PropertySpecifier> createPredicateForSpecifier(
		final Example<S> example,
		final E entity)
	{
		return specifier ->
		{
			final ReflectedField<E, Object> reflectedField =
				(ReflectedField<E, Object>)ReflectedField.createReflectedField(
					example.getProbeType(),
					specifier.getPath());
			
			final Object exampleValue = reflectedField.readValue((E)example.getProbe());
			final Optional<Object> transformedExampledValue =
				specifier.getPropertyValueTransformer().apply(Optional.ofNullable(exampleValue));
			
			if(transformedExampledValue.isEmpty())
			{
				return true;
			}
			
			final Object value = reflectedField.readValue(entity);
			final Optional<Object> transformedValue =
				specifier.getPropertyValueTransformer().apply(Optional.ofNullable(value));
			
			final ExampleMatcher.StringMatcher setOrDefaultMatcher = specifier.getStringMatcher() == null
				? example.getMatcher().getDefaultStringMatcher()
				: specifier.getStringMatcher();
			
			return this.createPredicateForStringMatcher(
				specifier,
				setOrDefaultMatcher,
				transformedExampledValue.get(),
				transformedValue.orElse(null));
		};
	}
	
	private boolean createPredicateForStringMatcher(
		final ExampleMatcher.PropertySpecifier specifier,
		final ExampleMatcher.StringMatcher setOrDefaultMatcher,
		final Object transformedExampledValue, // Never null
		final Object transformedValue) // Nullable
	{
		// Check exact matches
		if(ExampleMatcher.StringMatcher.DEFAULT.equals(setOrDefaultMatcher)
			|| ExampleMatcher.StringMatcher.EXACT.equals(setOrDefaultMatcher))
		{
			if(transformedExampledValue instanceof String)
			{
				return Objects.equals(
					this.valueToString(transformedExampledValue, specifier),
					this.valueToString(transformedValue, specifier));
			}
			return Objects.equals(transformedExampledValue, transformedValue);
		}
		
		// Check comparisons
		final BiPredicate<String, String> compareFunc = switch(setOrDefaultMatcher)
		{
			case STARTING -> String::startsWith;
			case ENDING -> String::endsWith;
			case CONTAINING -> String::contains;
			case REGEX -> (v, example) -> REGEX_EXAMPLE_CACHE.computeIfAbsent(example, Pattern::compile)
				.matcher(v)
				.find();
			default -> null;
		};
		
		return compareFunc != null
			&& Optional.ofNullable(this.valueToString(transformedValue, specifier))
			.map(v -> compareFunc.test(v, this.valueToString(transformedExampledValue, specifier)))
			.orElse(false);
	}
	
	private String valueToString(
		final Object value,
		final ExampleMatcher.PropertySpecifier specifier)
	{
		if(value == null)
		{
			return null;
		}
		
		return (specifier != null && Boolean.TRUE.equals(specifier.getIgnoreCase()))
			? value.toString().toLowerCase(Locale.ROOT)
			: value.toString();
	}
}
