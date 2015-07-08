package org.coffee.repository.logic;

import org.coffee.domain.logic.OrderDetail;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the OrderDetail entity.
 */
public interface OrderDetailRepository extends JpaRepository<OrderDetail,Long> {

}
