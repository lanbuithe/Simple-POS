package org.pos.repository.logic;

import java.util.List;

import org.pos.domain.logic.Item;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Item entity.
 */
public interface ItemRepository extends JpaRepository<Item,Long> {

	public List<Item> findByCategoryId(Long itemCategoryId);
}
