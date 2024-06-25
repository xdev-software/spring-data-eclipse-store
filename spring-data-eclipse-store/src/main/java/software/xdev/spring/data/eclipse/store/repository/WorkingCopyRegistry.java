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
package software.xdev.spring.data.eclipse.store.repository;

import java.util.IdentityHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.xdev.spring.data.eclipse.store.exceptions.DifferentClassesException;


public class WorkingCopyRegistry
{
	private static final Logger LOG = LoggerFactory.getLogger(WorkingCopyRegistry.class);
	/**
	 * Map with Working Copies (key) with the corresponding original object (value).
	 */
	private Map<Object, Object> currentWorkingCopies = new IdentityHashMap<>();
	
	/**
	 * Ties an original object together with a working copy.
	 *
	 * @return {@code true} if original object <b>not</b> already registered.
	 */
	@SuppressWarnings("UnusedReturnValue")
	public <T> boolean invertRegister(final T workingCopy, final T objectToStore)
	{
		return this.registerInternal(objectToStore, workingCopy);
	}
	
	/**
	 * Ties a working copy together with an original object.
	 *
	 * @return {@code true} if working copy is <b>not</b> already registered.
	 */
	@SuppressWarnings("UnusedReturnValue")
	public <T> boolean register(final T workingCopyToRegister, final T orginalObject)
	{
		return this.registerInternal(workingCopyToRegister, orginalObject);
	}
	
	private synchronized <T> boolean registerInternal(final T keyObject, final T valueObject)
	{
		if(keyObject.getClass() != valueObject.getClass())
		{
			throw new DifferentClassesException("There is a critical error creating a working copy.");
		}
		if(this.currentWorkingCopies.containsKey(keyObject))
		{
			return false;
		}
		this.currentWorkingCopies.put(keyObject, valueObject);
		if(LOG.isTraceEnabled())
		{
			LOG.trace(
				"Registered a object of class {}. Registered objects: {}",
				valueObject.getClass(),
				this.currentWorkingCopies.size());
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public synchronized <T> T getOriginalObjectFromWorkingCopy(final T working)
	{
		return (T)this.currentWorkingCopies.get(working);
	}
	
	public synchronized void deregister(final Object workingCopyToDeregister)
	{
		this.currentWorkingCopies.remove(workingCopyToDeregister);
		
		if(LOG.isTraceEnabled())
		{
			LOG.trace(
				"Deregistered a object of class {}. Registered objects: {}",
				workingCopyToDeregister.getClass(),
				this.currentWorkingCopies.size());
		}
	}
	
	public synchronized void reset()
	{
		this.currentWorkingCopies = new IdentityHashMap<>();
		
		if(LOG.isTraceEnabled())
		{
			LOG.trace("Cleared WorkingCopyRegistry");
		}
	}
}
