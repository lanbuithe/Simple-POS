package org.pos.service.logic;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.pos.domain.logic.OrderNo;
import org.pos.repository.logic.OrderNoRepository;
import org.pos.util.JodaTimeUtil;
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

}
