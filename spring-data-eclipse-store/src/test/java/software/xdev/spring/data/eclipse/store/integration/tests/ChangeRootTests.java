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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.DefaultTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.repositories.Node;
import software.xdev.spring.data.eclipse.store.integration.repositories.NodeRepository;
import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;


@DefaultTestAnnotations
public class ChangeRootTests
{
	public static final String CHILD_1 = "child1";
	public static final String CHILD_2 = "child2";
	public static final String PARENT_1 = "parent1";
	public static final String PARENT_2 = "parent2";
	
	@Autowired
	private NodeRepository repository;
	@Autowired
	private EclipseStoreClientConfiguration configuration;
	
	//@formatter:off
	/**
     * ┍-□     □ □-|    => □ └-□     □
     **/
	//@formatter:on
	@Test
	void testSaveParentAndFindAll()
	{
		final Node childNode1 = new Node(CHILD_1);
		final Node childNode2 = new Node(CHILD_2);
		final Node parentNode = new Node(PARENT_1, List.of(childNode1, childNode2));
		this.repository.save(parentNode);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<Node> nodes = TestUtil.iterableToList(this.repository.findAll());
				Assertions.assertEquals(3, nodes.size());
				Assertions.assertTrue(Node.getNodeWithName(nodes, PARENT_1).isPresent());
				Assertions.assertTrue(Node.getNodeWithName(nodes, CHILD_1).isPresent());
				Assertions.assertTrue(Node.getNodeWithName(nodes, CHILD_2).isPresent());
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
	void testSaveRecursive()
	{
		final Node childNode1 = new Node(CHILD_1);
		final Node parentNode1 = new Node(PARENT_1, List.of(childNode1));
		final Node parentNode2 = new Node(PARENT_2);
		childNode1.getChildren().add(parentNode2);
		this.repository.save(parentNode1);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<Node> nodes = TestUtil.iterableToList(this.repository.findAll());
				Assertions.assertEquals(3, nodes.size());
				Assertions.assertTrue(Node.getNodeWithName(nodes, PARENT_1).isPresent());
				Assertions.assertTrue(Node.getNodeWithName(nodes, CHILD_1).isPresent());
				Assertions.assertTrue(Node.getNodeWithName(nodes, PARENT_2).isPresent());
			}
		);
	}
	
	//@formatter:off
	/**
	 *   ┍-□
	 * □-|
	 * └-□
	 **/
	//@formatter:on
	@Test
	void testSaveGraph()
	{
		final Node childNode1 = new Node(CHILD_1);
		final Node childNode2 = new Node(CHILD_2);
		final Node parentNode = new Node(PARENT_1, List.of(childNode1, childNode2));
		childNode2.getChildren().add(parentNode);
		this.repository.save(parentNode);
		
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<Node> nodes = TestUtil.iterableToList(this.repository.findAll());
				Assertions.assertEquals(3, nodes.size());
				Assertions.assertTrue(Node.getNodeWithName(nodes, PARENT_1).isPresent());
				Assertions.assertTrue(Node.getNodeWithName(nodes, CHILD_1).isPresent());
				Assertions.assertTrue(Node.getNodeWithName(nodes, CHILD_2).isPresent());
			}
		);
	}
}
