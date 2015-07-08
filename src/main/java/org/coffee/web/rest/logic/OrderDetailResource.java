package org.coffee.web.rest.logic;

import com.codahale.metrics.annotation.Timed;

import org.coffee.domain.logic.OrderDetail;
import org.coffee.repository.logic.OrderDetailRepository;
import org.coffee.web.rest.util.PaginationUtil;
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
 * REST controller for managing OrderDetail.
 */
@RestController
@RequestMapping("/api")
public class OrderDetailResource {

    private final Logger log = LoggerFactory.getLogger(OrderDetailResource.class);

    @Inject
    private OrderDetailRepository orderDetailRepository;

    /**
     * POST  /orderDetails -> Create a new orderDetail.
     */
    @RequestMapping(value = "/orderDetails",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody OrderDetail orderDetail) throws URISyntaxException {
        log.debug("REST request to save OrderDetail : {}", orderDetail);
        if (orderDetail.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new orderDetail cannot already have an ID").build();
        }
        orderDetailRepository.save(orderDetail);
        return ResponseEntity.created(new URI("/api/orderDetails/" + orderDetail.getId())).build();
    }

    /**
     * PUT  /orderDetails -> Updates an existing orderDetail.
     */
    @RequestMapping(value = "/orderDetails",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody OrderDetail orderDetail) throws URISyntaxException {
        log.debug("REST request to update OrderDetail : {}", orderDetail);
        if (orderDetail.getId() == null) {
            return create(orderDetail);
        }
        orderDetailRepository.save(orderDetail);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /orderDetails -> get all the orderDetails.
     */
    @RequestMapping(value = "/orderDetails",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<OrderDetail>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<OrderDetail> page = orderDetailRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/orderDetails", offset, limit);
        return new ResponseEntity<List<OrderDetail>>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /orderDetails/:id -> get the "id" orderDetail.
     */
    @RequestMapping(value = "/orderDetails/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OrderDetail> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get OrderDetail : {}", id);
        OrderDetail orderDetail = orderDetailRepository.findOne(id);
        if (orderDetail == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(orderDetail, HttpStatus.OK);
    }

    /**
     * DELETE  /orderDetails/:id -> delete the "id" orderDetail.
     */
    @RequestMapping(value = "/orderDetails/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete OrderDetail : {}", id);
        orderDetailRepository.delete(id);
    }
}
