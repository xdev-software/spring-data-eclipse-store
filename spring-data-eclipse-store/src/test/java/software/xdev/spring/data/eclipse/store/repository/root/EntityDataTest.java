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
package software.xdev.spring.data.eclipse.store.repository.root;

import java.util.function.Function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import software.xdev.spring.data.eclipse.store.helper.TestData;


class EntityDataTest
{
	record Person(String id, String name)
	{
	}
	
	
	private final Function<Person, String> idGetter = person -> person.id;
	private final Function<Person, String> idNullGetter = person -> null;
	
	@Test
	void idGetterIsNotSet()
	{
		final EntityData<Person, String> testData = new EntityData<>();
		testData.ensureEntityAndReturnObjectsToStore(new Person("1", TestData.FIRST_NAME));
		Assertions.assertTrue(testData.getEntitiesById().isEmpty());
		Assertions.assertEquals(1, testData.getEntities().size());
	}
	
	@Test
	void idGetterSetAndNothingAdded()
	{
		final EntityData<Person, String> testData = new EntityData<>();
		testData.setIdGetter(this.idGetter);
		Assertions.assertTrue(testData.getEntitiesById().isEmpty());
		Assertions.assertTrue(testData.getEntities().isEmpty());
	}
	
	@Test
	void idGetterNotSetSetAndNothingAdded()
	{
		final EntityData<Person, String> testData = new EntityData<>();
		Assertions.assertTrue(testData.getEntitiesById().isEmpty());
		Assertions.assertTrue(testData.getEntities().isEmpty());
	}
	
	@Test
	void idGetterSet()
	{
		final EntityData<Person, String> testData = new EntityData<>();
		testData.setIdGetter(this.idGetter);
		testData.ensureEntityAndReturnObjectsToStore(new Person("1", TestData.FIRST_NAME));
		Assertions.assertEquals(1, testData.getEntitiesById().size());
		Assertions.assertEquals(1, testData.getEntities().size());
	}
	
	@Test
	void idGetterSetToNull()
	{
		final EntityData<Person, String> testData = new EntityData<>();
		testData.setIdGetter(this.idNullGetter);
		testData.ensureEntityAndReturnObjectsToStore(new Person("1", TestData.FIRST_NAME));
		Assertions.assertEquals(1, testData.getEntitiesById().size());
		Assertions.assertEquals(1, testData.getEntities().size());
	}
	
	@Test
	void idGetterSetToNullWith2Entities()
	{
		final EntityData<Person, String> testData = new EntityData<>();
		testData.setIdGetter(this.idNullGetter);
		testData.ensureEntityAndReturnObjectsToStore(new Person("1", TestData.FIRST_NAME));
		testData.ensureEntityAndReturnObjectsToStore(new Person("2", TestData.FIRST_NAME_ALTERNATIVE));
		Assertions.assertEquals(1, testData.getEntitiesById().size());
		Assertions.assertEquals(2, testData.getEntities().size());
	}
	
	@Test
	void idGetterSetWith2Entities()
	{
		final EntityData<Person, String> testData = new EntityData<>();
		testData.setIdGetter(this.idGetter);
		testData.ensureEntityAndReturnObjectsToStore(new Person("1", TestData.FIRST_NAME));
		testData.ensureEntityAndReturnObjectsToStore(new Person("2", TestData.FIRST_NAME_ALTERNATIVE));
		Assertions.assertEquals(2, testData.getEntitiesById().size());
		Assertions.assertEquals(2, testData.getEntities().size());
	}
	
	@Test
	void idGetterFirstNullThenReturningIdSameObject()
	{
		final EntityData<Person, String> testData = new EntityData<>();
		testData.setIdGetter(null);
		testData.ensureEntityAndReturnObjectsToStore(new Person("1", TestData.FIRST_NAME));
		testData.ensureEntityAndReturnObjectsToStore(new Person("2", TestData.FIRST_NAME_ALTERNATIVE));
		Assertions.assertTrue(testData.getEntitiesById().isEmpty());
		Assertions.assertEquals(2, testData.getEntities().size());
		
		testData.setIdGetter(this.idGetter);
		final Person foundPerson = testData.getEntities().stream().findFirst().get();
		testData.ensureEntityAndReturnObjectsToStore(foundPerson);
		Assertions.assertEquals(2, testData.getEntitiesById().size());
		Assertions.assertEquals(2, testData.getEntities().size());
	}
	
	@Test
	void idGetterFirstReturnNullThenReturningIdSameObject()
	{
		final EntityData<Person, String> testData = new EntityData<>();
		testData.setIdGetter(this.idNullGetter);
		testData.ensureEntityAndReturnObjectsToStore(new Person("1", TestData.FIRST_NAME));
		testData.ensureEntityAndReturnObjectsToStore(new Person("2", TestData.FIRST_NAME_ALTERNATIVE));
		Assertions.assertEquals(1, testData.getEntitiesById().size());
		Assertions.assertEquals(2, testData.getEntities().size());
		
		testData.setIdGetter(this.idGetter);
		final Person foundPerson = testData.getEntities().stream().findFirst().get();
		testData.ensureEntityAndReturnObjectsToStore(foundPerson);
		Assertions.assertEquals(2, testData.getEntitiesById().size());
		Assertions.assertEquals(2, testData.getEntities().size());
	}
	
	@Test
	void idGetterFirstNullThenReturningIdDifferentObject()
	{
		final EntityData<Person, String> testData = new EntityData<>();
		testData.setIdGetter(null);
		testData.ensureEntityAndReturnObjectsToStore(new Person("1", TestData.FIRST_NAME));
		testData.ensureEntityAndReturnObjectsToStore(new Person("2", TestData.FIRST_NAME_ALTERNATIVE));
		Assertions.assertTrue(testData.getEntitiesById().isEmpty());
		Assertions.assertEquals(2, testData.getEntities().size());
		
		testData.setIdGetter(this.idGetter);
		testData.ensureEntityAndReturnObjectsToStore(new Person("1", TestData.FIRST_NAME));
		Assertions.assertEquals(2, testData.getEntitiesById().size());
		Assertions.assertEquals(3, testData.getEntities().size());
	}
	
	@Test
	void idGetterFirstReturnNullThenReturningIdDifferentObject()
	{
		final EntityData<Person, String> testData = new EntityData<>();
		testData.setIdGetter(this.idNullGetter);
		testData.ensureEntityAndReturnObjectsToStore(new Person("1", TestData.FIRST_NAME));
		testData.ensureEntityAndReturnObjectsToStore(new Person("2", TestData.FIRST_NAME_ALTERNATIVE));
		Assertions.assertEquals(1, testData.getEntitiesById().size());
		Assertions.assertEquals(2, testData.getEntities().size());
		
		testData.setIdGetter(this.idGetter);
		testData.ensureEntityAndReturnObjectsToStore(new Person("1", TestData.FIRST_NAME));
		Assertions.assertEquals(2, testData.getEntitiesById().size());
		Assertions.assertEquals(3, testData.getEntities().size());
	}
}
