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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.version;

import jakarta.persistence.Version;


public class VersionedEntityWithIntegerAndVersionedChild implements VersionedEntity<Integer>
{
	@Version
	private int version;
	private String name;
	private VersionedChildEntityWithLong child;
	
	public VersionedEntityWithIntegerAndVersionedChild(final String name, final VersionedChildEntityWithLong child)
	{
		this.name = name;
		this.child = child;
	}
	
	@Override
	public Integer getVersion()
	{
		return this.version;
	}
	
	@Override
	public String getName()
	{
		return this.name;
	}
	
	public void setVersion(final int version)
	{
		this.version = version;
	}
	
	@Override
	public void setName(final String name)
	{
		this.name = name;
	}
	
	public VersionedChildEntityWithLong getChild()
	{
		return this.child;
	}
	
	public void setChild(final VersionedChildEntityWithLong child)
	{
		this.child = child;
	}
}
