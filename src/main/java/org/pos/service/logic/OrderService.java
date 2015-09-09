package org.pos.service.logic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.pos.domain.User;
import org.pos.domain.logic.OrderDetail;
import org.pos.domain.logic.OrderNo;
import org.pos.repository.UserRepository;
import org.pos.repository.logic.OrderDetailRepository;
import org.pos.repository.logic.OrderNoRepository;
import org.pos.security.AuthoritiesConstants;
import org.pos.service.MailService;
import org.pos.util.JodaTimeUtil;
import org.pos.util.OrderStatus;
import org.pos.web.rest.dto.logic.LineChartDTO;
import org.pos.web.rest.dto.logic.PieChartDTO;
import org.pos.web.websocket.dto.logic.ChartDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

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
    
    @Inject
    private MailService mailService;

    @Inject
    private MessageSource messageSource;

    @Inject
    private SpringTemplateEngine templateEngine;
    
	@Inject
	private UserRepository userRepository;    

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
    
    public BigDecimal getSumReceivableAmountByStatusCreatedDate(String status, DateTime from, DateTime to) {
    	BigDecimal sumAmount = null;
    	try {
    		if (null != from && null != to) {
    			from = from.withTimeAtStartOfDay();
    			to = JodaTimeUtil.withTimeAtEndOfDay(to);
    			log.debug(String.format("case between from=%s, to=%s", from, to));
    			sumAmount = orderNoRepository.getSumReceivableAmountByStatusCreatedDateBetween(status, from, to);
    		} else if (null == from && null == to) {
    			DateTime now = new DateTime();
    			from = now.withTimeAtStartOfDay();
    			to = JodaTimeUtil.withTimeAtEndOfDay(now);
    			log.debug(String.format("case without date from=%s, to=%s", from, to));
    			sumAmount = orderNoRepository.getSumReceivableAmountByStatusCreatedDateBetween(status, from, to);
    		} else if (null == from) {
    			to = JodaTimeUtil.withTimeAtEndOfDay(to);
    			log.debug(String.format("case before equal=%s", to));
    			sumAmount = orderNoRepository.getSumReceivableAmountByStatusCreatedDateBeforeEqual(status, to);
    		} else if (null == to) {
    			from = from.withTimeAtStartOfDay();
    			log.debug(String.format("case after equal=%s", from));
    			sumAmount = orderNoRepository.getSumReceivableAmountByStatusCreatedDateAfterEqual(status, from);
    		}
    	} catch (Exception e) {
    		log.error("Exception", e);
    	}
    	return sumAmount;
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
			log.debug(String.format("case between from=%s, to=%s", from, to));
			saleItems = orderDetailRepository.getSaleItemByStatusCreatedDateBetween(status, from, to);
    	} catch (Exception e) {
    		log.error("Exception", e);
    	}    	
    	return saleItems;
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
					if (CollectionUtils.isEmpty(orderNo.getDetails())) {
						orderNo.setStatus(OrderStatus.CANCEL.toString());
					}
					// for bidirectional association
					for (OrderDetail orderDetail : orderNo.getDetails()) {
						orderDetail.setOrderNo(orderNo);
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
			log.debug(String.format("case between from=%s, to=%s", from, to));
			sales = orderNoRepository.getSaleByStatusCreatedDateBetween(status, from, to);
			saleItems = orderDetailRepository.getSaleItemByStatusCreatedDateBetween(status, from, to);
			ChartDTO chartDTO = new ChartDTO(sales, saleItems);
			messagingTemplate.convertAndSend("/topic/chart", chartDTO);
    	} catch (Exception e) {
    		log.error("Exception", e);
    	}
    }
    
    /**
     * Revenue report should be automatically send email everyday.
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at 23:00
     * </p>
     */
    @Scheduled(cron = "${cron.email.revenue.report}")
    public void sendRevenueReportMail() {
        log.debug("Sending revenue report e-mail");
        DateTime now = new DateTime();
        List<OrderNo> orders = orderNoRepository.findByStatusIsAndCreatedDateBetween(OrderStatus.PAYMENT.toString(), now.withTimeAtStartOfDay(), JodaTimeUtil.withTimeAtEndOfDay(now));
        List<User> users = userRepository.findAllByActivatedIsAndAuthorityNameIs(true, AuthoritiesConstants.ADMIN);
        if (CollectionUtils.isNotEmpty(orders) && CollectionUtils.isNotEmpty(users)) {
        	BigDecimal revenueAmount = getSumReceivableAmountByStatusCreatedDate(OrderStatus.PAYMENT.toString(), now.withTimeAtStartOfDay(), JodaTimeUtil.withTimeAtEndOfDay(now));
	        Locale locale = Locale.forLanguageTag("vi");
	        Context context = new Context(locale);
	        context.setVariable("now", now);
	        context.setVariable("revenueAmount", revenueAmount);
	        context.setVariable("orders", orders);
	        String to = "";
	        for (User user : users) {
				to += user.getEmail() + ";";
			}
	        String subject = messageSource.getMessage("revenue.report.email.title", null, locale);
	        String content = templateEngine.process("revenueReportEmail", context);
	        mailService.sendEmail(to, subject, content, false, true);
        }
    }

}
