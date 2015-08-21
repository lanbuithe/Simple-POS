package org.pos.service.logic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.pos.domain.logic.OrderNo;
import org.pos.repository.logic.OrderDetailRepository;
import org.pos.repository.logic.OrderNoRepository;
import org.pos.util.JodaTimeUtil;
import org.pos.util.OrderStatus;
import org.pos.web.rest.dto.logic.LineChart;
import org.pos.web.rest.dto.logic.PieChart;
import org.pos.web.websocket.dto.logic.ChartDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
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
    
    @Inject
    private SimpMessageSendingOperations messagingTemplate;

    public Page<OrderNo> getByTableIdStatusCreatedDate(Long tableId, String status, DateTime from, DateTime to, Pageable pageable) {
    	Page<OrderNo> page = null;
    	try {
    		if (null != from && null != to) {
    			from = from.withTimeAtStartOfDay();
    			to = JodaTimeUtil.withTimeAtEndOfDay(to);
    			log.debug(String.format("case between from=%s, to=%s", from, to));
    			if (null != tableId) {
    				page = orderNoRepository.findByTableNoIdIsAndStatusIsAndCreatedDateBetween(tableId, status, from, to, pageable);
    			} else {
    				page = orderNoRepository.findByStatusIsAndCreatedDateBetween(status, from, to, pageable);	
    			}
    		} else if (null == from && null == to) {
    			log.debug("case without date");
    			if (null != tableId) {
    				page = orderNoRepository.findByTableNoIdIsAndStatusIs(tableId, status, pageable);
    			} else {
    				page = orderNoRepository.findByStatusIs(status, pageable);	
    			}    			
    		} else if (null == from) {
    			to = JodaTimeUtil.withTimeAtEndOfDay(to);
    			log.debug(String.format("case less then equal=%s", to));
    			if (null != tableId) {
    				page = orderNoRepository.findByTableNoIdIsAndStatusIsAndCreatedDateLessThanEqual(tableId, status, to, pageable);
    			} else {
    				page = orderNoRepository.findByStatusIsAndCreatedDateLessThanEqual(status, to, pageable);	
    			}     			
    		} else if (null == to) {
    			from = from.withTimeAtStartOfDay();
    			log.debug(String.format("case greater than equal=%s", from));
    			if (null != tableId) {
    				page = orderNoRepository.findByTableNoIdIsAndStatusIsAndCreatedDateGreaterThanEqual(tableId, status, from, pageable);
    			} else {
    				page = orderNoRepository.findByStatusIsAndCreatedDateGreaterThanEqual(status, from, pageable);	
    			}     			
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
    
    public boolean moveItem(List<OrderNo> orderNos) {
    	boolean result = true;
    	try {
    		if (CollectionUtils.isNotEmpty(orderNos)) {
    			for (OrderNo orderNo : orderNos) {
    				log.debug(String.format("order no=%s", orderNo.toString()));
					if (CollectionUtils.isEmpty(orderNo.getDetails())) {
						orderNo.setStatus(OrderStatus.CANCEL.toString());
					}
				}
    			orderNoRepository.save(orderNos);
    		} else {
    			result = false;
    		}
    	} catch (Exception e) {
    		result = false;
    		log.error("Exception", e);
    	}
    	return result;
    }
    
    @Async
    public void getSaleChart(String status, DateTime from, DateTime to) {
    	List<LineChart> sales = new ArrayList<LineChart>();
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
			sales = orderNoRepository.getSaleByStatusCreatedDateBetween(status, from, to);
			saleItems = orderDetailRepository.getSaleItemByStatusCreatedDateBetween(status, from, to);
			ChartDTO chartDTO = new ChartDTO(sales, saleItems);
			messagingTemplate.convertAndSend("/topic/chart", chartDTO);
    	} catch (Exception e) {
    		log.error("Exception", e);
    	}
    }    

}
