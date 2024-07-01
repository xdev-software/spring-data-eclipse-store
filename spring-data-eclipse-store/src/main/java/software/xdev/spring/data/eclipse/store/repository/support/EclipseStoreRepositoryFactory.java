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

import java.util.Optional;

import jakarta.annotation.Nonnull;

import org.springframework.data.mapping.model.BasicPersistentEntity;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.PersistentEntityInformation;
import org.springframework.data.repository.core.support.RepositoryComposition;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;

import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;
import software.xdev.spring.data.eclipse.store.repository.SupportedChecker;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.RecursiveWorkingCopier;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopier;


/**
 * Creates the correct repository instance for repository-interfaces.
 */
public class EclipseStoreRepositoryFactory extends RepositoryFactorySupport
{
	private final EclipseStoreStorage storage;
	private final PlatformTransactionManager transactionManager;
	
	public EclipseStoreRepositoryFactory(
		final EclipseStoreStorage storage,
		final PlatformTransactionManager transactionManager)
	{
		this.storage = storage;
		this.transactionManager = transactionManager;
	}
	
	@Override
	@Nonnull
	public <T, ID> EntityInformation<T, ID> getEntityInformation(@Nonnull final Class<T> domainClass)
	{
		return new PersistentEntityInformation<>(new BasicPersistentEntity<>(TypeInformation.of(domainClass)));
	}
	
	@Override
	@Nonnull
	protected Optional<QueryLookupStrategy> getQueryLookupStrategy(
		@Nullable final QueryLookupStrategy.Key key,
		@Nonnull final QueryMethodEvaluationContextProvider evaluationContextProvider)
	{
		return Optional.of(new EclipseStoreQueryLookupStrategy(this.storage, this::createWorkingCopier));
	}
	
	private <T> WorkingCopier<T> createWorkingCopier(
		final Class<T> domainType,
		final EclipseStoreStorage storage)
	{
		return new RecursiveWorkingCopier<>(
			domainType,
			storage.getRegistry(),
			storage,
			storage,
			new SupportedChecker.Implementation(),
			storage
		);
	}
	
	@Override
	@Nonnull
	protected Object getTargetRepository(@Nonnull final RepositoryInformation metadata)
	{
		final SimpleEclipseStoreRepository<?, ?> existingRepository =
			this.storage.getRepository(metadata.getDomainType());
		if(existingRepository != null)
		{
			return existingRepository;
		}
		
		return this.getTargetRepositoryViaReflection(
			metadata,
			this.storage,
			this.createWorkingCopier(metadata.getDomainType(), this.storage),
			metadata.getDomainType(),
			this.transactionManager,
			this.storage.ensureIdManager(metadata.getDomainType())
		);
	}
	
	@Override
	@Nonnull
	protected Class<?> getRepositoryBaseClass(@Nonnull final RepositoryMetadata metadata)
	{
		return SimpleEclipseStoreRepository.class;
	}
	
	@Override
	@Nonnull
	protected RepositoryInformation getRepositoryInformation(
		@Nonnull final RepositoryMetadata metadata,
		@Nonnull final RepositoryComposition.RepositoryFragments fragments)
	{
		return super.getRepositoryInformation(metadata, fragments);
	}
	
	@Override
	@Nonnull
	public <T> T getRepository(
		@Nonnull final Class<T> repositoryInterface,
		@Nonnull final RepositoryComposition.RepositoryFragments fragments)
	{
		return super.getRepository(repositoryInterface, fragments);
	}
}
