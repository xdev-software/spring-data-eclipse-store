/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package software.xdev.spring.data.eclipse.store.demo.complex;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class ComplexDemoApplication implements CommandLineRunner
{
	private final OwnerService ownerService;
	private final VetService vetService;
	
	@Autowired
	public ComplexDemoApplication(
		final OwnerService ownerService,
		final VetService vetService
	)
	{
		this.ownerService = ownerService;
		this.vetService = vetService;
	}
	
	public static void main(final String[] args)
	{
		final ConfigurableApplicationContext run = SpringApplication.run(ComplexDemoApplication.class, args);
		run.close();
	}
	
	@Override
	public void run(final String... args)
	{
		this.vetCalls();
		this.ownerCalls();
	}
	
	/**
	 * Some calls are transactional (delete and create) and some are not (log).
	 */
	private void ownerCalls()
	{
		this.ownerService.logOwners();
		this.ownerService.deleteAll();
		this.ownerService.logOwners();
		this.ownerService.createNewOwnerAndVisit();
		this.ownerService.logOwnersAndVisits();
	}
	
	/**
	 * Each of these calls are one transaction.
	 */
	private void vetCalls()
	{
		this.vetService.logVetEntries();
		this.vetService.deleteAll();
		this.vetService.logVetEntries();
		this.vetService.saveNewEntries();
		this.vetService.logVetEntries();
	}
}
