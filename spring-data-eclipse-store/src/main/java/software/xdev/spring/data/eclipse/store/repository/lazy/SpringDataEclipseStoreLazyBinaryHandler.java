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
package software.xdev.spring.data.eclipse.store.repository.lazy;

import java.lang.reflect.Constructor;
import java.util.Objects;

import org.eclipse.serializer.memory.XMemory;
import org.eclipse.serializer.persistence.binary.types.AbstractBinaryHandlerCustom;
import org.eclipse.serializer.persistence.binary.types.Binary;
import org.eclipse.serializer.persistence.binary.types.BinaryTypeHandler;
import org.eclipse.serializer.persistence.types.PersistenceLoadHandler;
import org.eclipse.serializer.persistence.types.PersistenceReferenceLoader;
import org.eclipse.serializer.persistence.types.PersistenceStoreHandler;
import org.eclipse.serializer.reference.Lazy;
import org.eclipse.serializer.reference.ObjectSwizzling;
import org.eclipse.serializer.reflect.XReflect;

import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopier;


/**
 * This is a complicated one. First off: this handler should only be used for WorkingCopies (see
 * {@link software.xdev.spring.data.eclipse.store.repository.support.copier.registering.EclipseSerializerRegisteringCopier})!
 * <p>
 *     First case:<br/>
 *     The user creates a {@link SpringDataEclipseStoreLazy} and puts a object in it.
 *     This object is stored as with a default {@link BinaryTypeHandler}. But when it gets loaded,
 *     it <b>does not</b> load as the stored object, but it gets wrapped in a {@link Lazy#Reference(Object)}.
 * </p>
 * <p>
 *     Second case:<br/>
 *     The actual lazy object gets loaded from the actual storage. In this case the {@link ObjectSwizzling} is
 *     important! It's the actual {@link ObjectSwizzling} from the storage (not from the
 * {@link software.xdev.spring.data.eclipse.store.repository.support.copier.registering.EclipseSerializerRegisteringCopier}).
 *     This means, the {@link SpringDataEclipseStoreLazy} holds the objectId of the original lazy in the original
 *     storage.
 *     Therefore if {@link SpringDataEclipseStoreLazy#get()} is called a new working copy of the lazy from the
 *     storage is loaded.
 * </p>
 */
public final class SpringDataEclipseStoreLazyBinaryHandler
	extends AbstractBinaryHandlerCustom<SpringDataEclipseStoreLazy.Default<?>>
{
	@SuppressWarnings("rawtypes")
	static final Constructor<SpringDataEclipseStoreLazy.Default> CONSTRUCTOR_SURROGATE_LAZY = XReflect.setAccessible(
		XReflect.getDeclaredConstructor(
			SpringDataEclipseStoreLazy.Default.class,
			long.class,
			ObjectSwizzling.class,
			WorkingCopier.class
		)
	);
	
	private static final int OFFSET_UNWRAPPED_OBJECT = 8;
	private static final int OFFSET_LAZY = 0;
	
	private final ObjectSwizzling originalStoreLoader;
	private final WorkingCopier<?> copier;
	
	public SpringDataEclipseStoreLazyBinaryHandler(
		final ObjectSwizzling originalStoreLoader,
		final WorkingCopier<?> copier)
	{
		super(
			// Cast is necessary for the compiler
			(Class)SpringDataEclipseStoreLazy.Default.class,
			CustomFields(
				CustomField(Object.class, "lazySubject"),
				CustomField(Object.class, "unwrappedSubject")
			)
		);
		this.originalStoreLoader = Objects.requireNonNull(originalStoreLoader);
		this.copier = Objects.requireNonNull(copier);
	}
	
	@Override
	public void store(
		final Binary data,
		final SpringDataEclipseStoreLazy.Default<?> instance,
		final long objectId,
		final PersistenceStoreHandler<Binary> handler
	)
	{
		data.storeEntityHeader(Binary.referenceBinaryLength(2), this.typeId(), objectId);
		if(instance.isOriginalObject())
		{
			// Store unwrapped Object
			data.store_long(OFFSET_UNWRAPPED_OBJECT, handler.applyEager(instance.getObjectToBeWrapped()));
		}
		else
		{
			// Store only reference to lazy
			data.store_long(OFFSET_LAZY, instance.objectId());
		}
		instance.setStored();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SpringDataEclipseStoreLazy.Default<?> create(final Binary data, final PersistenceLoadHandler handler)
	{
		final long objectIdOfLazy = data.read_long(OFFSET_LAZY);
		final long objectIdOfUnwrappedObject = data.read_long(OFFSET_UNWRAPPED_OBJECT);
		
		if(objectIdOfUnwrappedObject == 0)
		{
			return Lazy.register(
				XReflect.invoke(CONSTRUCTOR_SURROGATE_LAZY, objectIdOfLazy, this.originalStoreLoader, this.copier)
			);
		}
		return XMemory.instantiateBlank(SpringDataEclipseStoreLazy.Default.class);
	}
	
	@Override
	public void updateState(
		final Binary data,
		final SpringDataEclipseStoreLazy.Default<?> instance,
		final PersistenceLoadHandler handler
	)
	{
		this.updateStateT(data, instance, handler);
	}
	
	private <T> void updateStateT(
		final Binary data,
		final SpringDataEclipseStoreLazy.Default<T> instance,
		final PersistenceLoadHandler handler
	)
	{
		final long objectIdOfUnwrappedObject = data.read_long(OFFSET_UNWRAPPED_OBJECT);
		
		if(objectIdOfUnwrappedObject != 0)
		{
			instance.setWrappedLazy(Lazy.Reference((T)handler.lookupObject(objectIdOfUnwrappedObject)));
			// Is already stored in the main storage.
			instance.setStored();
		}
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
		final Binary data,
		final PersistenceReferenceLoader iterator
	)
	{
		final long objectIdOfUnwrappedObject = data.read_long(OFFSET_UNWRAPPED_OBJECT);
		
		if(objectIdOfUnwrappedObject != 0)
		{
			iterator.acceptObjectId(objectIdOfUnwrappedObject);
		}
	}
}
