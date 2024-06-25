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
package software.xdev.spring.data.eclipse.store.integration.shared.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Node
{
	private String name;
	
	private List<Node> children;
	
	public Node(final String name, final List<Node> children)
	{
		this.name = name;
		this.children = children;
	}
	
	public Node(final String name)
	{
		this(name, new ArrayList<>());
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setName(final String name)
	{
		this.name = name;
	}
	
	public void setChildren(final List<Node> children)
	{
		this.children = children;
	}
	
	public List<Node> getChildren()
	{
		return this.children;
	}
	
	@Override
	public String toString()
	{
		return "Node{"
			+ "name='" + this.name + '\''
			+ ", children=" + this.children.size()
			+ '}';
	}
	
	public static Optional<Node> getNodeWithName(final List<Node> nodes, final String name)
	{
		return nodes.stream().filter(node -> node.getName().equals(name)).findFirst();
	}
}
