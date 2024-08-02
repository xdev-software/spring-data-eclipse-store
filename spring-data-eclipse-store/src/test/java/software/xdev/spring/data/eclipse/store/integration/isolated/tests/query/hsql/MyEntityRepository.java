package software.xdev.spring.data.eclipse.store.integration.isolated.tests.query.hsql;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.ListCrudRepository;

import software.xdev.spring.data.eclipse.store.repository.Query;


public interface MyEntityRepository extends ListCrudRepository<MyEntity, Long>
{
	// 1. Simple Select
	@Query("SELECT e FROM MyEntity e")
	List<MyEntity> findAllEntities();
	
	// 2. Select with a where clause
	@Query("SELECT e FROM MyEntity e WHERE e.name = ?1")
	List<MyEntity> findByName(String name);
	
	// 3. Select with multiple where clauses
	@Query("SELECT e FROM MyEntity e WHERE e.name = ?1 AND e.age > ?2")
	List<MyEntity> findByNameAndAgeGreaterThan(String name, int age);
	
	// 4. Select with order by
	@Query("SELECT e FROM MyEntity e ORDER BY e.age DESC")
	List<MyEntity> findAllOrderByAgeDesc();
	
	// 5. Select with limit
	@Query(value = "SELECT e FROM MyEntity e ORDER BY e.age DESC")
	List<MyEntity> findTop5ByOrderByAgeDesc();
	
	// 6. Select with distinct
	@Query("SELECT DISTINCT e.name FROM MyEntity e")
	List<String> findDistinctNames();
	
	// 7. Select with join
	@Query("SELECT e FROM MyEntity e JOIN e.otherEntity o WHERE o.id = ?1")
	List<MyEntity> findByOtherEntityId(Long otherEntityId);
	
	// 8. Select with group by
	@Query("SELECT e.name, COUNT(e) FROM MyEntity e GROUP BY e.name")
	List<Object[]> countByName();
	
	// 9. Select with having
	@Query("SELECT e.name, COUNT(e) FROM MyEntity e GROUP BY e.name HAVING COUNT(e) > ?1")
	List<Object[]> countByNameHavingMoreThan(long count);
	
	// 10. Select with subquery
	@Query("SELECT e FROM MyEntity e WHERE e.age = (SELECT MAX(e2.age) FROM MyEntity e2)")
	MyEntity findEntityWithMaxAge();
	
	// 11. Select with IN clause
	@Query("SELECT e FROM MyEntity e WHERE e.name IN ?1")
	List<MyEntity> findByNameIn(List<String> names);
	
	// 12. Select with LIKE clause
	@Query("SELECT e FROM MyEntity e WHERE e.name LIKE %?1%")
	List<MyEntity> findByNameContaining(String keyword);
	
	// 13. Select with native query
	@Query(value = "SELECT * FROM my_entity WHERE name = ?1")
	List<MyEntity> findByNameNative(String name);
	
	// 14. Select with date comparison
	@Query("SELECT e FROM MyEntity e WHERE e.creationDate > ?1")
	List<MyEntity> findByCreationDateAfter(LocalDate date);
	
	// 15. Select with between clause
	@Query("SELECT e FROM MyEntity e WHERE e.age BETWEEN ?1 AND ?2")
	List<MyEntity> findByAgeBetween(int startAge, int endAge);
	
	// 16. Select with boolean condition
	@Query("SELECT e FROM MyEntity e WHERE e.active = true")
	List<MyEntity> findAllActive();
	
	// 17. Select with is null condition
	@Query("SELECT e FROM MyEntity e WHERE e.otherEntity IS NULL")
	List<MyEntity> findWhereOtherEntityIsNull();
	
	// 18. Select with is not null condition
	@Query("SELECT e FROM MyEntity e WHERE e.otherEntity IS NOT NULL")
	List<MyEntity> findWhereOtherEntityIsNotNull();
	
	// TODO
	// 19. Select with a custom projection
	// @Query("SELECT new com.example.demo.dto.MyEntityDTO(e.name, e.age) FROM MyEntity e")
	// List<MyEntityDTO> findAllAsDTO();
	
	// 20. Select with function
	@Query("SELECT e FROM MyEntity e WHERE FUNCTION('YEAR', e.creationDate) = ?1")
	List<MyEntity> findByCreationYear(int year);
}
