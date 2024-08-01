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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.query.by.string;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository;


@Repository
public interface UserRepository extends EclipseStoreRepository<User, Long>
{
	
	// Test keyword: And
	List<User> findByFirstNameAndLastName(String firstName, String lastName);
	
	// Test keyword: Or
	List<User> findByFirstNameOrLastName(String firstName, String lastName);
	
	// Test keyword: Between
	List<User> findByAgeBetween(Integer startAge, Integer endAge);
	
	// Test keyword: LessThan
	List<User> findByAgeLessThan(Integer age);
	
	// Test keyword: LessThanEqual
	List<User> findByAgeLessThanEqual(Integer age);
	
	// Test keyword: GreaterThan
	List<User> findByAgeGreaterThan(Integer age);
	
	// Test keyword: GreaterThanEqual
	List<User> findByAgeGreaterThanEqual(Integer age);
	
	// Test keyword: After
	List<User> findByDateOfBirthAfter(LocalDate date);
	
	// Test keyword: Before
	List<User> findByDateOfBirthBefore(LocalDate date);
	
	// Test keyword: IsNull
	List<User> findByEmailIsNull();
	
	// Test keyword: IsNotNull
	List<User> findByEmailIsNotNull();
	
	// Test keyword: Like
	List<User> findByFirstNameLike(String pattern);
	
	List<User> findByFirstNameLikeIgnoreCase(String pattern);
	
	// Test keyword: NotLike
	List<User> findByFirstNameNotLike(String pattern);
	
	// Test keyword: StartingWith
	List<User> findByFirstNameStartingWith(String prefix);
	
	// Test keyword: EndingWith
	List<User> findByFirstNameEndingWith(String suffix);
	
	// Test keyword: Containing
	List<User> findByFirstNameContaining(String infix);
	
	// Test keyword: OrderBy
	List<User> findByCityOrderByFirstNameAsc(String city);
	
	// Test keyword: Not
	List<User> findByFirstNameNot(String firstName);
	
	// Test keyword: In
	List<User> findByAgeIn(List<Integer> ages);
	
	// Test keyword: NotIn
	List<User> findByAgeNotIn(List<Integer> ages);
	
	// Test keyword: True
	List<User> findByIsActiveTrue();
	
	// Test keyword: False
	List<User> findByIsActiveFalse();
	
	// Additional fields to handle boolean flag for active status
	List<User> findByIsActive(Boolean isActive);
}
