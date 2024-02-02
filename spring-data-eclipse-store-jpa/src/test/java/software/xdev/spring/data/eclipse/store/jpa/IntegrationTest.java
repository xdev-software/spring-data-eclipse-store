/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package software.xdev.spring.data.eclipse.store.jpa;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import software.xdev.spring.data.eclipse.store.jpa.repository.Customer;
import software.xdev.spring.data.eclipse.store.jpa.repository.CustomerRepository;
import software.xdev.spring.data.eclipse.store.jpa.repository.PetRepository;
import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;


@DefaultTestAnnotations
public class IntegrationTest
{
	@Autowired
	private EclipseStoreStorage storage;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private PetRepository petRepository;

	
	@Test
	void testBasicSaveAndFindSingleRecords()
	{
		final Customer customer = new Customer("", "");
		this.customerRepository.save(customer);
		
		final List<Customer> customers = iterableToList(this.customerRepository.findAll());
		Assertions.assertEquals(1, customers.size());
		Assertions.assertEquals(customer, customers.get(0));
	}
	
	public static <T> List<T> iterableToList(final Iterable<T> iterable)
	{
		final List<T> list = new ArrayList<>();
		iterable.forEach(list::add);
		return list;
	}
}
