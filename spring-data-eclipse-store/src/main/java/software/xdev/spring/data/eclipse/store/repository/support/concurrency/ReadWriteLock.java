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
package software.xdev.spring.data.eclipse.store.repository.support.concurrency;

/**
 * Facility to execute operations with read and write locks.
 * <p>
 * Non-reentrant read operations are not allowed until all write operations have been finished. Additionally, a write
 * operation can acquire the read lock, but not vice-versa.
 */
public interface ReadWriteLock
{
	/**
	 * Executes an operation protected by a read lock.
	 *
	 * @param <T>       the operation's return type
	 * @param operation the operation to execute
	 * @return the operation's result
	 */
	<T> T read(final ValueOperation<T> operation);
	
	/**
	 * Executes an operation protected by a read lock.
	 *
	 * @param operation the operation to execute
	 */
	void read(final VoidOperation operation);
	
	/**
	 * Executes an operation protected by a write lock.
	 *
	 * @param <T>       the operation's return type
	 * @param operation the operation to execute
	 * @return the operation's result
	 */
	<T> T write(final ValueOperation<T> operation);
	
	/**
	 * Executes an operation protected by a write lock.
	 *
	 * @param operation the operation to execute
	 */
	void write(final VoidOperation operation);
}
