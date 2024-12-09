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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.migration;

import static org.eclipse.store.storage.embedded.types.EmbeddedStorage.Foundation;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.eclipse.store.storage.types.Storage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.FileSystemUtils;

import software.xdev.micromigration.version.MigrationVersion;
import software.xdev.spring.data.eclipse.store.helper.TestData;
import software.xdev.spring.data.eclipse.store.helper.TestUtil;
import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;
import software.xdev.spring.data.eclipse.store.repository.Root;
import software.xdev.spring.data.eclipse.store.repository.root.RootDataV2;
import software.xdev.spring.data.eclipse.store.repository.root.VersionedRoot;


@SuppressWarnings({"deprecation", "checkstyle:MethodName"})
@IsolatedTestAnnotations
@ContextConfiguration(classes = {MigrationTestConfiguration.class})
class MigrationTest
{
	public static final User TEST_USER = new User(TestData.FIRST_NAME, BigDecimal.ONE);
	
	private final MigrationTestConfiguration configuration;
	private final UserRepository userRepository;
	
	@Autowired
	public MigrationTest(final MigrationTestConfiguration configuration, final UserRepository userRepository)
	{
		this.configuration = configuration;
		this.userRepository = userRepository;
	}
	
	@Test
	void simpleMigrateFromV0_0_0ToNewest() throws IOException
	{
		this.initV1_0_0Data();
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<User> foundUsers = this.userRepository.findAll();
				Assertions.assertEquals(1, foundUsers.size());
				Assertions.assertEquals(TEST_USER, foundUsers.get(0));
			}
		);
	}
	
	@Test
	void simpleMigrateFromV2_0_0ToNewest() throws IOException
	{
		this.initV2_0_0Data();
		TestUtil.doBeforeAndAfterRestartOfDatastore(
			this.configuration,
			() -> {
				final List<User> foundUsers = this.userRepository.findAll();
				Assertions.assertEquals(1, foundUsers.size());
				Assertions.assertEquals(TEST_USER, foundUsers.get(0));
			}
		);
	}
	
	private void initV1_0_0Data() throws IOException
	{
		// Delete already created data
		this.configuration.getStorageInstance().stop();
		FileSystemUtils.deleteRecursively(Path.of(this.configuration.getStorageDirectory()));
		
		// Init old data
		try(final EmbeddedStorageManager storageManager =
			Foundation(Storage.Configuration(Storage.FileProvider(Path.of(this.configuration.getStorageDirectory()))))
				.start())
		{
			final Root oldRoot = new Root();
			oldRoot.createNewEntityList(User.class);
			oldRoot.getEntityList(User.class).add(TEST_USER);
			storageManager.setRoot(oldRoot);
			storageManager.storeRoot();
		}
	}
	
	private void initV2_0_0Data() throws IOException
	{
		// Delete already created data
		this.configuration.getStorageInstance().stop();
		FileSystemUtils.deleteRecursively(Path.of(this.configuration.getStorageDirectory()));
		
		// Init old data
		try(final EmbeddedStorageManager storageManager =
			Foundation(Storage.Configuration(Storage.FileProvider(Path.of(this.configuration.getStorageDirectory()))))
				.start())
		{
			final VersionedRoot oldRoot = new VersionedRoot();
			oldRoot.setRootDataV2(new RootDataV2());
			oldRoot.getRootDataV2().createNewEntityData(User.class, e -> null);
			oldRoot.getRootDataV2().getEntityData(User.class).ensureEntityAndReturnObjectsToStore(TEST_USER);
			oldRoot.setVersion(new MigrationVersion(2, 0, 0));
			storageManager.setRoot(oldRoot);
			storageManager.storeRoot();
		}
	}
}
