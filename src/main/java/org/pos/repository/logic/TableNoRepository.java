package org.pos.repository.logic;

import java.util.List;

import org.pos.domain.logic.TableNo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data JPA repository for the TableNo entity.
 */
public interface TableNoRepository extends JpaRepository<TableNo,Long> {
	
	@Query("select a from TableNo a join a.orders b where b.status = ?1")
	public List<TableNo> findByOrderStatus(String status);

}
