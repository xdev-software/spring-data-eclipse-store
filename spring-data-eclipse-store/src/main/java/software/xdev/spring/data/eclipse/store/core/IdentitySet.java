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
package software.xdev.spring.data.eclipse.store.core;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.serializer.meta.NotImplementedYetError;
import org.springframework.lang.NonNull;

import jakarta.annotation.Nonnull;


/**
 * A hash map implementation depending on object identity (==) rather than equality (.equals) to identify elements.
 **/
public class IdentitySet<E> implements Set<E>
{
	private final IdentityHashMap<E, Boolean> internalMap;
	
	public IdentitySet()
	{
		this.internalMap = new IdentityHashMap<>();
	}
	
	@Override
	public int size()
	{
		return this.internalMap.size();
	}
	
	@Override
	public boolean isEmpty()
	{
		return this.internalMap.isEmpty();
	}
	
	@SuppressWarnings("SuspiciousMethodCalls")
	@Override
	public boolean contains(final Object o)
	{
		return this.internalMap.containsKey(o);
	}
	
	@Override
	@Nonnull
	public Iterator<E> iterator()
	{
		return this.internalMap.keySet().iterator();
	}
	
	@Override
	@Nonnull
	public Object[] toArray()
	{
		return this.internalMap.keySet().toArray();
	}
	
	@Override
	@NonNull
	public <T> T[] toArray(@Nonnull final T[] a)
	{
		return this.internalMap.keySet().toArray(a);
	}
	
	@Override
	public boolean add(final E e)
	{
		return this.internalMap.put(e, null) == null;
	}
	
	@Override
	public boolean remove(final Object o)
	{
		return this.internalMap.remove(o) != null;
	}
	
	@Override
	public boolean containsAll(@Nonnull final Collection<?> c)
	{
		return this.internalMap.keySet().containsAll(c);
	}
	
	@Override
	public boolean addAll(@Nonnull final Collection<? extends E> c)
	{
		throw new NotImplementedYetError();
	}
	
	@Override
	public boolean retainAll(@Nonnull final Collection<?> c)
	{
		throw new NotImplementedYetError();
	}
	
	@Override
	public boolean removeAll(final Collection<?> c)
	{
		return c.stream().map(this.internalMap::remove).anyMatch(Objects::nonNull);
	}
	
	@Override
	public void clear()
	{
		this.internalMap.clear();
	}
	
	@SuppressWarnings("com.haulmont.jpb.EqualsDoesntCheckParameterClass")
	@Override
	public boolean equals(final Object o)
	{
		return this.internalMap.equals(o);
	}
	
	@Override
	public int hashCode()
	{
		return this.internalMap.hashCode();
	}
	
	public Map<E, Boolean> getInternalMap()
	{
		return this.internalMap;
	}
}
