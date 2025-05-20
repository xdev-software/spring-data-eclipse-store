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
package software.xdev.spring.data.eclipse.store.repository.support.copier.registering;

import jakarta.validation.Validator;

import org.eclipse.serializer.Serializer;
import org.eclipse.serializer.SerializerFoundation;
import org.eclipse.serializer.persistence.binary.jdk17.java.util.BinaryHandlerImmutableCollectionsList12;
import org.eclipse.serializer.persistence.binary.jdk17.java.util.BinaryHandlerImmutableCollectionsSet12;
import org.eclipse.serializer.persistence.binary.types.Binary;
import org.eclipse.serializer.persistence.types.PersistenceManager;
import org.eclipse.serializer.reference.ObjectSwizzling;
import org.eclipse.serializer.reference.Reference;
import org.eclipse.serializer.reflect.ClassLoaderProvider;
import org.eclipse.serializer.util.X;

import software.xdev.spring.data.eclipse.store.repository.SupportedChecker;
import software.xdev.spring.data.eclipse.store.repository.lazy.SpringDataEclipseStoreLazyBinaryHandler;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopier;


/**
 * This class registers storage instances and copy them for working copies. Utilizes
 * {@link EclipseSerializerRegisteringCopier}.
 */
public abstract class AbstractRegisteringCopier implements RegisteringObjectCopier
{
	private final EclipseSerializerRegisteringCopier actualCopier;
	
	protected AbstractRegisteringCopier(
		final SupportedChecker supportedChecker,
		final RegisteringWorkingCopyAndOriginal register,
		final ObjectSwizzling objectSwizzling,
		final WorkingCopier<?> copier,
		final Validator validator,
		final ClassLoaderProvider currentClassLoaderProvider
	)
	{
		this.actualCopier = new EclipseSerializerRegisteringCopier(
			supportedChecker,
			register,
			() ->
			this.createPersistenceManager(
				this.createSerializerFoundation(),
				objectSwizzling,
				copier,
				currentClassLoaderProvider
			),
			validator
		);
	}
	
	private PersistenceManager<Binary> createPersistenceManager(
		final SerializerFoundation<?> serializerFoundation,
		final ObjectSwizzling objectSwizzling,
		final WorkingCopier<?> copier,
		final ClassLoaderProvider currentClassLoaderProvider)
	{
		return serializerFoundation
			.setClassLoaderProvider(currentClassLoaderProvider)
			.registerCustomTypeHandler(BinaryHandlerImmutableCollectionsSet12.New())
			.registerCustomTypeHandler(BinaryHandlerImmutableCollectionsList12.New())
			.registerCustomTypeHandlers(new SpringDataEclipseStoreLazyBinaryHandler(objectSwizzling, copier))
			.createPersistenceManager();
	}
	
	@SuppressWarnings("java:S1452")
	protected SerializerFoundation<?> createSerializerFoundation()
	{
		final Reference<Binary> buffer = X.Reference(null);
		final Serializer.Source source = () -> X.Constant(buffer.get());
		final Serializer.Target target = buffer::set;
		
		return SerializerFoundation.New()
			.setPersistenceSource(source)
			.setPersistenceTarget(target)
			// Make every type persistable.
			// This is quite dangerous!
			// But if this is not set we get problems e.g. with HashMap$Node
			.setTypeEvaluatorPersistable(a -> true);
	}
	
	@Override
	public <T> T copy(final T source)
	{
		return this.actualCopier.copy(source);
	}
	
	@Override
	public void close()
	{
		this.actualCopier.close();
	}
}
