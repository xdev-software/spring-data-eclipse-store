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
package software.xdev.spring.data.eclipse.store.repository.support.copier.working;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.serializer.reference.Lazy;
import org.eclipse.serializer.reference.ObjectSwizzling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.xdev.spring.data.eclipse.store.exceptions.MergeFailedException;
import software.xdev.spring.data.eclipse.store.repository.IdSetterProvider;
import software.xdev.spring.data.eclipse.store.repository.PersistableChecker;
import software.xdev.spring.data.eclipse.store.repository.SupportedChecker;
import software.xdev.spring.data.eclipse.store.repository.WorkingCopyRegistry;
import software.xdev.spring.data.eclipse.store.repository.access.AccessHelper;
import software.xdev.spring.data.eclipse.store.repository.access.modifier.FieldAccessModifier;
import software.xdev.spring.data.eclipse.store.repository.lazy.SpringDataEclipseStoreLazy;
import software.xdev.spring.data.eclipse.store.repository.support.copier.DataTypeUtil;
import software.xdev.spring.data.eclipse.store.repository.support.copier.copier.RegisteringObjectCopier;
import software.xdev.spring.data.eclipse.store.repository.support.copier.copier.RegisteringStorageToWorkingCopyCopier;
import software.xdev.spring.data.eclipse.store.repository.support.copier.copier.RegisteringWorkingCopyToStorageCopier;


/**
 * Creates copies and puts them back. Recognizes already persisted Objects and checks them for changes as well.
 */
public class RecursiveWorkingCopier<T> implements WorkingCopier<T>
{
	private static final Logger LOG = LoggerFactory.getLogger(RecursiveWorkingCopier.class);
	private final RegisteringObjectCopier workingCopyToStorageCopier;
	private final RegisteringObjectCopier storageToWorkingCopyCopier;
	private final WorkingCopyRegistry registry;
	private final IdSetterProvider idSetterProvider;
	private final Class<T> domainClass;
	private final PersistableChecker persistableChecker;
	
	public RecursiveWorkingCopier(
		final Class<T> domainClass,
		final WorkingCopyRegistry registry,
		final IdSetterProvider idSetterProvider,
		final PersistableChecker persistableChecker,
		final SupportedChecker supportedChecker,
		final ObjectSwizzling objectSwizzling)
	{
		this.domainClass = domainClass;
		this.registry = registry;
		this.workingCopyToStorageCopier =
			new RegisteringWorkingCopyToStorageCopier(registry, supportedChecker, objectSwizzling);
		this.storageToWorkingCopyCopier =
			new RegisteringStorageToWorkingCopyCopier(registry, supportedChecker, objectSwizzling);
		this.idSetterProvider = idSetterProvider;
		this.persistableChecker = persistableChecker;
	}
	
	@Override
	public T copy(final T objectToCopy)
	{
		final T createdCopy = this.genericCopy(objectToCopy, false);
		if(LOG.isTraceEnabled())
		{
			LOG.trace("Copied object of class {}", objectToCopy.getClass().getSimpleName());
		}
		return createdCopy;
	}
	
	@Override
	public <L extends Collection<T>> L copy(final L objectCollectionToCopy)
	{
		final L createdCopy = this.genericCopy(objectCollectionToCopy, false);
		if(LOG.isTraceEnabled())
		{
			LOG.trace("Copied collection with class {}", objectCollectionToCopy.getClass().getSimpleName());
		}
		return createdCopy;
	}
	
	private <E> E genericCopy(final E objectToCopy, final boolean invertRegistry)
	{
		if(this.registry.getOriginalObjectFromWorkingCopy(objectToCopy) != null)
		{
			// Object is already a working copy.
			// Copy isn't created again to stop recursive stack overflow.
			return objectToCopy;
		}
		return this.onlyCreateCopy(objectToCopy, invertRegistry);
	}
	
	@Override
	public WorkingCopierResult<T> mergeBack(final T workingCopy)
	{
		final HashSetChangedObjectCollector<T> changedObjectCollector =
			new HashSetChangedObjectCollector<>(this.domainClass, this.persistableChecker);
		this.getOrCreateObjectForDatastore(
			workingCopy,
			true,
			new HashSetMergedTargetsCollector(),
			changedObjectCollector);
		if(LOG.isTraceEnabled())
		{
			LOG.trace("Merging back the working copy object of class {}", workingCopy.getClass().getSimpleName());
		}
		return changedObjectCollector.toResult();
	}
	
