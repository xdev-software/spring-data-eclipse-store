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
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import software.xdev.spring.data.eclipse.store.repository.access.AccessHelper;
import software.xdev.spring.data.eclipse.store.repository.query.ReflectedField;


public class CriteriaByExample<T, S extends T> implements Criteria<T>
{
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
	
	private <T> Predicate<ExampleMatcher.PropertySpecifier> createPredicateForSpecifier(
		final Example<S> example,
		final T entity)
	{
		return specifier ->
		{
			final ReflectedField<T, Object> reflectedField =
				(ReflectedField<T, Object>)ReflectedField.createReflectedField(
					example.getProbeType(),
					specifier.getPath());
			
			final Object exampleValue = reflectedField.readValue((T)example.getProbe());
			final Optional<Object> transformedExampledValue =
				specifier.getPropertyValueTransformer().apply(Optional.ofNullable(exampleValue));
			
			if(transformedExampledValue.isEmpty())
			{
				return true;
			}
			
			final Object value = reflectedField.readValue(entity);
			final Optional<Object> transformedValue =
				specifier.getPropertyValueTransformer().apply(Optional.ofNullable(value));
			
			final ExampleMatcher.StringMatcher setOrDefaultMatcher = specifier.getStringMatcher() == null ?
				example.getMatcher().getDefaultStringMatcher() :
				specifier.getStringMatcher();
			
			switch(setOrDefaultMatcher)
			{
				case DEFAULT, EXACT ->
				{
					if(transformedExampledValue.get() instanceof String)
					{
						return this.valueToString(transformedValue, specifier).equals(this.valueToString(
							transformedExampledValue,
							specifier));
					}
					return transformedExampledValue.equals(transformedValue);
				}
				case STARTING ->
				{
					final Optional<String> valueAsString = this.valueToString(transformedValue, specifier);
					if(valueAsString.isEmpty())
					{
						return false;
					}
					return valueAsString.get()
						.startsWith(this.valueToString(transformedExampledValue, specifier).get());
				}
				case ENDING ->
				{
					final Optional<String> valueAsString = this.valueToString(transformedValue, specifier);
					if(valueAsString.isEmpty())
					{
						return false;
					}
					return valueAsString.get().endsWith(this.valueToString(transformedExampledValue, specifier).get());
				}
				case CONTAINING ->
				{
					final Optional<String> valueAsString = this.valueToString(transformedValue, specifier);
					if(valueAsString.isEmpty())
					{
						return false;
					}
					return valueAsString.get().contains(this.valueToString(transformedExampledValue, specifier).get());
				}
				case REGEX ->
				{
					final Optional<String> valueAsString = this.valueToString(transformedValue, specifier);
					if(valueAsString.isEmpty())
					{
						return false;
					}
					return Pattern.compile(
							this.valueToString(transformedExampledValue, specifier).get()
						)
						.matcher(valueAsString.get()).find();
				}
			}
			return true;
		};
	}
	
	private Optional<String> valueToString(
		final Optional<Object> value,
		final ExampleMatcher.PropertySpecifier specifier)
	{
		if(value.isEmpty())
		{
			return Optional.empty();
		}
		if(specifier != null && Boolean.TRUE.equals(specifier.getIgnoreCase()))
		{
			return Optional.of(value.get().toString().toLowerCase(Locale.ROOT));
		}
		return Optional.of(value.get().toString());
	}
}
