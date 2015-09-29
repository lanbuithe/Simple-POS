package org.pos.service.logic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.pos.domain.logic.OrderDetail;
import org.pos.domain.logic.OrderNo;
import org.pos.repository.logic.OrderDetailRepository;
import org.pos.repository.logic.OrderNoRepository;
import org.pos.security.AuthoritiesConstants;
import org.pos.security.SecurityUtils;
import org.pos.util.JodaTimeUtil;
import org.pos.util.OrderStatus;
import org.pos.web.rest.dto.logic.LineChartDTO;
import org.pos.web.rest.dto.logic.PieChartDTO;
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
    			log.debug("case between from={}, to={}", from, to);
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
    			log.debug("case less then equal={}", to);
    			if (null != tableId) {
    				page = orderNoRepository.findByTableNoIdIsAndStatusIsAndCreatedDateLessThanEqual(tableId, status, to, pageable);
    			} else {
    				page = orderNoRepository.findByStatusIsAndCreatedDateLessThanEqual(status, to, pageable);	
    			}     			
    		} else if (null == to) {
    			from = from.withTimeAtStartOfDay();
    			log.debug("case greater than equal={}", from);
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
    
    public Page<OrderNo> getByStatusCreatedDate(String status, DateTime from, DateTime to, Pageable pageable) {
    	return this.getByTableIdStatusCreatedDate(null, status, from, to, pageable);
    }    
    
    public BigDecimal getSumReceivableAmountByStatusCreatedDate(String status, DateTime from, DateTime to) {
    	BigDecimal sumAmount = null;
    	try {
    		if (null != from && null != to) {
    			from = from.withTimeAtStartOfDay();
    			to = JodaTimeUtil.withTimeAtEndOfDay(to);
    			log.debug("case between from={}, to={}", from, to);
    			sumAmount = orderNoRepository.getSumReceivableAmountByStatusCreatedDateBetween(status, from, to);
    		} else if (null == from && null == to) {
    			DateTime now = new DateTime();
    			from = now.withTimeAtStartOfDay();
    			to = JodaTimeUtil.withTimeAtEndOfDay(now);
    			log.debug("case without date from={}, to={}", from, to);
    			sumAmount = orderNoRepository.getSumReceivableAmountByStatusCreatedDateBetween(status, from, to);
    		} else if (null == from) {
    			to = JodaTimeUtil.withTimeAtEndOfDay(to);
    			log.debug("case before equal=%s", to);
    			sumAmount = orderNoRepository.getSumReceivableAmountByStatusCreatedDateBeforeEqual(status, to);
    		} else if (null == to) {
    			from = from.withTimeAtStartOfDay();
    			log.debug("case after equal={}", from);
    			sumAmount = orderNoRepository.getSumReceivableAmountByStatusCreatedDateAfterEqual(status, from);
    		}
    	} catch (Exception e) {
    		log.error("Exception", e);
    	} finally {
    		if (null == sumAmount) {
    			sumAmount = new BigDecimal(0);
    		}
    	}
    	return sumAmount;
    }       
    
    public List<LineChartDTO> getSaleByStatusCreatedDateBetween(String status, DateTime from, DateTime to) {
    	List<LineChartDTO> sales = new ArrayList<LineChartDTO>();
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
    		boolean adminRole = SecurityUtils.isUserInRole(AuthoritiesConstants.ADMIN);
			log.debug("case between from={}, to={}, call by role admin={}", from, to, adminRole);
			if (adminRole) {
				sales = orderNoRepository.getSaleByStatusIsAndCreatedDateBetween(status, from, to);
			} else {
				String username = SecurityUtils.getCurrentLogin();
				sales = orderNoRepository.getSaleByStatusIsAndCreatedDateBetweenAndCreatedByIs(status, from, to, username);
			}
    	} catch (Exception e) {
    		log.error("Exception", e);
    	}    	
    	return sales;
    }
    
    public List<PieChartDTO> getSaleItemByStatusCreatedDateBetween(String status, DateTime from, DateTime to) {
    	List<PieChartDTO> saleItems = new ArrayList<PieChartDTO>();
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
			saleItems = orderDetailRepository.getSaleItemByStatusIsAndCreatedDateBetween(status, from, to);
    		boolean adminRole = SecurityUtils.isUserInRole(AuthoritiesConstants.ADMIN);
			log.debug("case between from={}, to={}, call by role admin={}", from, to, adminRole);
			if (adminRole) {
				saleItems = orderDetailRepository.getSaleItemByStatusIsAndCreatedDateBetween(status, from, to);
			} else {
				String username = SecurityUtils.getCurrentLogin();
				saleItems = orderDetailRepository.getSaleItemByStatusIsAndCreatedDateBetweenAndCreatedByIs(status, from, to, username);
			}
    	} catch (Exception e) {
    		log.error("Exception", e);
    	}    	
    	return saleItems;
    } 
    
    public boolean moveItem(List<OrderNo> orderNos) {
    	boolean result = true;
    	try {
    		if (CollectionUtils.isNotEmpty(orderNos)) {
    			for (OrderNo orderNo : orderNos) {
    				// order has no item
					if (CollectionUtils.isEmpty(orderNo.getDetails())) {
						orderNo.setStatus(OrderStatus.CANCEL.toString());
					}
					// new order
					if (null == orderNo.getId()) {
						orderNo.setStatus(OrderStatus.HOLD.toString());
					}
					// for bidirectional association
					for (OrderDetail orderDetail : orderNo.getDetails()) {
						orderDetail.setOrderNo(orderNo);
						if (null == orderNo.getId()) {
							orderDetail.setId(null);
						}
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
    	List<LineChartDTO> sales = new ArrayList<LineChartDTO>();
    	List<PieChartDTO> saleItems = new ArrayList<PieChartDTO>();
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
    		String username = SecurityUtils.getCurrentLogin();
    		boolean adminRole = SecurityUtils.isUserInRole(AuthoritiesConstants.ADMIN);
			log.debug("case between from={}, to={}, call by user {} with role admin={}", from, to, username, adminRole);
			if (adminRole) {
				sales = orderNoRepository.getSaleByStatusIsAndCreatedDateBetween(status, from, to);
				saleItems = orderDetailRepository.getSaleItemByStatusIsAndCreatedDateBetween(status, from, to);
			} else {
				sales = orderNoRepository.getSaleByStatusIsAndCreatedDateBetweenAndCreatedByIs(status, from, to, username);
				saleItems = orderDetailRepository.getSaleItemByStatusIsAndCreatedDateBetweenAndCreatedByIs(status, from, to, username);
			}
			ChartDTO chartDTO = new ChartDTO(sales, saleItems);
			//messagingTemplate.convertAndSend("/topic/chart", chartDTO);
			messagingTemplate.convertAndSendToUser(username, "/queue/sale", chartDTO);
    	} catch (Exception e) {
    		log.error("Exception", e);
    	}
    }
    
    public OrderNo saveOrder(OrderNo order) {
    	if (null != order && CollectionUtils.isNotEmpty(order.getDetails())) {
    		for (OrderDetail orderDetail : order.getDetails()) {
    			log.debug("update OrderDetail : {}", orderDetail);
    			orderDetail.setOrderNo(order);
    		}
    	}
    	order = orderNoRepository.save(order);
        if (OrderStatus.PAYMENT.name().equalsIgnoreCase(order.getStatus())) {
        	this.getSaleChart(OrderStatus.PAYMENT.name(), null, null);
        }
        return order;
    }
    

}
