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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.real.life.examples.model;

import java.util.List;
import java.util.Optional;


public class Position
{
	private Article article;
	private int amount;
	
	public Position(final Article article, final int amount)
	{
		this.article = article;
		this.amount = amount;
	}
	
	public void setArticle(final Article article)
	{
		this.article = article;
	}
	
	public void setAmount(final int amount)
	{
		this.amount = amount;
	}
	
	public Article getArticle()
	{
		return this.article;
	}
	
	public int getAmount()
	{
		return this.amount;
	}
	
	public static Optional<Position> getPositionWithArticleWithName(
		final List<Position> positions,
		final String articleName)
	{
		return positions.stream().filter(pos -> pos.getArticle().getName().equals(articleName)).findFirst();
	}
}
