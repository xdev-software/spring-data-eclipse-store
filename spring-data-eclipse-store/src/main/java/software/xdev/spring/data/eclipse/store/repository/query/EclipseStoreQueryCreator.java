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

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.data.util.Streamable;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.ObjectUtils;

import software.xdev.spring.data.eclipse.store.repository.query.criteria.AbstractCriteriaNode;
import software.xdev.spring.data.eclipse.store.repository.query.criteria.Criteria;
import software.xdev.spring.data.eclipse.store.repository.query.criteria.CriteriaSingleNode;
import software.xdev.spring.data.eclipse.store.repository.query.executors.QueryExecutor;
import software.xdev.spring.data.eclipse.store.repository.query.executors.QueryExecutorCreator;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopier;


/**
 * Creates an {@link QueryExecutor} according to the given {@link PartTree}. This is done by building a tree with
 * {@link AbstractCriteriaNode}s. Only the root of the tree is returned and therefore evaluated.
 *
 * @param <T> of entities that are queried
 */
@Nonnull
public class EclipseStoreQueryCreator<T> extends AbstractQueryCreator<QueryExecutor<T>, AbstractCriteriaNode<T>>
{
	private final TypeInformation<?> typeInformation;
	private final Class<T> domainClass;
	private final WorkingCopier<T> copier;
	
	public EclipseStoreQueryCreator(
		final Class<T> domainClass,
		final TypeInformation<?> typeInformation,
		final WorkingCopier<T> copier,
		final PartTree tree,
		final ParameterAccessor parameters)
	{
		super(tree, parameters);
		this.domainClass = Objects.requireNonNull(domainClass);
		this.typeInformation = Objects.requireNonNull(typeInformation);
		this.copier = Objects.requireNonNull(copier);
	}
	
	@Override
	@Nonnull
	protected AbstractCriteriaNode<T> create(@Nonnull final Part part, @Nonnull final Iterator<Object> iterator)
	{
		Objects.requireNonNull(part);
		Objects.requireNonNull(iterator);
		return this.from(part, new CriteriaSingleNode<>(this.getDeclaredField(part)), iterator);
	}
	
	@Override
	@Nonnull
	protected AbstractCriteriaNode<T> and(
		@Nonnull final Part part,
		@Nullable final AbstractCriteriaNode<T> base,
		@Nonnull final Iterator<Object> iterator)
	{
		Objects.requireNonNull(part);
		Objects.requireNonNull(iterator);
		if(base == null)
		{
			return this.create(part, iterator);
		}
		
		return this.from(part, base.and(this.getDeclaredField(part)), iterator);
	}
	
	@Override
	@Nonnull
	protected AbstractCriteriaNode<T> or(
		@Nonnull final AbstractCriteriaNode<T> base,
		@Nonnull final AbstractCriteriaNode<T> criteria)
	{
		Objects.requireNonNull(base);
		Objects.requireNonNull(criteria);
		return base.orOperator(criteria);
	}
	
	@Override
	@Nonnull
	protected QueryExecutor<T> complete(final AbstractCriteriaNode<T> criteria, @Nonnull final Sort sort)
	{
		Objects.requireNonNull(sort);
		if(criteria == null)
		{
			return QueryExecutorCreator.createQuery(
				this.typeInformation,
				this.copier,
				Criteria.createNoCriteria(),
				sort);
		}
		return QueryExecutorCreator.createQuery(this.typeInformation, this.copier, criteria, sort);
	}
	
	@SuppressWarnings("PMD.CyclomaticComplexity")
	private AbstractCriteriaNode<T> from(
		final Part part,
		final AbstractCriteriaNode<T> criteria,
		@Nullable final Iterator<Object> parameters)
	{
		Objects.requireNonNull(criteria);
		final Part.Type type = Objects.requireNonNull(part).getType();
		final Part.IgnoreCaseType ignoreCaseType = part.shouldIgnoreCase();
		final boolean doIgnoreCase =
			ignoreCaseType == Part.IgnoreCaseType.ALWAYS || ignoreCaseType == Part.IgnoreCaseType.WHEN_POSSIBLE;
		
		switch(type)
		{
			case AFTER, GREATER_THAN ->
			{
				return criteria.gt(Objects.requireNonNull(parameters).next());
			}
			case GREATER_THAN_EQUAL ->
			{
				return criteria.gte(Objects.requireNonNull(parameters).next());
			}
			case BEFORE, LESS_THAN ->
			{
				return criteria.lt(Objects.requireNonNull(parameters).next());
			}
			case LESS_THAN_EQUAL ->
			{
				return criteria.lte(Objects.requireNonNull(parameters).next());
			}
			case BETWEEN ->
			{
				return criteria.between(
					Objects.requireNonNull(parameters).next(),
					Objects.requireNonNull(parameters).next());
			}
			case IS_NOT_NULL ->
			{
				return criteria.ne(null);
			}
			case IS_NULL ->
			{
				return criteria.is(null);
			}
			case NOT_IN ->
			{
				return criteria.nin(this.asStreamable(Objects.requireNonNull(parameters).next()));
			}
			case IN ->
			{
				return criteria.in(this.asStreamable(Objects.requireNonNull(parameters).next()));
			}
			case LIKE ->
			{
				return criteria.like((String)Objects.requireNonNull(parameters).next(), doIgnoreCase);
			}
			case STARTING_WITH ->
			{
				return criteria.startWith((String)Objects.requireNonNull(parameters).next(), doIgnoreCase);
			}
			case ENDING_WITH ->
			{
				return criteria.endWith((String)Objects.requireNonNull(parameters).next(), doIgnoreCase);
			}
			case CONTAINING ->
			{
				return criteria.containing((String)Objects.requireNonNull(parameters).next(), doIgnoreCase);
			}
			case NOT_LIKE ->
			{
				return criteria.notLike((String)Objects.requireNonNull(parameters).next(), doIgnoreCase);
			}
			case NOT_CONTAINING ->
			{
				return criteria.notContaining((String)Objects.requireNonNull(parameters).next(), doIgnoreCase);
			}
			case EXISTS ->
			{
				return criteria.exists((Boolean)Objects.requireNonNull(parameters).next());
			}
			case TRUE ->
			{
				return criteria.is(true);
			}
			case FALSE ->
			{
				return criteria.is(false);
			}
			case SIMPLE_PROPERTY ->
			{
				if(this.isSimpleComparisonPossible(part))
				{
					return criteria.is(Objects.requireNonNull(parameters).next());
				}
			}
			case NEGATING_SIMPLE_PROPERTY ->
			{
				if(this.isSimpleComparisonPossible(part))
				{
					return criteria.ne(Objects.requireNonNull(parameters).next());
				}
			}
			default -> throw new IllegalArgumentException("Unsupported keyword");
		}
		throw new IllegalArgumentException("Unsupported keyword");
	}
	
	private Streamable<?> asStreamable(final Object value)
	{
		
		if(value instanceof final Collection<?> collection)
		{
			return Streamable.of(collection);
		}
		else if(ObjectUtils.isArray(value))
		{
			return Streamable.of((Object[])value);
		}
		return Streamable.of(value);
	}
	
	private boolean isSimpleComparisonPossible(final Part part)
	{
		return switch(part.shouldIgnoreCase())
		{
			case NEVER -> true;
			case WHEN_POSSIBLE -> part.getProperty().getType() != String.class;
			case ALWAYS -> false;
		};
	}
	
	private ReflectedField<T, ?> getDeclaredField(final Part part)
	{
		final String fieldName = part.getProperty().getSegment();
		return ReflectedField.createReflectedField(this.domainClass, fieldName);
	}
}
