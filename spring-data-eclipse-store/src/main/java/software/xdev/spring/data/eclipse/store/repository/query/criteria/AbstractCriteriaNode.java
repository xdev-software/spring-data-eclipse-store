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
package software.xdev.spring.data.eclipse.store.repository.query.criteria;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.function.Predicate;

import org.springframework.data.util.Streamable;
import org.springframework.lang.Nullable;

import software.xdev.spring.data.eclipse.store.repository.query.ReflectedField;
import software.xdev.spring.data.eclipse.store.util.GenericObjectComparer;


/**
 * Criteria to apply to an entity and check if the criteria is fulfilled or not.
 *
 * @param <T> entity-type to apply the criteria to.
 */
public abstract class AbstractCriteriaNode<T> implements Criteria<T>
{
	private final ReflectedField<T, ?> field;
	final LinkedHashSet<Predicate<T>> predicates = new LinkedHashSet<>();
	
	protected AbstractCriteriaNode(@Nullable final ReflectedField<T, ?> field)
	{
		this.field = field;
	}
	
	public AbstractCriteriaNode<T> and(final ReflectedField<T, ?> field)
	{
		return new CriteriaAndNode<>(
			this,
			Objects.requireNonNull(field));
	}
	
	public AbstractCriteriaNode<T> orOperator(final AbstractCriteriaNode<T> criteria)
	{
		Objects.requireNonNull(criteria);
		return new CriteriaOrNode<>(this, criteria);
	}
	
	public AbstractCriteriaNode<T> is(@Nullable final Object value)
	{
		this.predicates.add(entity -> {
			final Object fieldValue = Objects.requireNonNull(this.field).readValue(entity);
			return Objects.equals(fieldValue, value);
		});
		return this;
	}
	
	public AbstractCriteriaNode<T> ne(@Nullable final Object value)
	{
		this.predicates.add(entity -> {
			final Object fieldValue = Objects.requireNonNull(this.field).readValue(entity);
			return !Objects.equals(fieldValue, value);
		});
		return this;
	}
	
	public AbstractCriteriaNode<T> lt(final Object value)
	{
		Objects.requireNonNull(value);
		this.predicates.add(entity ->
			GenericObjectComparer.isLessThan(Objects.requireNonNull(this.field).readValue(entity), value)
		);
		return this;
	}
	
	public AbstractCriteriaNode<T> lte(final Object value)
	{
		Objects.requireNonNull(value);
		this.predicates.add(entity -> GenericObjectComparer.isLessOrEqualTo(Objects.requireNonNull(this.field)
			.readValue(entity), value));
		return this;
	}
	
	public AbstractCriteriaNode<T> gt(final Object value)
	{
		Objects.requireNonNull(value);
		this.predicates.add(entity -> GenericObjectComparer.isGreaterThan(Objects.requireNonNull(this.field)
			.readValue(entity), value));
		return this;
	}
	
	public AbstractCriteriaNode<T> gte(final Object value)
	{
		Objects.requireNonNull(value);
		this.predicates.add(entity -> GenericObjectComparer.isGreaterOrEqualTo(Objects.requireNonNull(this.field)
			.readValue(entity), value));
		return this;
	}
	
	public AbstractCriteriaNode<T> between(final Object minValue, final Object maxValue)
	{
		Objects.requireNonNull(minValue);
		Objects.requireNonNull(maxValue);
		this.predicates.add(entity -> {
			final Object value = Objects.requireNonNull(this.field).readValue(entity);
			return GenericObjectComparer.isLessOrEqualTo(value, maxValue) &&
				GenericObjectComparer.isGreaterOrEqualTo(value, minValue);
		});
		return this;
	}
	
	public AbstractCriteriaNode<T> in(final Streamable<?> values)
	{
		this.predicates.add(entity -> {
			if(values == null)
			{
				return false;
			}
			return values.toSet().contains(Objects.requireNonNull(this.field).readValue(entity));
		});
		return this;
	}
	
	public AbstractCriteriaNode<T> nin(final Streamable<?> values)
	{
		this.predicates.add(entity -> {
			if(values == null)
			{
				return true;
			}
			return !values.toSet().contains(Objects.requireNonNull(this.field).readValue(entity));
		});
		return this;
	}
	
	public AbstractCriteriaNode<T> exists(final boolean value)
	{
		this.predicates.add(entity -> value == (Objects.requireNonNull(this.field).readValue(entity) != null));
		return this;
	}
	
	public AbstractCriteriaNode<T> like(final String like)
	{
		final String completeRegex = sqlLikeStringToRegex(like);
		this.predicates.add(entity -> {
			final String fieldValue = (String)Objects.requireNonNull(this.field).readValue(entity);
			return fieldValue != null && fieldValue.toUpperCase().matches(completeRegex);
		});
		return this;
	}
	
	private static String sqlLikeStringToRegex(final String like)
	{
		String regex = like.toUpperCase();
		regex = regex.replace(".", "\\.");
		regex = regex.replace("_", ".");
		return regex.replace("%", ".*");
	}
	
	public AbstractCriteriaNode<T> startWith(final String startString)
	{
		return this.like(startString + "%");
	}
	
	public AbstractCriteriaNode<T> endWith(final String endString)
	{
		return this.like("%" + endString);
	}
	
	public AbstractCriteriaNode<T> containing(final String containedString)
	{
		return this.like("%" + containedString + "%");
	}
	
	public AbstractCriteriaNode<T> notLike(final String notLikeString)
	{
		final String completeRegex = sqlLikeStringToRegex(notLikeString);
		this.predicates.add(entity -> {
			final String fieldValue = (String)Objects.requireNonNull(this.field).readValue(entity);
			return fieldValue != null && !fieldValue.toUpperCase().matches(completeRegex);
		});
		return this;
	}
	
	public AbstractCriteriaNode<T> notContaining(final String containedString)
	{
		return this.notLike("%" + containedString + "%");
	}
}
