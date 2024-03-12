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
package software.xdev.spring.data.eclipse.store.repository.lazy;

import java.lang.reflect.Constructor;
import java.util.Objects;

import org.eclipse.serializer.persistence.binary.types.AbstractBinaryHandlerCustom;
import org.eclipse.serializer.persistence.binary.types.Binary;
import org.eclipse.serializer.persistence.types.PersistenceLoadHandler;
import org.eclipse.serializer.persistence.types.PersistenceReferenceLoader;
import org.eclipse.serializer.persistence.types.PersistenceStoreHandler;
import org.eclipse.serializer.reference.Lazy;
import org.eclipse.serializer.reference.ObjectSwizzling;
import org.eclipse.serializer.reflect.XReflect;


/**
 * Copied from
 * {@link org.eclipse.serializer.persistence.binary.org.eclipse.serializer.reference.BinaryHandlerLazyDefault}.
 */
public final class SpringDataEclipseStoreLazyBinaryHandler
	extends AbstractBinaryHandlerCustom<SpringDataEclipseStoreLazy.Default<?>>
{
	@SuppressWarnings("rawtypes")
	static final Constructor<SpringDataEclipseStoreLazy.Default> CONSTRUCTOR = XReflect.setAccessible(
		XReflect.getDeclaredConstructor(
			SpringDataEclipseStoreLazy.Default.class,
			long.class,
			ObjectSwizzling.class
		)
	);
	
	private final ObjectSwizzling objectSwizzling;
	
	public SpringDataEclipseStoreLazyBinaryHandler(final ObjectSwizzling objectSwizzling)
	{
		super(
			SpringDataEclipseStoreLazy.Default.genericType(),
			CustomFields(
				CustomField(Object.class, "subject")
			)
		);
		this.objectSwizzling = Objects.requireNonNull(objectSwizzling);
	}
	
	@Override
	public final void store(
		final Binary data,
		final SpringDataEclipseStoreLazy.Default<?> instance,
		final long objectId,
		final PersistenceStoreHandler<Binary> handler
	)
	{
		final long referenceOid = instance.objectId();
		data.storeEntityHeader(Binary.referenceBinaryLength(1), this.typeId(), objectId);
		data.store_long(referenceOid);
		instance.setStored();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public final SpringDataEclipseStoreLazy.Default<?> create(final Binary data, final PersistenceLoadHandler handler)
	{
		final long objectId = data.read_long(0);
		
		return Lazy.register(
			XReflect.invoke(CONSTRUCTOR, objectId, this.objectSwizzling)
		);
	}
	
	@Override
	public final void updateState(
		final Binary data,
		final SpringDataEclipseStoreLazy.Default<?> instance,
		final PersistenceLoadHandler handler
	)
	{
		// no-op
	}
	
	@Override
	public final void complete(
		final Binary data,
		final SpringDataEclipseStoreLazy.Default<?> instance,
		final PersistenceLoadHandler handler
	)
	{
		// no-op for normal implementation (see non-reference-hashing collections for other examples)
	}
	
	@Override
	public final boolean hasPersistedReferences()
	{
		return true;
	}
	
	@Override
	public final boolean hasPersistedVariableLength()
	{
		return false;
	}
	
	@Override
	public final boolean hasVaryingPersistedLengthInstances()
	{
		return false;
	}
	
	@Override
	public final void iterateLoadableReferences(
		final Binary offset,
		final PersistenceReferenceLoader iterator
	)
	{
		// the lazy reference is not naturally loadable, but special-handled by this handler
	}
}
