package org.pos.repository.logic;

import java.util.List;

import org.joda.time.DateTime;
import org.pos.domain.logic.OrderDetail;
import org.pos.web.rest.dto.logic.PieChart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data JPA repository for the OrderDetail entity.
 */
public interface OrderDetailRepository extends JpaRepository<OrderDetail,Long> {
	
	@Query(value = "select new org.pos.web.rest.dto.logic.PieChart(a.itemName, sum(a.amount)) from OrderDetail a where a.orderNo.status = ?1 and a.orderNo.createdDate between ?2 and ?3 group by a.itemName")
    public List<PieChart> getSaleItemByStatusCreatedDateBetween(String status, DateTime from, DateTime to);
}
