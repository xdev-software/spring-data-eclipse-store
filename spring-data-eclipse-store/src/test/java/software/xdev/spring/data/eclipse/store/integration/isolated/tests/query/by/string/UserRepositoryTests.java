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
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {QueryTestConfiguration.class})
public class UserRepositoryTests
{
	@Autowired
	private UserRepository userRepository;
	
	@BeforeEach
	public void setUp()
	{
		this.userRepository.deleteAll();
		
		this.userRepository.save(new User(
			"John",
			"Doe",
			25,
			"john.doe@example.com",
			"New York",
			LocalDate.of(1998, 1, 1),
			true));
		this.userRepository.save(new User(
			"Jane",
			"Doe",
			30,
			"jane.doe@example.com",
			"Los Angeles",
			LocalDate.of(1993, 2, 2),
			false));
		this.userRepository.save(new User(
			"Alice",
			"Smith",
			28,
			"alice.smith@example.com",
			"New York",
			LocalDate.of(1996, 3, 3),
			true));
		this.userRepository.save(new User(
			"Bob",
			"Brown",
			35,
			"bob.brown@example.com",
			"Chicago",
			LocalDate.of(1988, 4, 4),
			true));
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForFindByFirstNameAndLastName")
	void testFindByFirstNameAndLastName(final String firstName, final String lastName, final int expectedSize)
	{
		final List<User> users = this.userRepository.findByFirstNameAndLastName(firstName, lastName);
		Assertions.assertEquals(expectedSize, users.size());
	}
	
	static Stream<Arguments> provideArgumentsForFindByFirstNameAndLastName()
	{
		return Stream.of(
			Arguments.of("John", "Doe", 1),
			Arguments.of("Jane", "Doe", 1),
			Arguments.of("Alice", "Smith", 1),
			Arguments.of("Bob", "Brown", 1),
			Arguments.of("NonExistent", "User", 0)
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForFindByFirstNameOrLastName")
	void testFindByFirstNameOrLastName(final String firstName, final String lastName, final int expectedSize)
	{
		final List<User> users = this.userRepository.findByFirstNameOrLastName(firstName, lastName);
		Assertions.assertEquals(expectedSize, users.size());
	}
	
	static Stream<Arguments> provideArgumentsForFindByFirstNameOrLastName()
	{
		return Stream.of(
			Arguments.of("John", "Smith", 2),
			Arguments.of("Jane", "Doe", 2),
			Arguments.of("NonExistent", "Brown", 1)
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForFindByAgeBetween")
	void testFindByAgeBetween(final int startAge, final int endAge, final int expectedSize)
	{
		final List<User> users = this.userRepository.findByAgeBetween(startAge, endAge);
		Assertions.assertEquals(expectedSize, users.size());
	}
	
	static Stream<Arguments> provideArgumentsForFindByAgeBetween()
	{
		return Stream.of(
			Arguments.of(20, 30, 3),
			Arguments.of(25, 35, 4),
			Arguments.of(30, 40, 2)
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForFindByAgeLessThan")
	void testFindByAgeLessThan(final int age, final int expectedSize)
	{
		final List<User> users = this.userRepository.findByAgeLessThan(age);
		Assertions.assertEquals(expectedSize, users.size());
	}
	
	static Stream<Arguments> provideArgumentsForFindByAgeLessThan()
	{
		return Stream.of(
			Arguments.of(30, 2),
			Arguments.of(35, 3),
			Arguments.of(40, 4)
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForFindByAgeLessThanEqual")
	void testFindByAgeLessThanEqual(final int age, final int expectedSize)
	{
		final List<User> users = this.userRepository.findByAgeLessThanEqual(age);
		Assertions.assertEquals(expectedSize, users.size());
	}
	
	static Stream<Arguments> provideArgumentsForFindByAgeLessThanEqual()
	{
		return Stream.of(
			Arguments.of(30, 3),
			Arguments.of(35, 4),
			Arguments.of(25, 1)
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForFindByAgeGreaterThan")
	void testFindByAgeGreaterThan(final int age, final int expectedSize)
	{
		final List<User> users = this.userRepository.findByAgeGreaterThan(age);
		Assertions.assertEquals(expectedSize, users.size());
	}
	
	static Stream<Arguments> provideArgumentsForFindByAgeGreaterThan()
	{
		return Stream.of(
			Arguments.of(25, 3),
			Arguments.of(30, 1),
			Arguments.of(35, 0)
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForFindByAgeGreaterThanEqual")
	void testFindByAgeGreaterThanEqual(final int age, final int expectedSize)
	{
		final List<User> users = this.userRepository.findByAgeGreaterThanEqual(age);
		Assertions.assertEquals(expectedSize, users.size());
	}
	
	static Stream<Arguments> provideArgumentsForFindByAgeGreaterThanEqual()
	{
		return Stream.of(
			Arguments.of(25, 4),
			Arguments.of(30, 2),
			Arguments.of(35, 1)
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForFindByDateOfBirthAfter")
	void testFindByDateOfBirthAfter(final LocalDate date, final int expectedSize)
	{
		final List<User> users = this.userRepository.findByDateOfBirthAfter(date);
		Assertions.assertEquals(expectedSize, users.size());
	}
	
	static Stream<Arguments> provideArgumentsForFindByDateOfBirthAfter()
	{
		return Stream.of(
			Arguments.of(LocalDate.of(1990, 1, 1), 3),
			Arguments.of(LocalDate.of(2000, 1, 1), 0),
			Arguments.of(LocalDate.of(1985, 1, 1), 4)
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForFindByDateOfBirthBefore")
	void testFindByDateOfBirthBefore(final LocalDate date, final int expectedSize)
	{
		final List<User> users = this.userRepository.findByDateOfBirthBefore(date);
		Assertions.assertEquals(expectedSize, users.size());
	}
	
	static Stream<Arguments> provideArgumentsForFindByDateOfBirthBefore()
	{
		return Stream.of(
			Arguments.of(LocalDate.of(1990, 1, 1), 1),
			Arguments.of(LocalDate.of(2000, 1, 1), 4),
			Arguments.of(LocalDate.of(1985, 1, 1), 0)
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForFindByEmailIsNull")
	void testFindByEmailIsNull(final int expectedSize)
	{
		final List<User> users = this.userRepository.findByEmailIsNull();
		Assertions.assertEquals(expectedSize, users.size());
	}
	
	static Stream<Arguments> provideArgumentsForFindByEmailIsNull()
	{
		return Stream.of(
			Arguments.of(0)
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForFindByEmailIsNotNull")
	void testFindByEmailIsNotNull(final int expectedSize)
	{
		final List<User> users = this.userRepository.findByEmailIsNotNull();
		Assertions.assertEquals(expectedSize, users.size());
	}
	
	static Stream<Arguments> provideArgumentsForFindByEmailIsNotNull()
	{
		return Stream.of(
			Arguments.of(4)
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForFindByFirstNameLike")
	void testFindByFirstNameLike(final String pattern, final int expectedSize)
	{
		final List<User> users = this.userRepository.findByFirstNameLike(pattern);
		Assertions.assertEquals(expectedSize, users.size());
	}
	
	static Stream<Arguments> provideArgumentsForFindByFirstNameLike()
	{
		return Stream.of(
			Arguments.of("John", 1),
			Arguments.of("J%", 2),
			Arguments.of("A%", 1)
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForFindByFirstNameNotLike")
	void testFindByFirstNameNotLike(final String pattern, final int expectedSize)
	{
		final List<User> users = this.userRepository.findByFirstNameNotLike(pattern);
		Assertions.assertEquals(expectedSize, users.size());
	}
	
	static Stream<Arguments> provideArgumentsForFindByFirstNameNotLike()
	{
		return Stream.of(
			Arguments.of("John", 3),
			Arguments.of("J%", 2),
			Arguments.of("A%", 3)
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForFindByFirstNameStartingWith")
	void testFindByFirstNameStartingWith(final String prefix, final int expectedSize)
	{
		final List<User> users = this.userRepository.findByFirstNameStartingWith(prefix);
		Assertions.assertEquals(expectedSize, users.size());
	}
	
	static Stream<Arguments> provideArgumentsForFindByFirstNameStartingWith()
	{
		return Stream.of(
			Arguments.of("J", 2),
			Arguments.of("A", 1),
			Arguments.of("B", 1),
			Arguments.of("b", 0),
			Arguments.of("x", 0)
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForFindByFirstNameEndingWith")
	void testFindByFirstNameEndingWith(final String suffix, final int expectedSize)
	{
		final List<User> users = this.userRepository.findByFirstNameEndingWith(suffix);
		Assertions.assertEquals(expectedSize, users.size());
	}
	
	static Stream<Arguments> provideArgumentsForFindByFirstNameEndingWith()
	{
		return Stream.of(
			Arguments.of("n", 1),
			Arguments.of("e", 2),
			Arguments.of("b", 1),
			Arguments.of("x", 0),
			Arguments.of("", 4)
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForFindByFirstNameContaining")
	void testFindByFirstNameContaining(final String infix, final int expectedSize)
	{
		final List<User> users = this.userRepository.findByFirstNameContaining(infix);
		Assertions.assertEquals(expectedSize, users.size());
	}
	
	static Stream<Arguments> provideArgumentsForFindByFirstNameContaining()
	{
		return Stream.of(
			Arguments.of("o", 2),
			Arguments.of("a", 1),
			Arguments.of("A", 1),
			Arguments.of("i", 1),
			Arguments.of("", 4)
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForFindByCityOrderByFirstNameAsc")
	void testFindByCityOrderByFirstNameAsc(final String city, final int expectedSize, final String firstName)
	{
		final List<User> users = this.userRepository.findByCityOrderByFirstNameAsc(city);
		Assertions.assertEquals(expectedSize, users.size());
		Assertions.assertEquals(firstName, users.get(0).getFirstName());
	}
	
	static Stream<Arguments> provideArgumentsForFindByCityOrderByFirstNameAsc()
	{
		return Stream.of(
			Arguments.of("New York", 2, "Alice"),
			Arguments.of("Los Angeles", 1, "Jane"),
			Arguments.of("Chicago", 1, "Bob")
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForFindByFirstNameNot")
	void testFindByFirstNameNot(final String firstName, final int expectedSize)
	{
		final List<User> users = this.userRepository.findByFirstNameNot(firstName);
		Assertions.assertEquals(expectedSize, users.size());
	}
	
	static Stream<Arguments> provideArgumentsForFindByFirstNameNot()
	{
		return Stream.of(
			Arguments.of("John", 3),
			Arguments.of("Jane", 3),
			Arguments.of("Alice", 3),
			Arguments.of("Bob", 3),
			Arguments.of("Sepp", 4)
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForFindByAgeIn")
	void testFindByAgeIn(final List<Integer> ages, final int expectedSize)
	{
		final List<User> users = this.userRepository.findByAgeIn(ages);
		Assertions.assertEquals(expectedSize, users.size());
	}
	
	static Stream<Arguments> provideArgumentsForFindByAgeIn()
	{
		return Stream.of(
			Arguments.of(List.of(25, 30), 2),
			Arguments.of(List.of(28, 35), 2),
			Arguments.of(List.of(40), 0)
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForFindByAgeNotIn")
	void testFindByAgeNotIn(final List<Integer> ages, final int expectedSize)
	{
		final List<User> users = this.userRepository.findByAgeNotIn(ages);
		Assertions.assertEquals(expectedSize, users.size());
	}
	
	static Stream<Arguments> provideArgumentsForFindByAgeNotIn()
	{
		return Stream.of(
			Arguments.of(List.of(25, 30), 2),
			Arguments.of(List.of(28, 35), 2),
			Arguments.of(List.of(40), 4)
		);
	}
	
	@Test
	void testFindByIsActiveTrue()
	{
		// Assuming all users are active for this example
		final List<User> users = this.userRepository.findByIsActiveTrue();
		Assertions.assertEquals(3, users.size());
	}
	
	@Test
	void testFindByIsActiveFalse()
	{
		// Assuming all users are inactive for this example
		final List<User> users = this.userRepository.findByIsActiveFalse();
		Assertions.assertEquals(1, users.size());
	}
}
