package software.xdev.spring.data.eclipse.store.repository.lazy;

import java.util.Objects;

import org.eclipse.serializer.reference.Lazy;
import org.eclipse.serializer.reference.ObjectSwizzling;
import org.eclipse.serializer.reference.Swizzling;


public interface SpringDataEclipseStoreLazy<T> extends Lazy<T>
{
	static <T> SpringDataEclipseStoreLazy.Default<T> build(final T objectToWrapInLazy)
	{
		return new Default<>(objectToWrapInLazy);
	}
	
	static SpringDataEclipseStoreLazy.Default<?> buildOnlyForStorage(final long objectId)
	{
		return new Default<>(objectId);
	}
	
	long objectId();
	
	class Default<T> implements SpringDataEclipseStoreLazy<T>
	{
		private Lazy<T> wrappedLazy;
		private long objectId = Swizzling.notFoundId();
		private transient ObjectSwizzling loader;
		private transient boolean isStored = false;
		
		private Default(final T wrappedObject)
		{
			this.wrappedLazy = Lazy.Reference(wrappedObject);
		}
		
		private Default(final long objectId, final ObjectSwizzling loader)
		{
			this.objectId = objectId;
			this.loader = loader;
		}
		
		private Default(final long objectId)
		{
			this.objectId = objectId;
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
	}
}
