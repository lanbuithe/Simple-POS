package org.pos.web.rest.logic;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.pos.domain.logic.TableNo;
import org.pos.repository.logic.TableNoRepository;
import org.pos.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing TableNo.
 */
@RestController
@RequestMapping("/api")
public class TableNoResource {

    private final Logger log = LoggerFactory.getLogger(TableNoResource.class);

    @Inject
    private TableNoRepository tableNoRepository;

    /**
     * POST  /tableNos -> Create a new tableNo.
     */
    @RequestMapping(value = "/tableNos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody TableNo tableNo) throws URISyntaxException {
        log.debug("REST request to save TableNo : {}", tableNo);
        if (tableNo.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new tableNo cannot already have an ID").build();
        }
        tableNoRepository.save(tableNo);
        return ResponseEntity.created(new URI("/api/tableNos/" + tableNo.getId())).build();
    }

    /**
     * PUT  /tableNos -> Updates an existing tableNo.
     */
    @RequestMapping(value = "/tableNos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody TableNo tableNo) throws URISyntaxException {
        log.debug("REST request to update TableNo : {}", tableNo);
        if (tableNo.getId() == null) {
            return create(tableNo);
        }
        tableNoRepository.save(tableNo);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /tableNos -> get all the tableNos.
     */
    @RequestMapping(value = "/tableNos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TableNo>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<TableNo> page = tableNoRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tableNos", offset, limit);
        return new ResponseEntity<List<TableNo>>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /tableNos/:id -> get the "id" tableNo.
     */
    @RequestMapping(value = "/tableNos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TableNo> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get TableNo : {}", id);
        TableNo tableNo = tableNoRepository.findOne(id);
        if (tableNo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(tableNo, HttpStatus.OK);
    }

    /**
     * DELETE  /tableNos/:id -> delete the "id" tableNo.
     */
    @RequestMapping(value = "/tableNos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete TableNo : {}", id);
        tableNoRepository.delete(id);
    }
    
    /**
     * GET  /tableNos/order
     */
    @RequestMapping(value = "/tableNos/order",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<TableNo> findByOrderStatus(@RequestParam(value="status") String status) {
        log.debug("REST request to get TableNo by order status: {}", status);
        return tableNoRepository.findByOrderStatus(status); 	
    }
}
