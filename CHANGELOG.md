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
 
