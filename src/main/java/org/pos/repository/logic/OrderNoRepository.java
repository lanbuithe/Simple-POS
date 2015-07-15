package org.pos.repository.logic;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.pos.domain.logic.OrderNo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data JPA repository for the OrderNo entity.
 */
public interface OrderNoRepository extends JpaRepository<OrderNo,Long> {

	@EntityGraph(value = "orderWithDetails", type = EntityGraphType.FETCH)
	public Page<OrderNo> findByStatusIs(String status, Pageable pageable);
	
	public Page<OrderNo> findByStatusIsAndCreatedDateBetween(String status, DateTime from, DateTime to, Pageable pageable);
	
	public Page<OrderNo> findByStatusIsAndCreatedDateLessThanEqual(String status, DateTime createdDate, Pageable pageable);
	
	public Page<OrderNo> findByStatusIsAndCreatedDateGreaterThanEqual(String status, DateTime createdDate, Pageable pageable);
	
	@Query(value = "select sum(a.amount) from OrderNo a where a.status = ?1 and a.createdDate between ?2 and ?3")
    public BigDecimal getSumAmountByStatusCreatedDateBetween(String status, DateTime from, DateTime to);
	
	@Query(value = "select sum(a.amount) from OrderNo a where a.status = ?1 and a.createdDate >= ?2")
    public BigDecimal getSumAmountByStatusCreatedDateAfterEqual(String status, DateTime createdDate);
	
	@Query(value = "select sum(a.amount) from OrderNo a where a.status = ?1 and a.createdDate <= ?2")
    public BigDecimal getSumAmountByStatusCreatedDateBeforeEqual(String status, DateTime createdDate);
	
	@EntityGraph(value = "orderWithDetails", type = EntityGraphType.FETCH)
	public OrderNo findById(Long id);	
}
