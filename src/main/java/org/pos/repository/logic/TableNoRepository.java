package org.pos.repository.logic;

import org.pos.domain.logic.TableNo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the TableNo entity.
 */
public interface TableNoRepository extends JpaRepository<TableNo,Long> {

}
