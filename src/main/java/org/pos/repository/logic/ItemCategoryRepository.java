package org.pos.repository.logic;

import org.pos.domain.logic.ItemCategory;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ItemCategory entity.
 */
public interface ItemCategoryRepository extends JpaRepository<ItemCategory,Long> {

}
