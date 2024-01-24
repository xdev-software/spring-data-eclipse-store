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
package software.xdev.spring.data.eclipse.store.repository.support.copier.object;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.serializer.Serializer;
import org.eclipse.serializer.SerializerFoundation;
import org.eclipse.serializer.persistence.binary.jdk17.java.util.BinaryHandlerImmutableCollectionsList12;
import org.eclipse.serializer.persistence.binary.jdk17.java.util.BinaryHandlerImmutableCollectionsSet12;
import org.eclipse.serializer.persistence.binary.types.Binary;
import org.eclipse.serializer.persistence.binary.types.BinaryStorer;
import org.eclipse.serializer.persistence.types.PersistenceLoader;
import org.eclipse.serializer.persistence.types.PersistenceManager;
import org.eclipse.serializer.persistence.types.PersistenceStorer;
import org.eclipse.serializer.reference.Reference;
import org.eclipse.serializer.util.X;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.xdev.spring.data.eclipse.store.repository.WorkingCopyRegistry;
import software.xdev.spring.data.eclipse.store.repository.support.copier.DataTypeUtil;


/**
 * This class utilizes EclipseStore-Serialization and copies objects by serializing and deserializing objects in
 * memory.
 */
public class EclipseSerializerRegisteringCopier implements RegisteringObjectCopier
{
	private static final Logger LOG = LoggerFactory.getLogger(EclipseSerializerRegisteringCopier.class);
	private final SerializerFoundation<?> foundation;
	private PersistenceManager<Binary> persistenceManager;
	
	private final WorkingCopyRegistry registry;
	
	public EclipseSerializerRegisteringCopier(final WorkingCopyRegistry registry)
	{
		final SerializerFoundation<?> newFoundation = SerializerFoundation.New();
		newFoundation.registerCustomTypeHandler(BinaryHandlerImmutableCollectionsSet12.New());
		newFoundation.registerCustomTypeHandler(BinaryHandlerImmutableCollectionsList12.New());
		this.foundation = newFoundation;
		this.registry = registry;
	}
	
	@Override
	public synchronized <T> T copy(final T source)
	{
		return this.copy(source, false);
	}
	
	@Override
	public synchronized void close()
	{
		if(this.persistenceManager != null)
		{
			this.persistenceManager.objectRegistry().clearAll();
			this.persistenceManager.close();
			this.persistenceManager = null;
		}
	}
	
	private void lazyInit()
	{
		if(this.persistenceManager == null)
		{
			final Reference<Binary> buffer = X.Reference(null);
			final Serializer.Source source = () -> X.Constant(buffer.get());
			final Serializer.Target target = buffer::set;
			this.persistenceManager =
				(((SerializerFoundation<?>)this.foundation.setPersistenceSource(source))
					.setPersistenceTarget(target))
					// Make every type persistable.
					// This is quite dangerous!
					// But if this is not set we get problems e.g. with HashMap$Node
					.setTypeEvaluatorPersistable(a -> true)
					.createPersistenceManager();
		}
		else
		{
			this.persistenceManager.objectRegistry().truncateAll();
		}
	}
	
	/**
	 * Here lies a lot of knowledge about EclipseStore internals.
	 * <p>
	 * <b>Edit with caution!</b>
	 * </p>
	 * <p>
	 * A storer is created. Then a loader. By then calling {@link PersistenceStorer#store(Object)} the source-object is
	 * serialized in memory. Then the created objects are put in a Map which holds the EclipseStore-ObjectId and all
	 * the
	 * serialized objects. By calling {@link PersistenceLoader#get()} the serilized objects are deserialized. Then we
	 * iterate over the deserlized objects and pair them with the corresponding source-objects through the
	 * EclipseStore-ObjectId.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public synchronized <T> T copy(final T source, final boolean invertRegistering)
	{
		this.lazyInit();
		final BinaryStorer.Default storer = (BinaryStorer.Default)this.persistenceManager.createStorer();
		// Loader erstellen
		final PersistenceLoader loader = this.persistenceManager.createLoader();
		
		storer.store(source);
		
		final Map<Long, Object> originalObjects = new HashMap<>();
		
		storer.iterateMergeableEntries(originalObjects::put);
		
		storer.commit();
		
		final T returnValue = (T)loader.get();
		
		final Summarizer summarizer = new Summarizer();
		
		loader.iterateEntries(
			(id, copiedObject) ->
			{
				summarizer.incrementCopiedObjectsCount();
				if(DataTypeUtil.isPrimitiveType(copiedObject.getClass()))
				{
					return;
				}
				final Object originalObject = originalObjects.get(id);
				if(originalObject != null)
				{
					summarizer.incrementRegisteredObjectsCountt();
					if(invertRegistering)
					{
						this.registry.invertRegister(copiedObject, originalObject);
					}
					else
					{
						this.registry.register(copiedObject, originalObject);
					}
				}
			}
		);
		
		if(LOG.isTraceEnabled())
		{
			LOG.trace(
				"Copied {} and registered {} objects.",
				summarizer.copiedObjectsCount,
				summarizer.registeredObjectsCount);
		}
		
		return returnValue;
	}
	
	private static class Summarizer
	{
		private long copiedObjectsCount;
		private long registeredObjectsCount;
		
		public Summarizer()
		{
			this.copiedObjectsCount = 0;
			this.registeredObjectsCount = 0;
		}
		
		public void incrementCopiedObjectsCount()
		{
			this.copiedObjectsCount += 1;
		}
		
		public void incrementRegisteredObjectsCountt()
		{
			this.registeredObjectsCount += 1;
		}
	}
}
