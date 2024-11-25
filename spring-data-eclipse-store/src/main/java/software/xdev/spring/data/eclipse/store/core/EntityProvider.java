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
package software.xdev.spring.data.eclipse.store.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import software.xdev.spring.data.eclipse.store.repository.root.EntityData;


@SuppressWarnings("java:S119")
public class EntityProvider<T, ID>
{
	private final List<EntityData<? extends T, ID>> entityDataList = new ArrayList<>();
	
	public void addEntityData(final EntityData<? extends T, ID> entityData)
	{
		this.entityDataList.add(entityData);
	}
	
	public Stream<? extends T> stream()
	{
		return this.entityDataList.stream().map(EntityData::getEntities).flatMap(Set::stream);
	}
	
	public Collection<T> toCollection()
	{
		return this.stream().collect(Collectors.toUnmodifiableList());
	}
	
	public boolean isEmpty()
	{
		return this.stream().findAny().isEmpty();
	}
	
	public long size()
	{
		return this.stream().count();
	}
	
	public Optional<T> findAnyEntityWithId(final ID id)
	{
		return (Optional<T>)this.entityDataList
			.stream()
			.map(entityData -> entityData.getEntitiesById().get(id))
			.filter(e -> e != null)
			.findAny();
	}
}
