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
package software.xdev.spring.data.eclipse.store.repository.interfaces.lazy;

import org.eclipse.serializer.reference.Lazy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;


@SuppressWarnings("java:S119")
@NoRepositoryBean
public interface LazyEclipseStoreCrudRepository<T, ID> extends CrudRepository<Lazy<T>, ID>
{
	/**
	 * @inheritDoc <b>Caution with referenced objects!</b><br/> If you are deleting an object that is referenced by
	 * another object, the behavior of this function may differ from what you are used to!
	 * <p>
	 * JPA would throw a {@code JdbcSQLIntegrityConstraintViolationException} but it would be very expensive to search
	 * the referencedObject in the complete object tree and throw the exception.
	 * <p>
	 * That is why this library simply removes the element from the repository, but if it is still referenced by
	 * another
	 * object, this <b>reference is still working and pointing to the object</b>. That means that in fact this the
	 * object to remove could very well stay in the storage if it is referenced.
	 * </p>
	 */
	@Override
	void deleteById(ID id);
	
	/**
	 * @inheritDoc <b>Caution with referenced objects!</b><br/> If you are deleting an object that is referenced by
	 * another object, the behavior of this function may differ from what you are used to!
	 * <p>
	 * For more information see {@link #deleteById(Object)}
	 * </p>
	 */
	void deleteEntity(T entity);
	
	/**
	 * @inheritDoc <b>Caution with referenced objects!</b><br/> If you are deleting an object that is referenced by
	 * another object, the behavior of this function may differ from what you are used to!
	 * <p>
	 * For more information see {@link #deleteById(Object)}
	 * </p>
	 */
	@Override
	void delete(Lazy<T> entity);
	
	/**
	 * @inheritDoc <b>Caution with referenced objects!</b><br/> If you are deleting an object that is referenced by
	 * another object, the behavior of this function may differ from what you are used to!
	 * <p>
	 * For more information see {@link #deleteById(Object)}
	 * </p>
	 */
	@Override
	void deleteAllById(Iterable<? extends ID> ids);
	
	/**
	 * @inheritDoc <b>Caution with referenced objects!</b><br/> If you are deleting an object that is referenced by
	 * another object, the behavior of this function may differ from what you are used to!
	 * <p>
	 * For more information see {@link #deleteById(Object)}
	 * </p>
	 */
	@Override
	void deleteAll(Iterable<? extends Lazy<T>> entities);
	
	/**
	 * @inheritDoc <b>Caution with referenced objects!</b><br/> If you are deleting an object that is referenced by
	 * another object, the behavior of this function may differ from what you are used to!
	 * <p>
	 * For more information see {@link #deleteById(Object)}
	 * </p>
	 */
	void deleteAllEntities(Iterable<? extends T> entities);
	
	/**
	 * @inheritDoc <b>Caution with referenced objects!</b><br/> If you are deleting an object that is referenced by
	 * another object, the behavior of this function may differ from what you are used to!
	 * <p>
	 * For more information see {@link #deleteById(Object)}
	 * </p>
	 */
	@Override
	void deleteAll();
}
