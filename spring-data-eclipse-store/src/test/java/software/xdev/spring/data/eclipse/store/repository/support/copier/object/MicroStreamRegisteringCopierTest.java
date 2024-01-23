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
package software.xdev.spring.data.eclipse.store.repository.support.copier.object;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import software.xdev.spring.data.eclipse.store.repository.WorkingCopyRegistry;


class EclipseStoreRegisteringCopierTest
{
	static private class DummyData
	{
		private final String data;
		private final int number;
		
		public DummyData(final String data, final int number)
		{
			this.data = data;
			this.number = number;
		}
		
		@Override
		public boolean equals(final Object o)
		{
			if(this == o)
			{
				return true;
			}
			if(o == null || this.getClass() != o.getClass())
			{
				return false;
			}
			final DummyData dummyData = (DummyData)o;
			return this.number == dummyData.number && Objects.equals(this.data, dummyData.data);
		}
		
		@Override
		public int hashCode()
		{
			return Objects.hash(this.data, this.number);
		}
	}
	
	@Test
	void testCopyManyCopiesSingle()
	{
		try(final EclipseSerializerRegisteringCopier copier =
			new EclipseSerializerRegisteringCopier(new WorkingCopyRegistry()))
		{
			final List<DummyData> originalObjects = IntStream.range(0, 1_000).mapToObj(
				i -> new DummyData("Data" + i, i)
			).toList();
			
			final List<DummyData> copiedObjects = originalObjects.stream().map(copier::copy).toList();
			
			Assertions.assertIterableEquals(originalObjects, copiedObjects);
		}
	}
	
	@Test
	void testCopyAgainSameObject()
	{
		try(final EclipseSerializerRegisteringCopier copier =
			new EclipseSerializerRegisteringCopier(new WorkingCopyRegistry()))
		{
			final DummyData originalObject = new DummyData("Test", 1);
			
			final DummyData firstCopy = copier.copy(originalObject);
			final DummyData secondCopy = copier.copy(originalObject);
			
			Assertions.assertNotSame(firstCopy, secondCopy);
		}
	}
	
	@Test
	void testCopyAgainTheCopy()
	{
		try(final EclipseSerializerRegisteringCopier copier =
			new EclipseSerializerRegisteringCopier(new WorkingCopyRegistry()))
		{
			final DummyData originalObject = new DummyData("Test", 1);
			
			final DummyData firstCopy = copier.copy(originalObject);
			final DummyData secondCopy = copier.copy(firstCopy);
			
			Assertions.assertNotSame(originalObject, firstCopy);
			Assertions.assertNotSame(firstCopy, secondCopy);
			Assertions.assertNotSame(secondCopy, originalObject);
		}
	}
	
	@Test
	void testCopyManyCopiesBulk()
	{
		try(final EclipseSerializerRegisteringCopier copier =
			new EclipseSerializerRegisteringCopier(new WorkingCopyRegistry()))
		{
			final List<DummyData> originalObjects = IntStream.range(0, 100_000).mapToObj(
				i -> new DummyData("Data" + i, i)
			).toList();
			
			final List<DummyData> copiedObjects = copier.copy(originalObjects);
			
			Assertions.assertIterableEquals(originalObjects, copiedObjects);
		}
	}
	
	@Test
	void testCopyEmpty()
	{
		try(final EclipseSerializerRegisteringCopier copier =
			new EclipseSerializerRegisteringCopier(new WorkingCopyRegistry()))
		{
			Assertions.assertThrows(NullPointerException.class, () -> copier.copy(null));
		}
	}
}
