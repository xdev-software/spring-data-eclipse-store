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

import java.util.Objects;

import org.eclipse.serializer.reference.Lazy;
import org.eclipse.serializer.reference.ObjectSwizzling;
import org.eclipse.serializer.reference.Swizzling;


/**
 * This is the Lazy-Wrapper a user of the Spring-Data-Eclipse-Store-Library should use. Please <b>do not use the
 * {@link Lazy}-Wrapper!</b> Because SDES is making working copies of the stored data, the {@link Lazy} does not work as
 * expected. Instead, use this Wrapper. It brings the same functionality as the native {@link Lazy}-Wrapper but works
 * with working copies.
 */
public interface SpringDataEclipseStoreLazy<T> extends Lazy<T>
{
	static <T> SpringDataEclipseStoreLazy.Default<T> build(final T objectToWrapInLazy)
	{
		return new Default<>(objectToWrapInLazy);
	}
	
	SpringDataEclipseStoreLazy<T> copy();
	
	long objectId();
	
	/**
	 * This class is very complex and its various membervariables all have their reason to exist. This code is very
	 * difficult to read due to its the functionality explained in the {@link SpringDataEclipseStoreLazyBinaryHandler}.
	 *
	 * @param <T>
	 */
	class Default<T> implements SpringDataEclipseStoreLazy<T>
	{
		private T objectToBeWrapped;
		private Lazy<T> wrappedLazy;
		private long objectId = Swizzling.notFoundId();
		private transient ObjectSwizzling loader;
		private transient boolean isStored = false;
		
		private Default(final T wrappedObject)
		{
			this.objectToBeWrapped = wrappedObject;
		}
		
		private Default(final long objectId, final ObjectSwizzling loader)
		{
			this.objectId = objectId;
			this.loader = loader;
		}
		
		private Lazy<T> ensureLazy()
		{
			if(this.wrappedLazy == null)
			{
				Objects.requireNonNull(this.loader);
				Objects.requireNonNull(this.objectId);
				this.wrappedLazy = Lazy.Reference((T)this.loader.getObject(this.objectId));
			}
			return this.wrappedLazy;
		}
		
		@SuppressWarnings("all")
		public static final Class<SpringDataEclipseStoreLazy.Default<?>> genericType()
		{
			// no idea how to get ".class" to work otherwise in conjunction with generics.
			return (Class)SpringDataEclipseStoreLazy.Default.class;
		}
		
		@Override
		public T get()
		{
			if(this.objectToBeWrapped != null)
			{
				return this.objectToBeWrapped;
			}
			return this.ensureLazy().get();
		}
		
		@Override
		public T peek()
		{
			return this.ensureLazy().peek();
		}
		
		@Override
		public T clear()
		{
			if(!this.isStored())
			{
				throw new IllegalStateException("Cannot clear an unstored lazy reference.");
			}
			// Make sure to save the correct objectId.
			this.objectId = this.objectId();
			this.wrappedLazy = null;
			return null;
		}
		
		@Override
		public boolean isStored()
		{
			return this.isStored;
		}
		
		void setStored()
		{
			this.isStored = true;
		}
		
		@Override
		public boolean isLoaded()
		{
			if(this.objectToBeWrapped != null)
			{
				return true;
			}
			if(this.wrappedLazy == null)
			{
				return false;
			}
			return this.ensureLazy().isLoaded();
		}
		
		@Override
		public long lastTouched()
		{
			return this.ensureLazy().lastTouched();
		}
		
		@Override
		public boolean clear(final ClearingEvaluator clearingEvaluator)
		{
			if(this.wrappedLazy != null)
			{
				return this.wrappedLazy.clear(clearingEvaluator);
			}
			return true;
		}
		
		@Override
		public long objectId()
		{
			if(this.wrappedLazy != null && this.wrappedLazy instanceof Lazy.Default<T>)
			{
				return ((Lazy.Default<T>)this.wrappedLazy).objectId();
			}
			return this.objectId;
		}
		
		void setWrappedLazy(final Lazy<T> wrappedLazy)
		{
			this.wrappedLazy = wrappedLazy;
		}
		
		public T getObjectToBeWrapped()
		{
			return this.objectToBeWrapped;
		}
		
		@Override
		public SpringDataEclipseStoreLazy<T> copy()
		{
			return new SpringDataEclipseStoreLazy.Default(
				this.objectId,
				this.loader
			);
		}
	}
}
