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
package software.xdev.spring.data.eclipse.store.importer;

import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManagerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import software.xdev.spring.data.eclipse.store.repository.EclipseStoreStorage;
import software.xdev.spring.data.eclipse.store.repository.support.SimpleEclipseStoreRepository;


@Component
public class EclipseStoreDataImporterComponent
{
	private final EclipseStoreDataImporter importer;
	private final ApplicationContext applicationContext;
	
	public EclipseStoreDataImporterComponent(
		final EclipseStoreStorage eclipseStoreStorage,
		final ApplicationContext applicationContext)
	{
		this.importer = new EclipseStoreDataImporter(eclipseStoreStorage);
		this.applicationContext = applicationContext;
	}
	
	public List<SimpleEclipseStoreRepository<?,?>> importData()
	{
		final Map<String, EntityManagerFactory> beansOfEms =
			this.applicationContext.getBeansOfType(EntityManagerFactory.class);
		return this.importer.importData(beansOfEms.values());
	}
}
