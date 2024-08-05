package software.xdev.spring.data.eclipse.store.integration.isolated.tests.query.hsql;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.ListCrudRepository;

import software.xdev.spring.data.eclipse.store.repository.Query;


public interface MyEntityRepository extends ListCrudRepository<MyEntity, Long>
{
	// 1. Simple Select
	@Query("SELECT * FROM MyEntity")
	List<MyEntity> findAllEntities();
	
	// 2. Select with a where clause
	@Query("SELECT * FROM MyEntity WHERE name = ?1")
	List<MyEntity> findByName(String name);
	
	// 3. Select with multiple where clauses
	@Query(" SELECT * FROM MyEntity WHERE (name = ?1 AND age > ?2)")
	List<MyEntity> findByNameAndAgeGreaterThan(String name, int age);
	
	// 4. Select with order by
	@Query(" SELECT * FROM MyEntity ORDER BY age DESC")
	List<MyEntity> findAllOrderByAgeDesc();
	
	// 5. Select with limit
	@Query(value = " SELECT * FROM MyEntity ORDER BY age DESC LIMIT 2")
	List<MyEntity> findTop2ByOrderByAgeDesc();
	
	// 6. Select with distinct
	@Query("SELECT DISTINCT name FROM MyEntity")
	List<String> findDistinctNames();
	
	// 8. Select with group by
	@Query("SELECT name, COUNT(*) FROM MyEntity GROUP BY name")
	List<Object[]> countByName();
	
	// 9. Select with having
	@Query("SELECT name, COUNT(*) FROM MyEntity GROUP BY name HAVING COUNT(*) > ?1")
	List<Object[]> countByNameHavingMoreThan(long count);
	
	// 10. Select with subquery
	@Query("SELECT * FROM MyEntity WHERE age = (SELECT MAX(age) FROM MyEntity2)")
	MyEntity findEntityWithMaxAge();
	
	// 11. Select with IN clause
	@Query(" SELECT * FROM MyEntity WHERE name IN ?1")
	List<MyEntity> findByNameIn(List<String> names);
	
	// 12. Select with LIKE clause
	@Query(" SELECT * FROM MyEntity WHERE 'name' LIKE '%?1%'")
	List<MyEntity> findByNameContaining(String keyword);
	
	// 13. Select with native query
	@Query(value = "SELECT * FROM my_entity WHERE name = ?1")
	List<MyEntity> findByNameNative(String name);
	
	// 14. Select with date comparison
	@Query(" SELECT * FROM MyEntity WHERE creationDate > ?1")
	List<MyEntity> findByCreationDateAfter(LocalDate date);
	
	// 15. Select with between clause
	@Query(" SELECT * FROM MyEntity WHERE age BETWEEN ?1 AND ?2")
	List<MyEntity> findByAgeBetween(int startAge, int endAge);
	
	// 16. Select with boolean condition
	@Query(" SELECT * FROM MyEntity WHERE active = true")
	List<MyEntity> findAllActive();
	
	// 17. Select with is null condition
	@Query(" SELECT * FROM MyEntity WHERE otherEntity IS NULL")
	List<MyEntity> findWhereOtherEntityIsNull();
	
	// 18. Select with is not null condition
	@Query(" SELECT * FROM MyEntity WHERE otherEntity IS NOT NULL")
	List<MyEntity> findWhereOtherEntityIsNotNull();
}
