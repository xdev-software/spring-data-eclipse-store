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
package software.xdev.spring.data.eclipse.store.integration.repositories.real.life.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Article
{
	private final String name;
	private final ArticleGroup group;
	private final List<Warehouse> warehouses;
	
	public Article(final String name, final ArticleGroup group, final List<Warehouse> warehouses)
	{
		this.name = name;
		this.group = group;
		this.warehouses = warehouses;
		this.warehouses.forEach(warehouse -> warehouse.getArticles().add(this));
	}
	
	public Article(final String name, final ArticleGroup group, final Warehouse warehouse)
	{
		this(name, group, new ArrayList<>(Collections.singletonList(warehouse)));
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public ArticleGroup getGroup()
	{
		return this.group;
	}
	
	public List<Warehouse> getWarehouses()
	{
		return this.warehouses;
	}
}
