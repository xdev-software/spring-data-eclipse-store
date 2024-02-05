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
package software.xdev.spring.data.eclipse.store.repository.support;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import software.xdev.spring.data.eclipse.store.exceptions.FieldAccessReflectionException;
import software.xdev.spring.data.eclipse.store.exceptions.NoIdFieldFoundException;
import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;
import software.xdev.spring.data.eclipse.store.repository.access.modifier.FieldAccessModifier;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreCrudRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreListCrudRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreListPagingAndSortingRepositoryRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStorePagingAndSortingRepositoryRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository;
import software.xdev.spring.data.eclipse.store.repository.query.criteria.Criteria;
import software.xdev.spring.data.eclipse.store.repository.query.executors.ListQueryExecutor;
import software.xdev.spring.data.eclipse.store.repository.query.executors.PageableQueryExecutor;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopier;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopierResult;


public class SimpleEclipseStoreRepository<T, ID>
	implements
	EclipseStoreRepository<T, ID>,
	EclipseStorePagingAndSortingRepositoryRepository<T, ID>,
	EclipseStoreListPagingAndSortingRepositoryRepository<T, ID>,
	EclipseStoreCrudRepository<T, ID>,
	EclipseStoreListCrudRepository<T, ID>
{
	private static final Logger LOG = LoggerFactory.getLogger(SimpleEclipseStoreRepository.class);
	private final EclipseStoreStorage storage;
	private final Class<T> domainClass;
	private final WorkingCopier<T> copier;
	private Field idField;
	
	public SimpleEclipseStoreRepository(
		final EclipseStoreStorage storage,
		final WorkingCopier<T> copier,
		final Class<T> domainClass)
	{
		this.storage = storage;
		this.domainClass = domainClass;
		this.storage.registerEntity(domainClass);
		this.copier = copier;
	}
	
	public Field getIdField()
	{
		if(this.idField == null)
		{
			final Optional<Field> foundIdField = IdFieldFinder.findIdField(this.domainClass);
			if(foundIdField.isEmpty())
			{
				throw new NoIdFieldFoundException(String.format(
					"Could not find id field in class %s",
					this.domainClass.getSimpleName()));
			}
			this.idField = foundIdField.get();
		}
		return this.idField;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized <S extends T> List<S> saveBulk(final Collection<S> entities)
	{
		if(LOG.isDebugEnabled())
		{
			LOG.debug("Saving {} entities...", entities.size());
		}
		final List<WorkingCopierResult<T>> results =
			this.checkEntityForNull(entities)
				.stream()
				.map(this.copier::mergeBack)
				.toList();
		final Set<Object> nonEntitiesToStore =
			results
				.stream()
				.map(WorkingCopierResult::nonEntitiesToStore)
				.flatMap(Collection::stream)
				.collect(Collectors.toUnmodifiableSet());
		final List<T> entitiesToStore =
			results
				.stream()
				.map(WorkingCopierResult::originalEntities)
				.flatMap(Collection::stream)
				.toList();
		if(LOG.isDebugEnabled())
		{
			LOG.debug("Collected {} non-entities to store.", nonEntitiesToStore.size());
		}
		this.storage.store(nonEntitiesToStore, this.domainClass, entitiesToStore);
		return (List<S>)entitiesToStore;
	}
	
	@Override
	@Nonnull
	public synchronized <S extends T> S save(@Nonnull final S entity)
	{
		return this.saveBulk(List.of(this.checkEntityForNull(entity))).get(0);
	}
	
	private <S> S checkEntityForNull(final S entity)
	{
		if(entity == null)
		{
			throw new IllegalArgumentException("Entity must not be null");
		}
		return entity;
	}
	
	@Override
	@Nonnull
	public <S extends T> List<S> saveAll(@Nonnull final Iterable<S> entities)
	{
		final List<S> list = new ArrayList<>();
		this.checkEntityForNull(entities).forEach(list::add);
		return this.saveBulk(list);
	}
	
	@Override
	@Nonnull
	public Optional<T> findById(@Nonnull final ID id)
	{
		for(final T entity : this.storage.getEntityList(this.domainClass))
		{
			try(final FieldAccessModifier<T> fam = FieldAccessModifier.makeFieldReadable(this.getIdField(), entity))
			{
				if(id.equals(fam.getValueOfField(entity)))
				{
					return Optional.of(this.copier.copy(entity));
				}
			}
			catch(final Exception e)
			{
				throw new FieldAccessReflectionException(String.format(
					FieldAccessReflectionException.COULD_NOT_READ_FIELD,
					this.getIdField().getName()), e);
			}
		}
		return Optional.empty();
	}
	
	@Override
	public boolean existsById(@Nonnull final ID id)
	{
		for(final T entity : this.storage.getEntityList(this.domainClass))
		{
			try(final FieldAccessModifier<T> fam = FieldAccessModifier.makeFieldReadable(this.getIdField(), entity))
			{
				if(id.equals(fam.getValueOfField(entity)))
				{
					return true;
				}
			}
			catch(final Exception e)
			{
				throw new FieldAccessReflectionException(String.format(
					FieldAccessReflectionException.COULD_NOT_READ_FIELD,
					this.getIdField().getName()), e);
			}
		}
		return false;
	}
	
	@Override
	@Nonnull
	public List<T> findAll()
	{
		return this.copier.copy(this.storage.getEntityList(this.domainClass)).stream().toList();
	}
	
	@Override
	@Nonnull
	public List<T> findAllById(@Nonnull final Iterable<ID> ids)
	{
		final List<T> foundEntities = new ArrayList<>();
		for(final T entity : this.storage.getEntityList(this.domainClass))
		{
			try(final FieldAccessModifier<T> fam = FieldAccessModifier.makeFieldReadable(this.getIdField(), entity))
			{
				for(final ID id : ids)
				{
					if(id.equals(fam.getValueOfField(entity)))
					{
						foundEntities.add(this.copier.copy(entity));
					}
				}
			}
			catch(final Exception e)
			{
				throw new FieldAccessReflectionException(String.format(
					FieldAccessReflectionException.COULD_NOT_READ_FIELD,
					this.getIdField().getName()), e);
			}
		}
		return foundEntities;
	}
	
	@Override
	public long count()
	{
		return this.storage.getEntityList(this.domainClass).size();
	}
	
	@Override
	public void deleteById(@Nonnull final ID id)
	{
		final Optional<T> byId = this.findById(id);
		byId.ifPresent(this::delete);
	}
	
	@Override
	public void delete(@Nonnull final T entity)
	{
		this.storage.delete(this.domainClass, this.copier.getOriginal(entity));
		this.copier.deregister(entity);
	}
	
	@Override
	public void deleteAllById(final Iterable<? extends ID> ids)
	{
		for(final ID id : ids)
		{
			this.deleteById(id);
		}
	}
	
	@Override
	public void deleteAll(final Iterable<? extends T> entities)
	{
		for(final T entity : entities)
		{
			this.delete(entity);
		}
	}
	
	@Override
	public void deleteAll()
	{
		this.storage.deleteAll(this.domainClass);
	}
	
	@Override
	@Nonnull
	public List<T> findAll(@Nonnull final Sort sort)
	{
		final ListQueryExecutor<T> query = new ListQueryExecutor<>(this.copier, Criteria.createNoCriteria());
		return query.execute(this.domainClass, this.storage.getEntityList(this.domainClass), new Object[]{sort});
	}
	
	@Override
	@Nonnull
	public Page<T> findAll(@Nonnull final Pageable pageable)
	{
		final PageableQueryExecutor<T> pageableQuery =
			new PageableQueryExecutor<>(this.copier, Criteria.createNoCriteria(), null);
		return pageableQuery.execute(
			this.domainClass,
			this.storage.getEntityList(this.domainClass),
			new Object[]{pageable});
	}
}
