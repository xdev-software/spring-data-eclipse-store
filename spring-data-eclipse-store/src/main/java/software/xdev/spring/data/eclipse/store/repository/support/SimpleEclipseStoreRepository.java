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
package software.xdev.spring.data.eclipse.store.repository.support;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import software.xdev.spring.data.eclipse.store.exceptions.FieldAccessReflectionException;
import software.xdev.spring.data.eclipse.store.exceptions.NoIdFieldFoundException;
import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;
import software.xdev.spring.data.eclipse.store.repository.access.modifier.FieldAccessModifier;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreCrudRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreListCrudRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreListPagingAndSortingRepositoryRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStorePagingAndSortingRepositoryRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreQueryByExampleExecutor;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository;
import software.xdev.spring.data.eclipse.store.repository.query.criteria.Criteria;
import software.xdev.spring.data.eclipse.store.repository.query.executors.ListQueryExecutor;
import software.xdev.spring.data.eclipse.store.repository.query.executors.PageableQueryExecutor;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopier;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopierResult;
import software.xdev.spring.data.eclipse.store.transactions.EclipseStoreTransaction;
import software.xdev.spring.data.eclipse.store.transactions.EclipseStoreTransactionManager;


public class SimpleEclipseStoreRepository<T, ID>
	implements
	EclipseStoreRepository<T, ID>,
	EclipseStorePagingAndSortingRepositoryRepository<T, ID>,
	EclipseStoreListPagingAndSortingRepositoryRepository<T, ID>,
	EclipseStoreCrudRepository<T, ID>,
	EclipseStoreListCrudRepository<T, ID>,
	EclipseStoreQueryByExampleExecutor<T>
{
	private static final Logger LOG = LoggerFactory.getLogger(SimpleEclipseStoreRepository.class);
	private final EclipseStoreStorage storage;
	private final Class<T> domainClass;
	private final WorkingCopier<T> copier;
	private final EclipseStoreTransactionManager transactionManager;
	private Field idField;
	
	public SimpleEclipseStoreRepository(
		final EclipseStoreStorage storage,
		final WorkingCopier<T> copier,
		final Class<T> domainClass,
		final EclipseStoreTransactionManager transactionManager)
	{
		this.storage = storage;
		this.domainClass = domainClass;
		this.storage.registerEntity(domainClass);
		this.copier = copier;
		this.transactionManager = transactionManager;
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
	public <S extends T> List<S> saveBulk(final Collection<S> entities)
	{
		final EclipseStoreTransaction transaction = this.transactionManager.getTransaction();
		transaction.addAction(() -> this.uncachedStore(entities));
		return (List<S>)entities;
	}
	
	private <S extends T> void uncachedStore(final Collection<S> entities)
	{
		this.storage.getReadWriteLock().write(
			() -> {
				if(LOG.isDebugEnabled())
				{
					LOG.debug("Saving {} entities...", entities.size());
				}
				final List<WorkingCopierResult<T>> results =
					this.checkEntityForNull(entities)
						.parallelStream()
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
			}
		);
	}
	
	@Override
	@Nonnull
	public <S extends T> S save(@Nonnull final S entity)
	{
		return this.storage.getReadWriteLock()
			.write(() -> this.saveBulk(List.of(this.checkEntityForNull(entity))).get(0));
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
		return this.storage.getReadWriteLock().write(
			() -> {
				final List<S> list = new ArrayList<>();
				this.checkEntityForNull(entities).forEach(list::add);
				return this.saveBulk(list);
			}
		);
	}
	
	@Override
	@Nonnull
	public Optional<T> findById(@Nonnull final ID id)
	{
		return this.storage.getReadWriteLock().read(
			() -> this.storage
				.getEntityList(this.domainClass)
				.parallelStream()
				.filter(
					entity ->
					{
						try(final FieldAccessModifier<T> fam = FieldAccessModifier.prepareForField(
							this.getIdField(),
							entity))
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
						return false;
					}
				)
				.findAny()
				.map(foundEntity -> this.copier.copy(foundEntity))
		);
	}
	
	@Override
	public boolean existsById(@Nonnull final ID id)
	{
		return this.findById(id).isPresent();
	}
	
	@Override
	@Nonnull
	public List<T> findAll()
	{
		// Must get copied as one list to keep same references objects the same.
		// (Example: If o1 and o2 (both part of the entity list) are referencing o3,
		// o3 should be the same no matter from where it is referenced.
		return this.copier.copy(this.storage.getEntityList(this.domainClass)).stream().toList();
	}
	
	@Override
	@Nonnull
	public List<T> findAllById(@Nonnull final Iterable<ID> idsToFind)
	{
		return this.storage.getReadWriteLock().read(
			// Must get copied as one list to keep same references objects the same.
			// (Example: If o1 and o2 (both part of the entity list) are referencing o3,
			// o3 should be the same no matter from where it is referenced.
			() -> this.copier.copy(
				this.storage
				.getEntityList(this.domainClass)
					.parallelStream()
				.filter(
					entity ->
					{
						try(final FieldAccessModifier<T> fam = FieldAccessModifier.prepareForField(
							this.getIdField(),
							entity))
						{
							final Object idOfEntity = fam.getValueOfField(entity);
							for(final ID idToFind : idsToFind)
							{
								if(idToFind.equals(idOfEntity))
								{
									return true;
								}
							}
						}
						catch(final Exception e)
						{
							throw new FieldAccessReflectionException(String.format(
								FieldAccessReflectionException.COULD_NOT_READ_FIELD,
								this.getIdField().getName()), e);
						}
						return false;
					}
				)
				.toList()
			)
		);
	}
	
	@Override
	public long count()
	{
		return this.storage.getEntityCount(this.domainClass);
	}
	
	@Override
	public void deleteById(@Nonnull final ID id)
	{
		this.storage.getReadWriteLock().write(
			() -> {
				final Optional<T> byId = this.findById(id);
				byId.ifPresent(this::delete);
			}
		);
	}
	
	@Override
	public void delete(@Nonnull final T entity)
	{
		final EclipseStoreTransaction transaction = this.transactionManager.getTransaction();
		transaction.addAction(() ->
			this.storage.getReadWriteLock().write(
				() -> {
					this.storage.delete(this.domainClass, this.copier.getOriginal(entity));
					this.copier.deregister(entity);
				}
			)
		);
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
		final EclipseStoreTransaction transaction = this.transactionManager.getTransaction();
		transaction.addAction(() -> this.storage.deleteAll(this.domainClass));
	}
	
	@Override
	@Nonnull
	public List<T> findAll(@Nonnull final Sort sort)
	{
		return this.storage.getReadWriteLock().read(
			() -> {
				final ListQueryExecutor<T> query = new ListQueryExecutor<>(this.copier, Criteria.createNoCriteria());
				return query.execute(
					this.domainClass,
					this.storage.getEntityList(this.domainClass),
					new Object[]{sort});
			}
		);
	}
	
	@Override
	@Nonnull
	public Page<T> findAll(@Nonnull final Pageable pageable)
	{
		return this.storage.getReadWriteLock().read(
			() -> {
				final PageableQueryExecutor<T> pageableQuery =
					new PageableQueryExecutor<>(this.copier, Criteria.createNoCriteria(), null);
				return pageableQuery.execute(
					this.domainClass,
					this.storage.getEntityList(this.domainClass),
					new Object[]{pageable});
			}
		);
	}
	
	@Override
	public <S extends T> Optional<S> findOne(final Example<S> example)
	{
		// TODO
		return Optional.empty();
	}
	
	@Override
	public <S extends T> Iterable<S> findAll(final Example<S> example)
	{
		// TODO
		return null;
	}
	
	@Override
	public <S extends T> Iterable<S> findAll(final Example<S> example, final Sort sort)
	{
		// TODO
		return null;
	}
	
	@Override
	public <S extends T> Page<S> findAll(final Example<S> example, final Pageable pageable)
	{
		// TODO
		return null;
	}
	
	@Override
	public <S extends T> long count(final Example<S> example)
	{
		// TODO
		return 0;
	}
	
	@Override
	public <S extends T> boolean exists(final Example<S> example)
	{
		// TODO
		return false;
	}
	
	@Override
	public <S extends T, R> R findBy(
		final Example<S> example,
		final Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction)
	{
		// TODO
		return null;
	}
}