	@SuppressWarnings("unchecked")
	public <E> E getOrCreateObjectForDatastore(
		final E workingCopy,
		final boolean mergeValues,
		final MergedTargetsCollector alreadyMergedTargets,
		final ChangedObjectCollector changedCollector)
	{
		if(workingCopy == null)
		{
			return null;
		}
		this.idSetterProvider.ensureIdSetter((Class<E>)workingCopy.getClass()).ensureId(workingCopy);
		final E originalObject = this.registry.getOriginalObjectFromWorkingCopy(workingCopy);
		if(originalObject != null)
		{
			if(mergeValues)
			{
				this.mergeValues(workingCopy, originalObject, alreadyMergedTargets, changedCollector);
			}
			changedCollector.collectChangedObject(originalObject);
			return originalObject;
		}
		
		// The object to merge back is not a working copy, but a originalObject.
		// Therefore, we create a copy to persist this in the storage.
		final E objectForDatastore = this.genericCopy(workingCopy, true);
		// "Why merge values of an identical object?" you might ask.
		// Well, because some sub-objects might already be in the datastore.
		this.mergeValues(workingCopy, objectForDatastore, alreadyMergedTargets, changedCollector);
		changedCollector.collectChangedObject(objectForDatastore);
		return objectForDatastore;
	}
	
	@Override
	public T getOriginal(final T workingCopy)
	{
		return this.registry.getOriginalObjectFromWorkingCopy(workingCopy);
	}
	
	private <E> void mergeValues(
		final E sourceObject,
		final E targetObject,
		final MergedTargetsCollector alreadyMergedTargets,
		final ChangedObjectCollector changedCollector
	)
	{
		if(sourceObject == targetObject || alreadyMergedTargets.isAlreadyMerged(targetObject) || targetObject == null)
		{
			// alreadyMergedTargets prevent endless loops
			return;
		}
		try
		{
			alreadyMergedTargets.collectMergedTarget(targetObject);
			
			if(sourceObject instanceof String)
			{
				// no merge needed and no merge possible
				return;
			}
			AccessHelper.getInheritedPrivateFieldsByName(sourceObject.getClass()).values().forEach(
				field ->
					this.mergeValueOfField(
						sourceObject,
						targetObject,
						field,
						alreadyMergedTargets,
						changedCollector)
			);
		}
		catch(final Exception e)
		{
			throw new MergeFailedException(sourceObject, targetObject, e);
		}
	}
	
	private <E> void mergeValueOfField(
		final E sourceObject,
		final E targetObject,
		final Field field,
		final MergedTargetsCollector alreadyMergedTargets,
		final ChangedObjectCollector changedCollector)
	{
		try
		{
			final int fieldModifiers = field.getModifiers();
			if(Modifier.isStatic(fieldModifiers))
			{
				return;
			}
			
			try(final FieldAccessModifier<E> fam = FieldAccessModifier.prepareForField(
				field,
				sourceObject))
			{
				final Object valueOfSourceObject = fam.getValueOfField(sourceObject);
				final Object valueOfTargetObject = fam.getValueOfField(targetObject);
				// If the same, then there is nothing to do
				if(valueOfTargetObject != valueOfSourceObject)
				{
					// If the class is part of the java package, some fields are final and not changeable.
					// These special cases are properly handled through the EclipseStore Serialization.
					// But to prevent our system to throw an error when setting a final field in the java package,
					// we use this parameter.
					final boolean targetObjectIsPartOfJavaPackage =
						targetObject.getClass().getPackageName().startsWith("java.");
					// Something in the containingObject has changed
					changedCollector.collectChangedObject(targetObject);
					if(DataTypeUtil.isPrimitiveType(field.getType()))
					{
						if(!Objects.equals(valueOfTargetObject, valueOfSourceObject))
						{
							fam.writeValueOfField(targetObject, valueOfSourceObject, !targetObjectIsPartOfJavaPackage);
						}
					}
					else if(DataTypeUtil.isPrimitiveArray(valueOfSourceObject))
					{
						// Copy complete Array
						fam.writeValueOfField(targetObject, valueOfSourceObject, !targetObjectIsPartOfJavaPackage);
					}
					else if(DataTypeUtil.isObjectArray(valueOfSourceObject))
					{
						// Create new Array with original objects with merged data
						final Object[] newArray = this.createGenericObjectArray(
							valueOfSourceObject.getClass().getComponentType(),
							(Object[])valueOfSourceObject,
							alreadyMergedTargets,
							changedCollector
						);
						fam.writeValueOfField(targetObject, newArray, !targetObjectIsPartOfJavaPackage);
					}
					else if(DataTypeUtil.isSpringDataEclipseStoreLazy(valueOfSourceObject))
					{
						final SpringDataEclipseStoreLazy<?> newLazy =
							this.createNewLazy(
								(SpringDataEclipseStoreLazy<?>)valueOfSourceObject,
								(SpringDataEclipseStoreLazy<?>)valueOfTargetObject,
								alreadyMergedTargets,
								changedCollector);
						fam.writeValueOfField(targetObject, newLazy, true);
					}
					else
					{
						// "Simple" object
						// get original value object
						final Object originalValueObjectOfSource =
							this.getOrCreateObjectForDatastore(
								valueOfSourceObject,
								false,
								alreadyMergedTargets,
								changedCollector);
						if(valueOfTargetObject != originalValueObjectOfSource)
						{
							// If the reference is new, it must be set
							fam.writeValueOfField(
								targetObject,
								originalValueObjectOfSource,
								!targetObjectIsPartOfJavaPackage);
						}
						
						if(this.isSpecialCaseWhereOnlyAFullCopyWorks(valueOfSourceObject))
						{
							fam.writeValueOfField(
								targetObject,
								this.onlyCreateCopy(valueOfSourceObject, true),
								!targetObjectIsPartOfJavaPackage);
						}
						else
						{
							// Merge after setting reference to avoid endless loops
							this.mergeValues(
								valueOfSourceObject,
								originalValueObjectOfSource,
								alreadyMergedTargets,
								changedCollector);
						}
					}
				}
			}
		}
		catch(final Exception e)
		{
			throw new MergeFailedException(sourceObject, targetObject, e);
		}
	}
	
