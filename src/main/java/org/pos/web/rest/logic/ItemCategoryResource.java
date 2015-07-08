package org.pos.web.rest.logic;

import com.codahale.metrics.annotation.Timed;

import org.pos.domain.logic.ItemCategory;
import org.pos.repository.logic.ItemCategoryRepository;
import org.pos.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * REST controller for managing ItemCategory.
 */
@RestController
@RequestMapping("/api")
public class ItemCategoryResource {

    private final Logger log = LoggerFactory.getLogger(ItemCategoryResource.class);

    @Inject
    private ItemCategoryRepository itemCategoryRepository;

    /**
     * POST  /itemCategorys -> Create a new itemCategory.
     */
    @RequestMapping(value = "/itemCategorys",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody ItemCategory itemCategory) throws URISyntaxException {
        log.debug("REST request to save ItemCategory : {}", itemCategory);
        if (itemCategory.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new itemCategory cannot already have an ID").build();
        }
        itemCategoryRepository.save(itemCategory);
        return ResponseEntity.created(new URI("/api/itemCategorys/" + itemCategory.getId())).build();
    }

    /**
     * PUT  /itemCategorys -> Updates an existing itemCategory.
     */
    @RequestMapping(value = "/itemCategorys",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody ItemCategory itemCategory) throws URISyntaxException {
        log.debug("REST request to update ItemCategory : {}", itemCategory);
        if (itemCategory.getId() == null) {
            return create(itemCategory);
        }
        itemCategoryRepository.save(itemCategory);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /itemCategorys -> get all the itemCategorys.
     */
    @RequestMapping(value = "/itemCategorys",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ItemCategory>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<ItemCategory> page = itemCategoryRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/itemCategorys", offset, limit);
        return new ResponseEntity<List<ItemCategory>>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /itemCategorys/:id -> get the "id" itemCategory.
     */
    @RequestMapping(value = "/itemCategorys/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ItemCategory> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get ItemCategory : {}", id);
        ItemCategory itemCategory = itemCategoryRepository.findOne(id);
        if (itemCategory == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(itemCategory, HttpStatus.OK);
    }

    /**
     * DELETE  /itemCategorys/:id -> delete the "id" itemCategory.
     */
    @RequestMapping(value = "/itemCategorys/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete ItemCategory : {}", id);
        itemCategoryRepository.delete(id);
    }
}
