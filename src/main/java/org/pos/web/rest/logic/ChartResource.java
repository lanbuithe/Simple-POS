package org.pos.web.rest.logic;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.pos.service.logic.OrderService;
import org.pos.util.DateTimePattern;
import org.pos.web.rest.dto.logic.PieChart;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing Chart.
 */
@RestController
@RequestMapping("/api/charts")
public class ChartResource {

    @Inject
    private OrderService orderService;
    
    /**
     * GET  /item -> Get sale item.
     */
    @RequestMapping(value = "/item",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PieChart>> getSaleItemByStatusCreatedDateBetween(@RequestParam(value = "status") String status,
    		@RequestParam(value = "from" , required = false) @DateTimeFormat(pattern = DateTimePattern.ISO_DATE) DateTime from, 
    		@RequestParam(value = "to", required = false) @DateTimeFormat(pattern = DateTimePattern.ISO_DATE) DateTime to) {
    	List<PieChart> saleItems = orderService.getSaleItemByStatusCreatedDateBetween(status, from, to);
        return new ResponseEntity<List<PieChart>>(saleItems, HttpStatus.OK);
    }    
}
