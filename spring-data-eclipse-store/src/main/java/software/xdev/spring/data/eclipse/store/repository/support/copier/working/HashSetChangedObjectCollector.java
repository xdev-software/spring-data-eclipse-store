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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.serializer.persistence.types.Unpersistable;

import software.xdev.spring.data.eclipse.store.repository.PersistableChecker;


public class HashSetChangedObjectCollector<T> implements ChangedObjectCollector, Unpersistable
{
	private final List<Object> nonEntityObjects = new ArrayList<>();
	private final List<T> entityObjects = new ArrayList<>();
	private final Class<T> entityClass;
	private final PersistableChecker persistableChecker;
	
	public HashSetChangedObjectCollector(final Class<T> entityClass, final PersistableChecker persistableChecker)
	{
		this.entityClass = entityClass;
		this.persistableChecker = persistableChecker;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void collectChangedObject(final Object changedObject)
	{
		if(!this.persistableChecker.isPersistable(changedObject.getClass()))
		{
			return;
		}
		if(this.entityClass.isInstance(changedObject))
		{
			this.entityObjects.add((T)changedObject);
		}
		else
		{
			this.nonEntityObjects.add(changedObject);
		}
	}
	
	public WorkingCopierResult<T> toResult()
	{
		return new WorkingCopierResult<>(
			this.entityObjects.stream().collect(Collectors.toSet()),
			this.nonEntityObjects.stream().collect(Collectors.toSet())
		);
	}
}
