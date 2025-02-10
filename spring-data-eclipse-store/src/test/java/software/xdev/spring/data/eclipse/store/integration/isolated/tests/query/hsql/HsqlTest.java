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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.query.hsql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {HsqlTestConfiguration.class})
class HsqlTest
{
	@Autowired
	private MyEntityRepository repository;
	
	@Test
	void workingCopyAndNotSameObject()
	{
		final MyEntity testEntity = new MyEntity();
		testEntity.setName(TestData.FIRST_NAME);
		this.repository.save(testEntity);
		
		final List<MyEntity> result = this.repository.findAllEntities();
		assertEquals(1, result.size());
		assertEquals(testEntity, result.get(0));
		assertNotSame(testEntity, result.get(0));
	}
	
	private static Stream<Arguments> provideTestDataFindAllEntities()
	{
		return Stream.of(
			Arguments.of(createEntityLists(0), 3),
			Arguments.of(createEntityLists(1), 3),
			Arguments.of(createEntityLists(2), 2),
			Arguments.of(createEntityLists(3), 0)
		);
	}
	@ParameterizedTest
	@MethodSource("provideTestDataFindAllEntities")
	void findAllEntities(final List<MyEntity> entities, final int expectedSize)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findAllEntities();
		assertEquals(expectedSize, result.size());
	}
	
	private static Stream<Arguments> provideTestDataFindByName()
	{
		return Stream.of(
			Arguments.of(createEntityLists(0), 2),
			Arguments.of(createEntityLists(1), 1),
			Arguments.of(createEntityLists(2), 0),
			Arguments.of(createEntityLists(3), 0)
		);
	}
	@ParameterizedTest
	@MethodSource("provideTestDataFindByName")
	void findByName(final List<MyEntity> entities, final int expectedSize)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findByName("John");
		assertEquals(expectedSize, result.size());
		if(expectedSize > 0)
		{
			assertEquals("John", result.get(0).getName());
		}
	}
	
	private static Stream<Arguments> provideTestDataFindByNameAndAgeGreaterThan()
	{
		return Stream.of(
			Arguments.of(createEntityLists(0), 0),
			Arguments.of(createEntityLists(1), 1),
			Arguments.of(createEntityLists(2), 0),
			Arguments.of(createEntityLists(3), 0)
		);
	}
	@ParameterizedTest
	@MethodSource("provideTestDataFindByNameAndAgeGreaterThan")
	void findByNameAndAgeGreaterThan(final List<MyEntity> entities, final int expectedSize)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findByNameAndAgeGreaterThan("John", 25);
		assertEquals(expectedSize, result.size());
		if(expectedSize > result.size())
		{
			assertEquals("John", result.get(0).getName());
		}
	}
	
	private static Stream<Arguments> provideTestDataFindAllOrderByAgeDesc()
	{
		return Stream.of(
			Arguments.of(createEntityLists(0), 40),
			Arguments.of(createEntityLists(1), 40),
			Arguments.of(createEntityLists(2), 28),
			Arguments.of(createEntityLists(3), null)
		);
	}
	@ParameterizedTest
	@MethodSource("provideTestDataFindAllOrderByAgeDesc")
	void findAllOrderByAgeDesc(final List<MyEntity> entities, final Integer expectedFirstAge)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findAllOrderByAgeDesc();
		assertEquals(entities.size(), result.size());
		if(expectedFirstAge != null)
		{
			assertEquals(expectedFirstAge, result.get(0).getAge());
		}
	}
	
	private static Stream<Arguments> provideTestDataFindByNameIn()
	{
		return Stream.of(
			Arguments.of(createEntityLists(0), 2),
			Arguments.of(createEntityLists(1), 2),
			Arguments.of(createEntityLists(2), 1),
			Arguments.of(createEntityLists(3), 0)
		);
	}
	@ParameterizedTest
	@MethodSource("provideTestDataFindByNameIn")
	void findByNameIn(final List<MyEntity> entities, final int expectedSize)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findByNameIn(Arrays.asList("John", "Jane"));
		assertEquals(expectedSize, result.size());
	}
	
	private static Stream<Arguments> provideTestDataFindByNameContaining()
	{
		return Stream.of(
			Arguments.of(createEntityLists(0), 2),
			Arguments.of(createEntityLists(1), 1),
			Arguments.of(createEntityLists(2), 0),
			Arguments.of(createEntityLists(3), 0)
		);
	}
	@ParameterizedTest
	@MethodSource("provideTestDataFindByNameContaining")
	void findByNameContaining(final List<MyEntity> entities, final int expectedSize)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findByNameContaining("Jo");
		assertEquals(expectedSize, result.size());
	}
	
	private static Stream<Arguments> provideTestDataFindByNameNative()
	{
		return Stream.of(
			Arguments.of(createEntityLists(0), 2),
			Arguments.of(createEntityLists(1), 1),
			Arguments.of(createEntityLists(2), 0),
			Arguments.of(createEntityLists(3), 0)
		);
	}
	@ParameterizedTest
	@MethodSource("provideTestDataFindByNameNative")
	void findByNameNative(final List<MyEntity> entities, final int expectedSize)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findByNameNative("John");
		assertEquals(expectedSize, result.size());
	}
	
	// TODO: This does not work currently, due to non existing parser in
	//  com.googlecode.cqengine.query.parser.common.QueryParser
	// private static Stream<Arguments> provideTestDataFindByCreationDateAfter()
	// {
	// 	return Stream.of(
	// 		Arguments.of(createEntityLists(0), 2),
	// 		Arguments.of(createEntityLists(1), 3),
	// 		Arguments.of(createEntityLists(2), 2),
	// 		Arguments.of(createEntityLists(3), 0)
	// 	);
	// }
	// @ParameterizedTest
	// @MethodSource("provideTestDataFindByCreationDateAfter")
	// void findByCreationDateAfter(final List<MyEntity> entities, final int expectedSize)
	// {
	// 	this.repository.saveAll(entities);
	// 	final List<MyEntity> result =
	// 		this.repository.findByCreationDateAfter(LocalDate.now().minusDays(1));
	// 	assertEquals(expectedSize, result.size());
	// }
	
	private static Stream<Arguments> provideTestDataFindByAgeBetween()
	{
		return Stream.of(
			Arguments.of(createEntityLists(0), 2),
			Arguments.of(createEntityLists(1), 2),
			Arguments.of(createEntityLists(2), 2),
			Arguments.of(createEntityLists(3), 0)
		);
	}
	@ParameterizedTest
	@MethodSource("provideTestDataFindByAgeBetween")
	void findByAgeBetween(final List<MyEntity> entities, final int expectedSize)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findByAgeBetween(20, 30);
		assertEquals(expectedSize, result.size());
	}
	
	private static Stream<Arguments> provideTestDataFindAllActive()
	{
		return Stream.of(
			Arguments.of(createEntityLists(0), 2),
			Arguments.of(createEntityLists(1), 0),
			Arguments.of(createEntityLists(2), 2),
			Arguments.of(createEntityLists(3), 0)
		);
	}
	@ParameterizedTest
	@MethodSource("provideTestDataFindAllActive")
	void findAllActive(final List<MyEntity> entities, final int expectedSize)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findAllActive();
		assertEquals(expectedSize, result.size());
	}
	
	private static Stream<Arguments> provideTestDataFindWhereOtherEntityIsNull()
	{
		return Stream.of(
			Arguments.of(createEntityLists(0), 0),
			Arguments.of(createEntityLists(1), 1),
			Arguments.of(createEntityLists(2), 2),
			Arguments.of(createEntityLists(3), 0)
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideTestDataFindWhereOtherEntityIsNull")
	void findWhereOtherEntityIsNull(final List<MyEntity> entities, final int expectedSize)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findWhereOtherEntityIsNull();
		assertEquals(expectedSize, result.size());
	}
	
	private static Stream<Arguments> provideTestDataFindWhereOtherEntityIsNotNull()
	{
		return Stream.of(
			Arguments.of(createEntityLists(0), 3),
			Arguments.of(createEntityLists(1), 2),
			Arguments.of(createEntityLists(2), 0),
			Arguments.of(createEntityLists(3), 0)
		);
	}
	@ParameterizedTest
	@MethodSource("provideTestDataFindWhereOtherEntityIsNotNull")
	void findWhereOtherEntityIsNotNull(final List<MyEntity> entities, final int expectedSize)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findWhereOtherEntityIsNotNull();
		assertEquals(expectedSize, result.size());
	}
	
	private static List<MyEntity> createEntityLists(final int testDataSetIndex)
	{
		final OtherEntity otherEntity = new OtherEntity();
		otherEntity.setDescription("Test OtherEntity");
		
		final Calendar calendarPastOneYear = Calendar.getInstance();
		calendarPastOneYear.add(Calendar.YEAR, -1);
		
		return switch(testDataSetIndex)
		{
			case 0 -> Arrays.asList(
				createMyEntity("John", 21, calendarPastOneYear.getTime(), true, otherEntity),
				createMyEntity("John", 25, false, otherEntity),
				createMyEntity("Doe", 40, true, otherEntity)
			);
			case 1 -> Arrays.asList(
				createMyEntity("John", 30, false, otherEntity),
				createMyEntity("Jane", 25, false, otherEntity),
				createMyEntity("Doe", 40, false, null)
			);
			case 2 -> Arrays.asList(
				createMyEntity("Jane", 22, true, null),
				createMyEntity("Bob", 28, true, null)
			);
			case 3 -> List.of();
			default -> throw new RuntimeException("Wrong index!");
		};
	}
	
	private static MyEntity createMyEntity(
		final String name,
		final int age,
		final boolean active,
		final OtherEntity otherEntity)
	{
		return createMyEntity(
			name,
			age,
			new Date(),
			active,
			otherEntity
		);
	}
	
	private static MyEntity createMyEntity(
		final String name,
		final int age,
		final Date creationDate,
		final boolean active,
		final OtherEntity otherEntity)
	{
		final MyEntity entity = new MyEntity();
		entity.setName(name);
		entity.setAge(age);
		entity.setCreationDate(creationDate);
		entity.setActive(active);
		entity.setOtherEntity(otherEntity);
		return entity;
	}
}
