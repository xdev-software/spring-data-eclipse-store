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
package software.xdev.spring.data.eclipse.store.repository.support.copier.copier;

import org.eclipse.serializer.persistence.binary.types.Binary;
import org.eclipse.serializer.persistence.types.PersistenceManager;

import software.xdev.spring.data.eclipse.store.repository.SupportedChecker;
import software.xdev.spring.data.eclipse.store.repository.WorkingCopyRegistry;
import software.xdev.spring.data.eclipse.store.repository.lazy.SuperLazyBinaryHandler;


/**
 * This class registers storage instances and copy them for working copies. Utilizes
 * {@link EclipseSerializerRegisteringCopier}.
 */
public class RegisteringStorageToWorkingCopyCopier extends AbstractRegisteringCopier
{
	public RegisteringStorageToWorkingCopyCopier(
		final WorkingCopyRegistry registry,
		final SupportedChecker supportedChecker)
	{
		super(
			supportedChecker,
			(workingCopy, objectToStore) -> registry.register(workingCopy, objectToStore)
		);
	}
	
	@Override
	protected PersistenceManager<Binary> createPersistenceManager()
	{
		return this.createSerializerFoundation()
			.registerCustomTypeHandlers(SuperLazyBinaryHandler.New())
			.createPersistenceManager();
	}
}
