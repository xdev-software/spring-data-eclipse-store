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
package software.xdev.spring.data.eclipse.store.repository.support.id.strategy.auto;

import java.util.Objects;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.xdev.spring.data.eclipse.store.repository.support.id.strategy.IdFinder;


public abstract class AbstractAutoIdFinder<ID> implements IdFinder<ID>
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractAutoIdFinder.class);
	final Supplier<ID> lastIdGetter;
	
	protected AbstractAutoIdFinder(final Supplier<ID> lastIdGetter)
	{
		this.lastIdGetter = Objects.requireNonNull(lastIdGetter);
		if(LOG.isDebugEnabled())
		{
			LOG.debug("New AutoIdFinder created. Starting ID: {}", this.lastIdGetter.get());
		}
	}
	
	@Override
	public ID findId()
	{
		final ID nextId = this.getNext(this.lastIdGetter.get());
		if(LOG.isDebugEnabled())
		{
			LOG.debug("New Id created: {}", nextId);
		}
		return nextId;
	}
	
	protected abstract ID getNext(ID oldId);
}
