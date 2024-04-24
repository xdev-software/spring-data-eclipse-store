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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.serializer.persistence.binary.types.Binary;
import org.eclipse.serializer.persistence.binary.types.BinaryStorer;
import org.eclipse.serializer.persistence.types.PersistenceLoader;
import org.eclipse.serializer.persistence.types.PersistenceManager;
import org.eclipse.serializer.persistence.types.PersistenceStorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.xdev.spring.data.eclipse.store.exceptions.DataTypeNotSupportedException;
import software.xdev.spring.data.eclipse.store.repository.SupportedChecker;
import software.xdev.spring.data.eclipse.store.repository.support.copier.DataTypeUtil;


/**
 * This class utilizes EclipseStore-Serialization and copies objects by serializing and deserializing objects in
 * memory.
 */
public class EclipseSerializerRegisteringCopier implements AutoCloseable
{
	private static final Logger LOG = LoggerFactory.getLogger(EclipseSerializerRegisteringCopier.class);
	private PersistenceManager<Binary> persistenceManager;
	private final SupportedChecker supportedChecker;
	private final RegisteringWorkingCopyAndOriginal register;
	
	public EclipseSerializerRegisteringCopier(
		final SupportedChecker supportedChecker,
		final RegisteringWorkingCopyAndOriginal register,
		final PersistenceManager<Binary> persistenceManager)
	{
		this.supportedChecker = supportedChecker;
		this.register = register;
		this.persistenceManager = persistenceManager;
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
	
	/**
	 * Here lies a lot of knowledge about EclipseStore internals.
	 * <p>
	 * <b>Edit with caution!</b>
	 * </p>
	 * <p>
	 * A storer is created. Then a loader. By then calling {@link PersistenceStorer#store(Object)} the source-object is
	 * serialized in memory. Then the created objects are put in a Map which holds the EclipseStore-ObjectId and all
	 * the
	 * serialized objects. By calling {@link PersistenceLoader#get()} the serialized objects are deserialized. Then we
	 * iterate over the deserialized objects and pair them with the corresponding source-objects through the
	 * EclipseStore-ObjectId.
	 * </p>
	 */
	public synchronized <T> T copy(final T source)
	{
		this.persistenceManager.objectRegistry().truncateAll();
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
				if(copiedObject != null && !this.supportedChecker.isSupported(copiedObject.getClass()))
				{
					throw new DataTypeNotSupportedException(copiedObject.getClass());
				}
				summarizer.incrementCopiedObjectsCount();
				if(DataTypeUtil.isPrimitiveType(copiedObject.getClass()))
				{
					return;
				}
				final Object originalObject = originalObjects.get(id);
				if(originalObject != null)
				{
					summarizer.incrementRegisteredObjectsCount();
					this.register.register(copiedObject, originalObject);
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
		
		public void incrementRegisteredObjectsCount()
		{
			this.registeredObjectsCount += 1;
		}
	}
}
