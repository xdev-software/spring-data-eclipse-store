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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {HsqlTestConfiguration.class})
class HsqlTest
{
	@Autowired
	private MyEntityRepository repository;
	
	@ParameterizedTest
	@MethodSource("provideTestData")
	void testFindAllEntities(final List<MyEntity> entities)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findAllEntities();
		assertEquals(entities.size(), result.size());
	}
	
	@ParameterizedTest
	@MethodSource("provideTestData")
	void testFindByName(final List<MyEntity> entities)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findByName("John");
		assertEquals(1, result.size());
		assertEquals("John", result.get(0).getName());
	}
	
	@ParameterizedTest
	@MethodSource("provideTestData")
	void testFindByNameAndAgeGreaterThan(final List<MyEntity> entities)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findByNameAndAgeGreaterThan("John", 25);
		assertEquals(1, result.size());
		assertEquals("John", result.get(0).getName());
	}
	
	@ParameterizedTest
	@MethodSource("provideTestData")
	void testFindAllOrderByAgeDesc(final List<MyEntity> entities)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findAllOrderByAgeDesc();
		assertEquals(entities.size(), result.size());
		assertEquals(40, result.get(0).getAge());
	}
	
	@ParameterizedTest
	@MethodSource("provideTestData")
	void testFindTop5ByOrderByAgeDesc(final List<MyEntity> entities)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findTop5ByOrderByAgeDesc();
		assertEquals(Math.min(5, entities.size()), result.size());
	}
	
	@ParameterizedTest
	@MethodSource("provideTestData")
	void testFindDistinctNames(final List<MyEntity> entities)
	{
		this.repository.saveAll(entities);
		final List<String> result = this.repository.findDistinctNames();
		assertEquals(entities.stream().map(MyEntity::getName).distinct().count(), result.size());
	}
	
	@ParameterizedTest
	@MethodSource("provideTestDataWithOtherEntity")
	void testFindByOtherEntityId(final List<MyEntity> entities, final OtherEntity otherEntity)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findByOtherEntityId(otherEntity.getId());
		assertEquals(entities.stream()
			.filter(e -> e.getOtherEntity() != null && e.getOtherEntity().getId().equals(otherEntity.getId()))
			.count(), result.size());
	}
	
	@ParameterizedTest
	@MethodSource("provideTestData")
	void testCountByName(final List<MyEntity> entities)
	{
		this.repository.saveAll(entities);
		final List<Object[]> result = this.repository.countByName();
		assertNotNull(result);
	}
	
	@ParameterizedTest
	@MethodSource("provideTestData")
	void testCountByNameHavingMoreThan(final List<MyEntity> entities)
	{
		this.repository.saveAll(entities);
		final List<Object[]> result = this.repository.countByNameHavingMoreThan(1);
		assertNotNull(result);
	}
	
	@ParameterizedTest
	@MethodSource("provideTestData")
	void testFindEntityWithMaxAge(final List<MyEntity> entities)
	{
		this.repository.saveAll(entities);
		final MyEntity result = this.repository.findEntityWithMaxAge();
		assertEquals(40, result.getAge());
	}
	
	@ParameterizedTest
	@MethodSource("provideTestData")
	void testFindByNameIn(final List<MyEntity> entities)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findByNameIn(Arrays.asList("John", "Jane"));
		assertEquals(2, result.size());
	}
	
	@ParameterizedTest
	@MethodSource("provideTestData")
	void testFindByNameContaining(final List<MyEntity> entities)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findByNameContaining("Jo");
		assertEquals(1, result.size());
	}
	
	@ParameterizedTest
	@MethodSource("provideTestData")
	void testFindByNameNative(final List<MyEntity> entities)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findByNameNative("John");
		assertEquals(1, result.size());
	}
	
	@ParameterizedTest
	@MethodSource("provideTestData")
	void testFindByCreationDateAfter(final List<MyEntity> entities)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result =
			this.repository.findByCreationDateAfter(LocalDate.now().minusDays(1));
		assertEquals(entities.size(), result.size());
	}
	
	@ParameterizedTest
	@MethodSource("provideTestData")
	void testFindByAgeBetween(final List<MyEntity> entities)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findByAgeBetween(20, 30);
		assertEquals(2, result.size());
	}
	
	@ParameterizedTest
	@MethodSource("provideTestData")
	void testFindAllActive(final List<MyEntity> entities)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findAllActive();
		assertEquals(entities.stream().filter(MyEntity::isActive).count(), result.size());
	}
	
	@ParameterizedTest
	@MethodSource("provideTestData")
	void testFindWhereOtherEntityIsNull(final List<MyEntity> entities)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findWhereOtherEntityIsNull();
		assertEquals(entities.stream().filter(e -> e.getOtherEntity() == null).count(), result.size());
	}
	
	@ParameterizedTest
	@MethodSource("provideTestDataWithOtherEntity")
	void testFindWhereOtherEntityIsNotNull(final List<MyEntity> entities)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findWhereOtherEntityIsNotNull();
		assertEquals(entities.stream().filter(e -> e.getOtherEntity() != null).count(), result.size());
	}
	//
	// @ParameterizedTest
	// @MethodSource("provideTestData")
	// void testFindAllAsDTO(List<MyEntity> entities) {
	// 	repository.saveAll(entities);
	// 	List<MyEntityDTO> result = repository.findAllAsDTO();
	// 	assertEquals(entities.size(), result.size());
	// }
	
	@ParameterizedTest
	@MethodSource("provideTestData")
	void testFindByCreationYear(final List<MyEntity> entities)
	{
		this.repository.saveAll(entities);
		final List<MyEntity> result = this.repository.findByCreationYear(Calendar.getInstance().get(Calendar.YEAR));
		assertEquals(entities.size(), result.size());
	}
	
	private static Stream<Arguments> provideTestData()
	{
		return Stream.of(
			Arguments.of(Arrays.asList(
				createMyEntity("John", 30, true, null),
				createMyEntity("Jane", 25, false, null),
				createMyEntity("Doe", 40, true, null)
			)),
			Arguments.of(Arrays.asList(
				createMyEntity("Alice", 22, true, null),
				createMyEntity("Bob", 28, false, null),
				createMyEntity("Charlie", 35, true, null)
			))
		);
	}
	
	private static Stream<Arguments> provideTestDataWithOtherEntity()
	{
		final OtherEntity otherEntity = new OtherEntity();
		otherEntity.setDescription("Test OtherEntity");
		return Stream.of(
			Arguments.of(Arrays.asList(
				createMyEntity("John", 30, true, otherEntity),
				createMyEntity("Jane", 25, false, null),
				createMyEntity("Doe", 40, true, otherEntity)
			), otherEntity)
		);
	}
	
	private static MyEntity createMyEntity(
		final String name,
		final int age,
		final boolean active,
		final OtherEntity otherEntity)
	{
		final MyEntity entity = new MyEntity();
		entity.setName(name);
		entity.setAge(age);
		entity.setCreationDate(LocalDate.now());
		entity.setActive(active);
		entity.setOtherEntity(otherEntity);
		return entity;
	}
}
