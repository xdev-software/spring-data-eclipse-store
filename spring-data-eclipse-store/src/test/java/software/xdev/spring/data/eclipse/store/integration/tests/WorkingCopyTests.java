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
package software.xdev.spring.data.eclipse.store.integration.tests;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.DefaultTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.repositories.Customer;
import software.xdev.spring.data.eclipse.store.integration.repositories.CustomerRepository;
import software.xdev.spring.data.eclipse.store.integration.repositories.Node;
import software.xdev.spring.data.eclipse.store.integration.repositories.NodeRepository;
import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;


@SuppressWarnings("OptionalGetWithoutIsPresent")
@DefaultTestAnnotations
public class WorkingCopyTests
{
	public static final String CHILD_NAME_1 = "child1";
	public static final String PARENT_NAME_2 = "parent2";
	public static final String PARENT_NAME_1 = "parent1";
	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	NodeRepository nodeRepository;
	@Autowired
	private EclipseStoreStorage storage;
	
	@Test
	void testBasicChangeOnlyAfterSave()
	{
		// Save default customer
		final Customer customer = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.customerRepository.save(customer);
		// Change In-Memory customer
		customer.setFirstName(TestData.FIRST_NAME_ALTERNATIVE);
		customer.setLastName(TestData.LAST_NAME_ALTERNATIVE);
		
		// Check saved customer
		final List<Customer> customers = TestUtil.iterableToList(this.customerRepository.findAll());
		Assertions.assertEquals(1, customers.size());
		Assertions.assertEquals(TestData.FIRST_NAME, customers.get(0).getFirstName());
		Assertions.assertEquals(TestData.LAST_NAME, customers.get(0).getLastName());
		
		// Save changed customer
		this.customerRepository.save(customer);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				// Check saved customer
				final List<Customer> customers2 = TestUtil.iterableToList(this.customerRepository.findAll());
				Assertions.assertEquals(1, customers2.size());
				Assertions.assertEquals(TestData.FIRST_NAME_ALTERNATIVE, customers2.get(0).getFirstName());
				Assertions.assertEquals(TestData.LAST_NAME_ALTERNATIVE, customers2.get(0).getLastName());
			}
		);
	}
	
	@Test
	void testBasicCopy()
	{
		final Customer customer = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.customerRepository.save(customer);
		final List<Customer> customers = TestUtil.iterableToList(this.customerRepository.findAll());
		Assertions.assertNotSame(customer, customers.get(0));
	}
	
	@Test
	void testBasicDoubleCopy()
	{
		final Customer customer = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.customerRepository.save(customer);
		final List<Customer> customers1 = TestUtil.iterableToList(this.customerRepository.findAll());
		final List<Customer> customers2 = TestUtil.iterableToList(this.customerRepository.findAll());
		Assertions.assertNotSame(customers1.get(0), customers2.get(0));
	}
	
	//@formatter:off
	/**
	 * □-□
	 * □-┘
	 **/
	//@formatter:on
	@Test
	void testStoreGraphWithCircularRelation()
	{
		final String parent1Name = PARENT_NAME_1;
		final String parent2Name = PARENT_NAME_2;
		final Node childNode = new Node(CHILD_NAME_1);
		final Node parentNode1 = new Node(parent1Name, List.of(childNode));
		final Node parentNode2 = new Node(parent2Name);
		childNode.getChildren().add(parentNode2);
		
		this.nodeRepository.save(parentNode1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final List<Node> loadedNodes = TestUtil.iterableToList(this.nodeRepository.findAll());
				Assertions.assertEquals(3, loadedNodes.size());
				
				final Optional<Node> loadedChildNode =
					loadedNodes.stream().filter(node -> node.getName().equals(CHILD_NAME_1)).findFirst();
				Assertions.assertTrue(loadedChildNode.isPresent());
				final Optional<Node> loadedParentNode1 =
					loadedNodes.stream().filter(node -> node.getName().equals(parent1Name)).findFirst();
				Assertions.assertTrue(loadedParentNode1.isPresent());
				final Optional<Node> loadedParentNode2 =
					loadedNodes.stream().filter(node -> node.getName().equals(parent2Name)).findFirst();
				Assertions.assertTrue(loadedParentNode2.isPresent());
				
				Assertions.assertSame(loadedParentNode1.get().getChildren().get(0), loadedChildNode.get());
				Assertions.assertSame(loadedChildNode.get().getChildren().get(0), loadedParentNode2.get());
			}
		);
	}
	
	//@formatter:off
	/**
	 * □-□
	 * □-┘
	 **/
	//@formatter:on
	@Test
	void testStoreGraphWithCircularRelationWithDoubleSave()
	{
		final Node childNode = new Node(CHILD_NAME_1);
		final Node parentNode1 = new Node(PARENT_NAME_1, List.of(childNode));
		final Node parentNode2 = new Node(PARENT_NAME_2);
		childNode.getChildren().add(parentNode2);
		
		this.nodeRepository.save(parentNode1);
		this.nodeRepository.save(parentNode2);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final List<Node> loadedNodes = TestUtil.iterableToList(this.nodeRepository.findAll());
				Assertions.assertEquals(3, loadedNodes.size());
				
				final Node loadedChildNode =
					loadedNodes.stream().filter(node -> node.getName().equals(CHILD_NAME_1)).findFirst().get();
				Assertions.assertEquals(CHILD_NAME_1, loadedChildNode.getName());
				final Node loadedParentNode1 =
					loadedNodes.stream().filter(node -> node.getName().equals(PARENT_NAME_1)).findFirst().get();
				Assertions.assertEquals(PARENT_NAME_1, loadedParentNode1.getName());
				final Node loadedParentNode2 =
					loadedNodes.stream().filter(node -> node.getName().equals(PARENT_NAME_2)).findFirst().get();
				Assertions.assertEquals(PARENT_NAME_2, loadedParentNode2.getName());
				
				Assertions.assertSame(loadedParentNode1.getChildren().get(0), loadedChildNode);
				Assertions.assertSame(loadedChildNode.getChildren().get(0), loadedParentNode2);
			}
		);
	}
	
	//@formatter:off
	/**
	 * □-□
	 * □-┘
	 **/
	//@formatter:on
	@Test
	void testStoreGraphWithCircularRelationWithTrippleSave()
	{
		final Node childNode = new Node(CHILD_NAME_1);
		final Node parentNode1 = new Node(PARENT_NAME_1, List.of(childNode));
		final Node parentNode2 = new Node(PARENT_NAME_2);
		childNode.getChildren().add(parentNode2);
		
		this.nodeRepository.save(parentNode1);
		this.nodeRepository.save(parentNode2);
		this.nodeRepository.save(childNode);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final List<Node> loadedNodes = TestUtil.iterableToList(this.nodeRepository.findAll());
				Assertions.assertEquals(3, loadedNodes.size());
				
				final Node loadedChildNode =
					loadedNodes.stream().filter(node -> node.getName().equals(CHILD_NAME_1)).findFirst().get();
				Assertions.assertEquals(CHILD_NAME_1, loadedChildNode.getName());
				final Node loadedParentNode1 =
					loadedNodes.stream().filter(node -> node.getName().equals(PARENT_NAME_1)).findFirst().get();
				Assertions.assertEquals(PARENT_NAME_1, loadedParentNode1.getName());
				final Node loadedParentNode2 =
					loadedNodes.stream().filter(node -> node.getName().equals(PARENT_NAME_2)).findFirst().get();
				Assertions.assertEquals(PARENT_NAME_2, loadedParentNode2.getName());
				
				Assertions.assertSame(loadedParentNode1.getChildren().get(0), loadedChildNode);
				Assertions.assertSame(loadedChildNode.getChildren().get(0), loadedParentNode2);
			}
		);
	}
	
	//@formatter:off
	/**
	 * □-□
	 * □-┘
	 **/
	//@formatter:on
	@Test
	void testModifyGraph()
	{
		final String changedParentNode2Name = "changedParent2";
		final Node childNode = new Node(CHILD_NAME_1);
		final Node parentNode1 = new Node(PARENT_NAME_1, List.of(childNode));
		final Node parentNode2 = new Node(PARENT_NAME_2);
		childNode.getChildren().add(parentNode2);
		this.nodeRepository.save(parentNode1);
		this.nodeRepository.save(parentNode2);
		final List<Node> loadedNodes1 = TestUtil.iterableToList(this.nodeRepository.findAll());
		Assertions.assertEquals(3, loadedNodes1.size());
		Node loadedChildNode =
			loadedNodes1.stream().filter(node -> node.getName().equals(CHILD_NAME_1)).findFirst().get();
		final Node loadedParentNode2 =
			loadedNodes1.stream().filter(node -> node.getName().equals(PARENT_NAME_2)).findFirst().get();
		// Change working copy
		loadedParentNode2.setName(changedParentNode2Name);
		Assertions.assertEquals(changedParentNode2Name, loadedChildNode.getChildren().get(0).getName());
		
		// No change in stored data before saving
		final List<Node> loadedNodes2 = TestUtil.iterableToList(this.nodeRepository.findAll());
		loadedChildNode = loadedNodes2.stream().filter(node -> node.getName().equals(CHILD_NAME_1)).findFirst().get();
		Assertions.assertEquals(PARENT_NAME_2, loadedChildNode.getChildren().get(0).getName());
		
		// Change in stored data after saving
		this.nodeRepository.saveAll(loadedNodes1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final List<Node> loadedNodes3 = TestUtil.iterableToList(this.nodeRepository.findAll());
				final Node loadedChildNode2 =
					loadedNodes3.stream().filter(node -> node.getName().equals(CHILD_NAME_1)).findFirst().get();
				Assertions.assertEquals(changedParentNode2Name, loadedChildNode2.getChildren().get(0).getName());
			}
		);
	}
	
	@Test
	void testBasicFindByFirstNameOneResult()
	{
		final Customer customer1 = new Customer(TestData.FIRST_NAME, TestData.LAST_NAME);
		this.customerRepository.save(customer1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.storage,
			() -> {
				final Optional<Customer> foundCustomer = this.customerRepository.findByFirstName(TestData.FIRST_NAME);
				Assertions.assertNotSame(customer1, foundCustomer.get());
			}
		);
	}
}
