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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.simple.nonlazy;

import software.xdev.spring.data.eclipse.store.integration.isolated.tests.simple.model.Owner;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.simple.model.OwnerRepository;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreCrudRepository;


public interface OwnerNonLazyRepository extends EclipseStoreCrudRepository<Owner, String>, OwnerRepository
{
}
