package software.xdev.spring.data.eclipse.store.repository.query.by.example;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;
import software.xdev.spring.data.eclipse.store.repository.query.criteria.CriteriaByExample;
import software.xdev.spring.data.eclipse.store.repository.query.executors.CountQueryExecutor;
import software.xdev.spring.data.eclipse.store.repository.query.executors.ExistsQueryExecutor;
import software.xdev.spring.data.eclipse.store.repository.query.executors.ListQueryExecutor;
import software.xdev.spring.data.eclipse.store.repository.query.executors.PageableQueryExecutor;
import software.xdev.spring.data.eclipse.store.repository.query.executors.SingleQueryExecutor;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopier;


/**
 * Needed to support {@link QueryByExampleExecutor}.
 */
public class EclipseStoreFetchableFluentQuery<T, S extends T> implements FluentQuery.FetchableFluentQuery<S>
{
	private final WorkingCopier<T> copier;
	private final Example<S> example;
	private final Class<T> domainClass;
	private final EclipseStoreStorage storage;
	private final Sort sort;
	
	public EclipseStoreFetchableFluentQuery(
		final WorkingCopier<T> copier,
		final Example<S> example,
		final Class<T> domainClass,
		final EclipseStoreStorage storage,
		final Sort sort
	)
	{
		this.copier = copier;
		this.example = example;
		this.domainClass = domainClass;
		this.storage = storage;
		this.sort = sort;
	}
	
	@Override
	public FetchableFluentQuery<S> sortBy(final Sort sort)
	{
		return new EclipseStoreFetchableFluentQuery(
			this.copier,
			this.example,
			this.domainClass,
			this.storage,
			sort
		);
	}
	
	@Override
	public <R> FetchableFluentQuery<R> as(final Class<R> resultType)
	{
		throw new UnsupportedOperationException("The method as() is not yet supported");
	}
	
	@Override
	public FetchableFluentQuery<S> project(final Collection<String> properties)
	{
		throw new UnsupportedOperationException("The method project() is not yet supported");
	}
	
	@Override
	public S oneValue()
	{
		return this.firstValue();
	}
	
	@Override
	public S firstValue()
	{
		final SingleQueryExecutor<T> query =
			new SingleQueryExecutor<>(this.copier, new CriteriaByExample<>((Example<T>)this.example), this.sort);
		return this.storage.getReadWriteLock().read(
			() ->
				(S)query.execute(
					this.domainClass,
					this.storage.getEntityList(this.domainClass),
					new Object[]{this.sort})
		);
	}
	
	@Override
	public List<S> all()
	{
		final ListQueryExecutor<T> query =
			new ListQueryExecutor<>(this.copier, new CriteriaByExample<>(this.example));
		return this.storage.getReadWriteLock().read(
			() -> (List<S>)query.execute(this.domainClass, this.storage.getEntityList(this.domainClass), new Object[]{
				this.sort})
		);
	}
	
	@Override
	public Page<S> page(final Pageable pageable)
	{
		final PageableQueryExecutor<T> pageableQuery =
			new PageableQueryExecutor<>(this.copier, new CriteriaByExample<>(this.example), this.sort);
		return this.storage.getReadWriteLock().read(
			() ->
				(Page<S>)pageableQuery.execute(
					this.domainClass,
					this.storage.getEntityList(this.domainClass),
					new Object[]{pageable, this.sort})
		);
	}
	
	@Override
	public Stream<S> stream()
	{
		return this.all().stream();
	}
	
	@Override
	public long count()
	{
		final CountQueryExecutor<T> query = new CountQueryExecutor<>(new CriteriaByExample<>(this.example));
		return this.storage.getReadWriteLock().read(
			() -> query.execute(this.domainClass, this.storage.getEntityList(this.domainClass), null)
		);
	}
	
	@Override
	public boolean exists()
	{
		final ExistsQueryExecutor<T> query = new ExistsQueryExecutor<>(new CriteriaByExample<>(this.example));
		return this.storage.getReadWriteLock().read(
			() -> query.execute(this.domainClass, this.storage.getEntityList(this.domainClass), null)
		);
	}
}
