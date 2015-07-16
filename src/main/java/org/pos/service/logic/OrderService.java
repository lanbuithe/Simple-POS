package org.pos.service.logic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.pos.domain.logic.OrderNo;
import org.pos.repository.logic.OrderDetailRepository;
import org.pos.repository.logic.OrderNoRepository;
import org.pos.util.JodaTimeUtil;
import org.pos.web.rest.dto.logic.LineChart;
import org.pos.web.rest.dto.logic.PieChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

    private final Logger log = LoggerFactory.getLogger(OrderService.class);
    
    @Inject
    private OrderNoRepository orderNoRepository;
    
    @Inject
    private OrderDetailRepository orderDetailRepository;
    
    public Page<OrderNo> getByStatusCreatedDate(String status, DateTime from, DateTime to, Pageable pageable) {
    	Page<OrderNo> page = null;
    	try {
    		if (null != from && null != to) {
    			from = from.withTimeAtStartOfDay();
    			to = JodaTimeUtil.withTimeAtEndOfDay(to);
    			log.debug(String.format("case between from=%s, to=%s", from, to));
    			page = orderNoRepository.findByStatusIsAndCreatedDateBetween(status, from, to, pageable);
    		} else if (null == from && null == to) {
    			log.debug("case without date");
    			page = orderNoRepository.findByStatusIs(status, pageable);
    		} else if (null == from) {
    			to = JodaTimeUtil.withTimeAtEndOfDay(to);
    			log.debug(String.format("case less then equal=%s", to));
    			page = orderNoRepository.findByStatusIsAndCreatedDateLessThanEqual(status, to, pageable);
    		} else if (null == to) {
    			from = from.withTimeAtStartOfDay();
    			log.debug(String.format("case greater than equal=%s", from));
    			page = orderNoRepository.findByStatusIsAndCreatedDateGreaterThanEqual(status, from, pageable);
    		} 
    	} catch (Exception e) {
    		log.error("Exception", e);
    	}
    	return page;
    }    
    
    public BigDecimal getSumAmountByStatusCreatedDate(String status, DateTime from, DateTime to) {
    	BigDecimal sumAmount = null;
    	try {
    		if (null != from && null != to) {
    			from = from.withTimeAtStartOfDay();
    			to = JodaTimeUtil.withTimeAtEndOfDay(to);
    			log.debug(String.format("case between from=%s, to=%s", from, to));
    			sumAmount = orderNoRepository.getSumAmountByStatusCreatedDateBetween(status, from, to);
    		} else if (null == from && null == to) {
    			DateTime now = new DateTime();
    			from = now.withTimeAtStartOfDay();
    			to = JodaTimeUtil.withTimeAtEndOfDay(now);
    			log.debug(String.format("case without date from=%s, to=%s", from, to));
    			sumAmount = orderNoRepository.getSumAmountByStatusCreatedDateBetween(status, from, to);
    		} else if (null == from) {
    			to = JodaTimeUtil.withTimeAtEndOfDay(to);
    			log.debug(String.format("case before equal=%s", to));
    			sumAmount = orderNoRepository.getSumAmountByStatusCreatedDateBeforeEqual(status, to);
    		} else if (null == to) {
    			from = from.withTimeAtStartOfDay();
    			log.debug(String.format("case after equal=%s", from));
    			sumAmount = orderNoRepository.getSumAmountByStatusCreatedDateAfterEqual(status, from);
    		}
    	} catch (Exception e) {
    		log.error("Exception", e);
    	}
    	return sumAmount;
    }
    
    public List<PieChart> getSaleItemByStatusCreatedDateBetween(String status, DateTime from, DateTime to) {
    	List<PieChart> saleItems = new ArrayList<PieChart>();
    	try {
    		if (null != from && null != to) {
    			from = from.withTimeAtStartOfDay();
    			to = JodaTimeUtil.withTimeAtEndOfDay(to);
    		} else if (null == from && null == to) {
    			DateTime now = new DateTime();
    			from = now.dayOfMonth().withMinimumValue();
    			from = from.withTimeAtStartOfDay();
    			to = now.dayOfMonth().withMaximumValue();
    			to = JodaTimeUtil.withTimeAtEndOfDay(to);
    		} else if (null == from) {
    			from = to.dayOfMonth().withMinimumValue();
    			from = from.withTimeAtStartOfDay();
    			to = JodaTimeUtil.withTimeAtEndOfDay(to);
    		} else if (null == to) {
    			from = from.withTimeAtStartOfDay();
    			DateTime now = new DateTime();
    			to = JodaTimeUtil.withTimeAtEndOfDay(now);
    		}
			log.debug(String.format("case between from=%s, to=%s", from, to));
			saleItems = orderDetailRepository.getSaleItemByStatusCreatedDateBetween(status, from, to);
    	} catch (Exception e) {
    		log.error("Exception", e);
    	}    	
    	return saleItems;
    }
    
    public List<LineChart> getSaleByStatusCreatedDateBetween(String status, DateTime from, DateTime to) {
    	List<LineChart> sales = new ArrayList<LineChart>();
    	try {
    		if (null != from && null != to) {
    			from = from.withTimeAtStartOfDay();
    			to = JodaTimeUtil.withTimeAtEndOfDay(to);
    		} else if (null == from && null == to) {
    			DateTime now = new DateTime();
    			from = now.dayOfMonth().withMinimumValue();
    			from = from.withTimeAtStartOfDay();
    			to = now.dayOfMonth().withMaximumValue();
    			to = JodaTimeUtil.withTimeAtEndOfDay(to);
    		} else if (null == from) {
    			from = to.dayOfMonth().withMinimumValue();
    			from = from.withTimeAtStartOfDay();
    			to = JodaTimeUtil.withTimeAtEndOfDay(to);
    		} else if (null == to) {
    			from = from.withTimeAtStartOfDay();
    			DateTime now = new DateTime();
    			to = JodaTimeUtil.withTimeAtEndOfDay(now);
    		}
			log.debug(String.format("case between from=%s, to=%s", from, to));
			sales = orderNoRepository.getSaleByStatusCreatedDateBetween(status, from, to);
    	} catch (Exception e) {
    		log.error("Exception", e);
    	}    	
    	return sales;
    }    

}
