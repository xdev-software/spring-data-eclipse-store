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
package software.xdev.spring.data.eclipse.store.repository;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.data.annotation.QueryAnnotation;


/**
 * Currently only exists to write a warning at runtime.
 * <p>
 * The library should be as compatible as possible with Spring JPA.
 * A <code>@Query</code>-Annotation is often used in these projects.
 * Keeping these annotations existing in code would cause an error. With this class the user can use the
 * annotation as before except with a different import. While running the application a warning is shown to
 * indicate that this is something to look after. It doesn't cause any problems, but it also doesn't do what
 * the user is used to in Spring JPA. This annotation does effectively nothing except write the warning message.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
@QueryAnnotation
public @interface Query
{
	@SuppressWarnings("unused") String value() default "";
}
