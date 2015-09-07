package org.pos.web.rest.logic;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.pos.domain.logic.OrderDetail;
import org.pos.domain.logic.OrderNo;
import org.pos.repository.logic.OrderNoRepository;
import org.pos.service.logic.OrderService;
import org.pos.util.DateTimePattern;
import org.pos.util.OrderStatus;
import org.pos.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
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
 * REST controller for managing OrderNo.
 */
@RestController
@RequestMapping("/api")
public class OrderNoResource {

    private final Logger log = LoggerFactory.getLogger(OrderNoResource.class);

    @Inject
    private OrderNoRepository orderNoRepository;
    
    @Inject
    private OrderService orderService;

    /**
     * POST  /orderNos -> Create a new orderNo.
     */
    @RequestMapping(value = "/orderNos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody OrderNo orderNo) throws URISyntaxException {
        log.debug("REST request to save OrderNo : {}", orderNo);
        if (orderNo.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new orderNo cannot already have an ID").build();
        }
        updateRelationship(orderNo);
        orderNoRepository.save(orderNo);
        return ResponseEntity.created(new URI("/api/orderNos/" + orderNo.getId())).build();
    }

    /**
     * PUT  /orderNos -> Updates an existing orderNo.
     */
    @RequestMapping(value = "/orderNos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody OrderNo orderNo) throws URISyntaxException {
        log.debug("REST request to update OrderNo : {}", orderNo);
        if (orderNo.getId() == null) {
            return create(orderNo);
        }
        updateRelationship(orderNo);
        orderNoRepository.save(orderNo);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 
     * @param orderNo
     */
    private void updateRelationship(OrderNo orderNo) {
    	if (null != orderNo && CollectionUtils.isNotEmpty(orderNo.getDetails())) {
    		for (OrderDetail orderDetail : orderNo.getDetails()) {
    			log.debug("update OrderDetail : {}", orderDetail);
    			orderDetail.setOrderNo(orderNo);
			}
    	}
    }

    /**
     * GET  /orderNos -> get all the orderNos.
     */
    @RequestMapping(value = "/orderNos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<OrderNo>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<OrderNo> page = orderNoRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/orderNos", offset, limit);
        return new ResponseEntity<List<OrderNo>>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /orderNos/:id -> get the "id" orderNo.
     */
    @RequestMapping(value = "/orderNos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OrderNo> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get OrderNo : {}", id);
        OrderNo orderNo = orderNoRepository.findOne(id);
        if (orderNo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(orderNo, HttpStatus.OK);
    }

    /**
     * DELETE  /orderNos/:id -> delete the "id" orderNo.
     */
    @RequestMapping(value = "/orderNos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete OrderNo : {}", id);
        orderNoRepository.delete(id);
    }
    
    /**
     * POST  /orders -> Create a new order.
     */
    @RequestMapping(value = "/orders",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OrderNo> createOrder(@Valid @RequestBody OrderNo orderNo) throws URISyntaxException {
        log.debug("REST request to save OrderNo : {}", orderNo);
        if (orderNo.getId() != null) {
        	HttpHeaders headers = new HttpHeaders();
        	headers.add("Failure", "A new orderNo cannot already have an ID");
            return new ResponseEntity<OrderNo>(orderNo, headers, HttpStatus.EXPECTATION_FAILED);
        }
        updateRelationship(orderNo);
        orderNoRepository.save(orderNo);
        if (OrderStatus.PAYMENT.name().equalsIgnoreCase(orderNo.getStatus())) {
        	orderService.getSaleChart(OrderStatus.PAYMENT.name(), null, null);
        }
        return new ResponseEntity<OrderNo>(orderNo, HttpStatus.CREATED);
    }
    
    /**
     * PUT  /orders -> Updates an existing order.
     */
    @RequestMapping(value = "/orders",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> updateOrder(@Valid @RequestBody OrderNo orderNo) throws URISyntaxException {
        log.debug("REST request to update OrderNo : {}", orderNo);
        if (orderNo.getId() == null) {
            return create(orderNo);
        }
        updateRelationship(orderNo);
        orderNoRepository.save(orderNo);
        if (OrderStatus.PAYMENT.name().equalsIgnoreCase(orderNo.getStatus())) {
        	orderService.getSaleChart(OrderStatus.PAYMENT.name(), null, null);
        }
        return ResponseEntity.ok().build();
    }
    
    /**
     * GET  /orders -> get all the orders by table id, status, created date.
     */
    @RequestMapping(value = "/orders",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<OrderNo>> getByTableIdStatusCreatedDate(@RequestParam(value = "page" , required = false) Integer offset,
    		@RequestParam(value = "per_page", required = false) Integer limit, 
    		@RequestParam(value = "tableId", required = false) Long tableId,
    		@RequestParam(value = "status") String status,
    		@RequestParam(value = "from" , required = false) @DateTimeFormat(pattern = DateTimePattern.ISO_DATE_TIME) DateTime from, 
    		@RequestParam(value = "to", required = false) @DateTimeFormat(pattern = DateTimePattern.ISO_DATE_TIME) DateTime to)
        throws URISyntaxException {
        Page<OrderNo> page = orderService.getByTableIdStatusCreatedDate(tableId, status, from, to, PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/orders", offset, limit);
        return new ResponseEntity<List<OrderNo>>(page.getContent(), headers, HttpStatus.OK);
    }   
    
    /**
     * GET  /orders/:id -> get the "id" orderNo.
     */
    @RequestMapping(value = "/orders/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OrderNo> getById(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get OrderNo to print : {}", id);
        OrderNo orderNo = orderNoRepository.findById(id);
        if (orderNo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(orderNo, HttpStatus.OK);
    }
    
    /**
     * GET  /orders/receivable-amount -> get receivable amount orders by status, created date.
     */
    @RequestMapping(value = "/orders/receivable-amount",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<BigDecimal> getSumReceivableAmountByStatusCreatedDate(@RequestParam(value = "status") String status, 
    		@RequestParam(value = "from" , required = false) @DateTimeFormat(pattern = DateTimePattern.ISO_DATE_TIME) DateTime from, 
    		@RequestParam(value = "to", required = false) @DateTimeFormat(pattern = DateTimePattern.ISO_DATE_TIME) DateTime to) {
    	BigDecimal revenueAmount = orderService.getSumReceivableAmountByStatusCreatedDate(status, from, to);
        return new ResponseEntity<BigDecimal>(revenueAmount, HttpStatus.OK);
    }     
    
    /**
     * POST  /orders/move -> Update items of order.
     */
    @RequestMapping(value = "/orders/move",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> moveItem(@RequestBody List<OrderNo> orderNos) {
        log.debug("REST request to move items of order");
        boolean result = orderService.moveItem(orderNos);
        return new ResponseEntity<Boolean>(result, HttpStatus.OK);
    }    
    
}
