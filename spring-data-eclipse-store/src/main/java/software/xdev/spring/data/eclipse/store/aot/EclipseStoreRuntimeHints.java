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
package software.xdev.spring.data.eclipse.store.aot;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.lang.Nullable;

import jakarta.annotation.Nonnull;


/**
 * Makes registration of types for reflection ({@link org.springframework.aot.hint.RuntimeHints} or proxies possible.
 * <p>
 * Is not needed right now, but is left here for future use.
 *
 * @see <a href="https://docs.spring.io/spring-framework/reference/core/aot.html#aot.hints">https://docs.spring
 * .io/spring-framework/reference/core/aot.html#aot.hints</a>
 */
class EclipseStoreRuntimeHints implements RuntimeHintsRegistrar
{
	public EclipseStoreRuntimeHints()
	{
		// Is not needed right now, but is left here for future use.
	}
	
	@Override
	@Nonnull
	public void registerHints(@Nonnull final RuntimeHints hints, @Nullable final ClassLoader classLoader)
	{
		// Is not needed right now, but is left here for future use.
	}
}
