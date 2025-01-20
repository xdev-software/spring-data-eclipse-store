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
package software.xdev.spring.data.eclipse.store.helper;

import java.util.ArrayList;
import java.util.List;

import org.opentest4j.AssertionFailedError;

import software.xdev.spring.data.eclipse.store.repository.config.EclipseStoreClientConfiguration;


public final class TestUtil
{
	public static <T> List<T> iterableToList(final Iterable<T> iterable)
	{
		final List<T> list = new ArrayList<>();
		iterable.forEach(list::add);
		return list;
	}
	
	public static void restartDatastore(final EclipseStoreClientConfiguration configuration)
	{
		configuration.getStorageInstance().stop();
		// Storage starts automatically again, if the repo is accessed
	}
	
	public static void doBeforeAndAfterRestartOfDatastore(
		final EclipseStoreClientConfiguration configuration,
		final Runnable thingToDoBeforeAndAfterRestart)
	{
		try
		{
			thingToDoBeforeAndAfterRestart.run();
		}
		catch(final AssertionFailedError error)
		{
			throw new AssertionFailedError("Error before restart of storage", error);
		}
		restartDatastore(configuration);
		try
		{
			thingToDoBeforeAndAfterRestart.run();
		}
		catch(final AssertionFailedError error)
		{
			throw new AssertionFailedError("Error after restart of storage", error);
		}
	}
	
	private TestUtil()
	{
	}
}
