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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.query.by.example;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {QueryByExampleTestConfiguration.class})
class QueryByExampleTest
{
	private final QueryByExampleTestConfiguration configuration;
	private final UserRepository userRepository;
	private User user1;
	private User user2;
	
	@Autowired
	public QueryByExampleTest(final QueryByExampleTestConfiguration configuration, final UserRepository userRepository)
	{
		this.configuration = configuration;
		this.userRepository = userRepository;
	}
	
	@BeforeEach
	void initData()
	{
		this.user1 = new User(1, TestData.FIRST_NAME, BigDecimal.TEN);
		this.user2 = new User(2, TestData.FIRST_NAME_ALTERNATIVE, BigDecimal.TEN);
		this.userRepository.saveAll(List.of(this.user1, this.user2));
	}
	
	@Test
	void simpleEqualsQuery()
	{
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final User probe = new User(1, TestData.FIRST_NAME, BigDecimal.TEN);
				final List<User> foundUsers = TestUtil.iterableToList(this.userRepository.findAll(Example.of(probe)));
				Assertions.assertEquals(1, foundUsers.size());
				Assertions.assertEquals(this.user1, foundUsers.get(0));
			}
		);
	}
	
	@Test
	void fluentApiFindSingle()
	{
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final User probe = new User(1, TestData.FIRST_NAME, BigDecimal.TEN);
				final Optional<User> foundUser =
					this.userRepository.findBy(
						Example.of(probe),
						user -> user
							.sortBy(Sort.by("name").descending())
							.first()
					);
				Assertions.assertTrue(foundUser.isPresent());
				Assertions.assertEquals(this.user1, foundUser.get());
			}
		);
	}
	
	@Test
	void exampleMatcher()
	{
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final User probe = new User(1, TestData.FIRST_NAME, BigDecimal.TEN);
				
				final ExampleMatcher matcher = ExampleMatcher.matching()
					.withMatcher("name", ExampleMatcher.GenericPropertyMatchers.endsWith())
					.withMatcher("balance", );
				
				final Optional<User> foundUser =
					this.userRepository.findBy(
						Example.of(
							probe,
							
							)
					);
				Assertions.assertTrue(foundUser.isPresent());
				Assertions.assertEquals(this.user1, foundUser.get());
			}
		);
	}
}
