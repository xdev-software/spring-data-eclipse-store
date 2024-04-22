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

import java.lang.reflect.Field;
import java.util.Objects;

import org.eclipse.serializer.reference.Lazy;
import org.eclipse.serializer.reference.ObjectSwizzling;
import org.eclipse.serializer.reference.Swizzling;

import software.xdev.spring.data.eclipse.store.exceptions.LazyNotUnlinkableException;
import software.xdev.spring.data.eclipse.store.repository.access.modifier.FieldAccessModifier;
import software.xdev.spring.data.eclipse.store.repository.support.copier.working.WorkingCopier;


/**
 * This is the Lazy-Wrapper a user of the Spring-Data-Eclipse-Store-Library should use. Please <b>do not use the
 * {@link Lazy}-Wrapper!</b> Because SDES is making working copies of the stored data, the {@link Lazy} does not work as
 * expected. Instead, use this Wrapper. It brings the same functionality as the native {@link Lazy}-Wrapper but works
 * with working copies.
 *
 * @param <T> the type of the lazily referenced element
 */
public interface SpringDataEclipseStoreLazy<T> extends Lazy<T>
{
	static <T> SpringDataEclipseStoreLazy.Default<T> build(final T objectToWrapInLazy)
	{
		return new Default<>(objectToWrapInLazy);
	}
	
	SpringDataEclipseStoreLazy<T> copyWithReference();
	
	void unlink();
	
	long objectId();
	
	boolean isOriginalObject();
	
	interface Internals
	{
		static <T> SpringDataEclipseStoreLazy.Default<T> buildWithLazy(final Lazy<T> lazySubject)
		{
			return new Default<>(lazySubject);
		}
	}
	
	/**
	 * This class is very complex and its various member variables all have their reason to exist. This code is very
	 * difficult to read due to its the functionality explained in the {@link SpringDataEclipseStoreLazyBinaryHandler}.
	 *
	 * @param <T> the type of the lazily referenced element
	 */
	@SuppressWarnings({"java:S2065", "checkstyle:FinalClass"})
	class Default<T> implements SpringDataEclipseStoreLazy<T>
	{
		private T objectToBeWrapped;
		private Lazy<T> wrappedLazy;
		private long objectId = Swizzling.notFoundId();
		private transient ObjectSwizzling loader;
		private transient WorkingCopier<T> copier;
		private transient boolean isStored;
		
		private Default(final Lazy<T> lazySubject)
		{
			this.setWrappedLazy(lazySubject);
		}
		
		private Default(final T objectToBeWrapped)
		{
			this.objectToBeWrapped = objectToBeWrapped;
		}
		
		private Default(final long objectId, final ObjectSwizzling loader, final WorkingCopier<T> copier)
		{
			this.objectId = objectId;
			this.loader = loader;
			this.copier = copier;
			// This object is already stored in the real storage. So it can get cleared.
			this.setStored();
		}
		
		private Lazy<T> ensureLazy()
		{
			if(this.wrappedLazy == null || !this.wrappedLazy.isLoaded())
			{
				this.wrappedLazy = this.createNewDefaultLazyWithClearableReference();
			}
			return this.wrappedLazy;
		}
		
		@SuppressWarnings("unchecked")
		private Lazy<T> createNewDefaultLazyWithClearableReference()
		{
			Objects.requireNonNull(this.loader);
			Objects.requireNonNull(this.copier);
			
			final T originalInstance = (T)this.loader.getObject(this.objectId);
			final T copiedInstance = this.copier.onlyCreateCopy(originalInstance, false);
			
			return Lazy.New(
				copiedInstance,
				Swizzling.nullId(),
				this.loader
			);
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
			if(this.wrappedLazy != null && this.wrappedLazy instanceof final Lazy.Default<T> wrappedTypedLazy)
			{
				return wrappedTypedLazy.objectId();
			}
			return this.objectId;
		}
		
		void setWrappedLazy(final Lazy<T> wrappedLazy)
		{
			this.wrappedLazy = wrappedLazy;
			// This object is already stored in the real storage. So it can get cleared.
			this.setStored();
		}
		
		public T getObjectToBeWrapped()
		{
			return this.objectToBeWrapped;
		}
		
		@Override
		public SpringDataEclipseStoreLazy<T> copyWithReference()
		{
			final SpringDataEclipseStoreLazy.Default<T> newLazy = new SpringDataEclipseStoreLazy.Default<>(
				this.objectId(),
				this.loader,
				this.copier
			);
			newLazy.wrappedLazy = this.wrappedLazy;
			return newLazy;
		}
		
		@Override
		public void unlink()
		{
			try
			{
				if(this.wrappedLazy != null)
				{
					final Lazy.Default<T> wrappedDefaultLazy = (Lazy.Default<T>)this.wrappedLazy;
					wrappedDefaultLazy.$unlink();
					final Field objectIdField = Lazy.Default.class.getDeclaredField("objectId");
					try(final FieldAccessModifier<Lazy.Default<T>> fam = FieldAccessModifier.prepareForField(
						objectIdField,
						wrappedDefaultLazy))
					{
						// The lazy object should be seen as "stored" by the LazyManager.
						// Therefore, we must set the objectId to Swizzling.nullId().
						fam.writeValueOfField(wrappedDefaultLazy, Swizzling.nullId(), true);
					}
				}
			}
			catch(final Exception e)
			{
				throw new LazyNotUnlinkableException("Could not unlink lazy " + this.wrappedLazy, e);
			}
		}
		
		@Override
		public boolean isOriginalObject()
		{
			return this.objectToBeWrapped != null;
		}
	}
}
