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
package software.xdev.spring.data.eclipse.store.integration.isolated.tests.real.life.examples.nonlazy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import software.xdev.spring.data.eclipse.store.integration.isolated.IsolatedTestAnnotations;
import software.xdev.spring.data.eclipse.store.integration.isolated.tests.real.life.examples.RealLifeExamplesTest;


@IsolatedTestAnnotations
@ContextConfiguration(classes = {RealLifeExamplesNonLazyTestConfiguration.class})
class RealLifeExamplesNonLazyTest extends RealLifeExamplesTest
{
	@Autowired
	public RealLifeExamplesNonLazyTest(
		final InvoiceNonLazyRepository invoiceRepository,
		final PositionNonLazyRepository positionRepository,
		final RealLifeExamplesNonLazyTestConfiguration configuration)
	{
		super(invoiceRepository, positionRepository, configuration);
	}
}