	private <E> SpringDataEclipseStoreLazy<E> createNewLazy(
		final SpringDataEclipseStoreLazy<E> oldLazy,
		final SpringDataEclipseStoreLazy<?> newLazy,
		final MergedTargetsCollector alreadyMergedTargets,
		final ChangedObjectCollector changedCollector
	)
	{
		if(oldLazy.isLoaded())
		{
			if(oldLazy.isOriginalObject())
			{
				// This object is new and in this case it is merged into the storage.
				if(!newLazy.isStored())
				{
					// The EclipseSerializerRegisteringCopier already creates the perfect lazy object.
					// No change necessary.
					return (SpringDataEclipseStoreLazy<E>)newLazy;
				}
				else
				{
					final Lazy<E> newLazyForBuilding = Lazy.Reference(oldLazy.get());
					oldLazy.unlink();
					newLazy.unlink();
					// This object is already stored but the new version must get overwritten.
					return SpringDataEclipseStoreLazy.Internals.build(newLazyForBuilding);
				}
			}
			final E copyOfWrappedObject = this.getOrCreateObjectForDatastore(
				oldLazy.get(),
				true,
				alreadyMergedTargets,
				changedCollector);
			oldLazy.unlink();
			newLazy.unlink();
			return SpringDataEclipseStoreLazy.build(copyOfWrappedObject);
		}
		else
		{
			oldLazy.unlink();
			// This lazy should never be used again!
			// It is though of as a temporary copy to merge back into the original-storage-data.
			return (SpringDataEclipseStoreLazy<E>)newLazy.copyOnlyWithReference();
		}
	}
	
	/**
	 * Super special case for HashMap or similar java-classes which can't be merged nicely. Thus, we make a simple copy
	 * of the whole object.
	 */
	private boolean isSpecialCaseWhereOnlyAFullCopyWorks(final Object valueOfSourceObject)
	{
		return
			valueOfSourceObject != null
				&& (
				valueOfSourceObject.getClass().isAssignableFrom(HashMap.class)
					|| valueOfSourceObject.getClass().isAssignableFrom(LinkedHashMap.class)
					|| valueOfSourceObject.getClass().isAssignableFrom(Hashtable.class)
					|| valueOfSourceObject.getClass().isAssignableFrom(TreeSet.class)
					|| valueOfSourceObject.getClass().isAssignableFrom(TreeMap.class)
					|| valueOfSourceObject.getClass().isAssignableFrom(IdentityHashMap.class)
			);
	}
	
	@SuppressWarnings("unchecked")
	private <E> E[] createGenericObjectArray(
		final Class<E> clazz,
		final Object[] valueOfSourceObjectArray,
		final MergedTargetsCollector alreadyMergedTargets,
		final ChangedObjectCollector changedCollector)
	{
		// Create new Array with original objects with merged data
		final E[] newArray = (E[])Array.newInstance(clazz, valueOfSourceObjectArray.length);
		for(int i = 0; i < valueOfSourceObjectArray.length; i++)
		{
			newArray[i] = this.getOrCreateObjectForDatastore(
				(E)valueOfSourceObjectArray[i],
				true,
				alreadyMergedTargets,
				changedCollector);
		}
		return newArray;
	}
	
	private <E> E onlyCreateCopy(final E objectToCopy, final boolean invertRegistry)
	{
		if(invertRegistry)
		{
			return this.workingCopyToStorageCopier.copy(objectToCopy);
		}
		return this.storageToWorkingCopyCopier.copy(objectToCopy);
	}
	
	@Override
	public void deregister(final T workingCopy)
	{
		this.registry.deregister(workingCopy);
	}
}
