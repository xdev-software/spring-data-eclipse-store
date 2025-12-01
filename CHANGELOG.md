# 2.5.3

* Updated org.springframework.boot.version to v3.5.3
* Updated EclipseStore to v2.1.3

# 2.5.2
* Migrated deployment to _Sonatype Maven Central Portal_ [#155](https://github.com/xdev-software/standard-maven-template/issues/155)
* Updated dependencies

# 2.5.1

* Simplified configuration injection in ``EclipseStoreRepositoryFactoryBean``.
* Updated EclipseStore to v2.1.1
* Updated org.springframework.boot.version to v3.4.2

# 2.5.0

* Updated org.springframework.boot.version to v3.4.1
* Added support for the [micro-migration-Framework](https://github.com/xdev-software/micro-migration)

# 2.4.1

* Updated EclipseStore to v2.1.0
* Added EclipseStore-Rest-API to tests (storage-restservice-springboot)

# 2.4.0

* Updated org.springframework.boot.version to v3.4.0
* Updated EclipseStore to v2.0.0
* Implemented Lazy Repositories with ``LazyEclipseStoreRepository``

# 2.3.1

* Auto-Fix problems with adding ids to entities with existing data store.

# 2.3.0

* Add support for shutting down the storage during application shutdown
  * By default, only enabled when Spring DevTools are active
  * This should fix "StorageExceptionInitialization: Active storage for ... already exists" errors during DevTools restart
* Added [Jakarta Bean Validation Constraints](https://jakarta.ee/learn/docs/jakartaee-tutorial/current/beanvalidation/bean-validation/bean-validation.html#_using_jakarta_bean_validation_constraints) with Hibernate validator for entities.

# 2.2.2

* Fixed NPE in EclipseSerializerRegisteringCopier

# 2.2.1

* Fixed release version

# 2.2.0

* Fixed issue with not found migration script (for v2.X)
* Updated org.springframework.boot.version to v3.3.4

# 2.1.0

* Implemented auto-id-generation for UUIDs.
* Implemented composite primary keys.
* Keyword "ignoreCase" now available for queries.
* Implemented ``@Query`` annotation with simple SQL-Selects

# 2.0.1

* Fix for Issue [#131](https://github.com/xdev-software/spring-data-eclipse-store/issues/131)

# 2.0.0

* Restructured root to improve performance with IDs in entities
* Implemented auto migration for older version (<2.0.0).
  Added [XDEV MicroMigration](https://github.com/xdev-software/micro-migration) as dependency.
* Updated EclipseStore version to 1.4.0
* Updated Spring to version 3.3.2

# 1.0.10

* Optimistic locking with @Version now possible

# 1.0.9

* Inherited entities with repositories are now realized by reading (finding coherent repositories) and not by writing
* Multiple restarts of the storage at initial startup is now fixed

# 1.0.8

* Entities with same ID are replaced on saved and not added
* Updated Spring to version 3.3.1

# 1.0.7

* QueryByExample now possible
* Performance optimizations
* It's now possible to use multiple repositories with the same class/entity

# 1.0.6

* Fixed problem with missing configuration

# 1.0.5

* Added support for transactions

# 1.0.4

* Added possibility to use multiple storages
* Added Lazy support

# 1.0.3

* Added the EclipseStoreDataImporter to import data from JPA repositories.
* Updated EclipseStore to version 1.2.0
* Updated Spring to version 3.2.3

# 1.0.2

* Added the EclipseStoreCustomRepository which has no methods defined at all.
* EclipseStoreRepository extends the Crud- and PagingAndSorting-Repository (just like the
  org.springframework.data.jpa.repository.JpaRepository).

# 1.0.1

* Fix for NullPointerException when storing an entity with Auto-ID and no previous action on the database.
* Provide multiple Repository Interfaces, not only EclipseStoreRepository (e.g. EclipseStoreCrudRepository).

# 1.0.0

* Initial release
