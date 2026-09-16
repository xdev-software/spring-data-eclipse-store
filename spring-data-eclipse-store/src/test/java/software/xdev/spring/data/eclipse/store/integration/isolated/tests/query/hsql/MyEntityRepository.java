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

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;

import software.xdev.spring.data.eclipse.store.repository.Query;


@SuppressWarnings("checkstyle:TodoComment")
public interface MyEntityRepository extends ListCrudRepository<MyEntity, Long>
{
	// Simple Select
	@Query("SELECT * FROM MyEntity")
	List<MyEntity> findAllEntities();
	
	// Select with a where clause
	@Query("SELECT * FROM MyEntity WHERE name = '?1'")
	List<MyEntity> findByName(String name);
	
	// Select with multiple where clauses
	@Query(" SELECT * FROM MyEntity WHERE (name = '?1' AND age > ?2)")
	List<MyEntity> findByNameAndAgeGreaterThan(String name, int age);
	
	// Select with order by
	@Query(" SELECT * FROM MyEntity ORDER BY age DESC")
	List<MyEntity> findAllOrderByAgeDesc();
	
	// Select with IN clause
	@Query(" SELECT * FROM MyEntity WHERE name IN ?1")
	List<MyEntity> findByNameIn(List<String> names);
	
	// Select with LIKE clause
	@Query(" SELECT * FROM MyEntity WHERE 'name' LIKE '%?1%'")
	List<MyEntity> findByNameContaining(String keyword);
	
	// Select with native query
	@Query(value = "SELECT * FROM my_entity WHERE name = '?1'")
	List<MyEntity> findByNameNative(String name);
	
	// Select with date comparison
	// TODO: This does not work currently, due to non existing parser in
	//  com.googlecode.cqengine.query.parser.common.QueryParser
	// @Query("SELECT * FROM MyEntity WHERE creationDate > '?1'")
	// List<MyEntity> findByCreationDateAfter(LocalDate date);
	
	// Select with between clause
	@Query("SELECT * FROM MyEntity WHERE age BETWEEN ?1 AND ?2")
	List<MyEntity> findByAgeBetween(int startAge, int endAge);
	
	// Select with boolean condition
	@Query("SELECT * FROM MyEntity WHERE active = true")
	List<MyEntity> findAllActive();
	
	// Select with is null condition
	@Query("SELECT * FROM MyEntity WHERE otherEntity IS NULL")
	List<MyEntity> findWhereOtherEntityIsNull();
	
	// Select with is not null condition
	@Query("SELECT * FROM MyEntity WHERE otherEntity IS NOT NULL")
	List<MyEntity> findWhereOtherEntityIsNotNull();
}
