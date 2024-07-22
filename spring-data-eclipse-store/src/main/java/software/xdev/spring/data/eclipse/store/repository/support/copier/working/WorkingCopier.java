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
package software.xdev.spring.data.eclipse.store.repository.support.copier.working;

import java.util.Collection;


/**
 * Creates, registers and deregisters Instances of working copies. Working copies are object instances that are a copy
 * of the objects in the actual datastore.
 *
 * @param <T> class of the object that will get copied
 */
public interface WorkingCopier<T>
{
	/**
	 * Creates a new instance of the given object and registers it in the
	 * {@link software.xdev.spring.data.eclipse.store.repository.WorkingCopyRegistry}.
	 *
	 * @return new instance of the object
	 */
	T copy(T objectToCopy);
	
	/**
	 * Creates new instances of each element of the given collection and registers it in the
	 * {@link software.xdev.spring.data.eclipse.store.repository.WorkingCopyRegistry}.
	 *
	 * @return new collection with new instances
	 */
	<L extends Collection<T>> L copy(L objectCollectionToCopy);
	
	/**
	 * Merges the values of the given object back into the original object from the date store.
	 *
	 * @param workingCopy to merge back into the original object
	 * @return all the changed objects that must get stored
	 */
	WorkingCopierResult<T> mergeBack(T workingCopy);
	
	<E> E onlyCreateCopy(final E objectToCopy, final boolean invertRegistry);
	
	/**
	 * @return the original entity that corresponds to the given working copy object.
	 */
	T getOriginal(T workingCopy);
	
	/**
	 * Unregister the working copy object from the
	 * {@link software.xdev.spring.data.eclipse.store.repository.WorkingCopyRegistry}.
	 */
	void deregister(T workingCopy);
}
