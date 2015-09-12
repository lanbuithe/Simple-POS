package org.pos.service.logic;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.pos.domain.logic.OrderNo;
import org.pos.repository.UserRepository;
import org.pos.repository.logic.OrderNoRepository;
import org.pos.security.AuthoritiesConstants;
import org.pos.service.MailService;
import org.pos.util.JodaTimeUtil;
import org.pos.util.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

@Service
@Transactional
public class EmailService {
	
	private final Logger log = LoggerFactory.getLogger(EmailService.class);
	
    @Inject
    private MailService mailService;

    @Inject
    private MessageSource messageSource;

    @Inject
    private SpringTemplateEngine templateEngine;
    
    @Inject
    private OrderNoRepository orderNoRepository;
    
	@Inject
	private UserRepository userRepository;
	
    @Inject
    private OrderService orderService;
	
    /**
     * Revenue report should be automatically send email everyday.
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at
     * </p>
     */
    @Scheduled(cron = "${cron.email.revenue.report}")
    public void sendRevenueReportMail() {
        log.debug("Sending revenue report e-mail");
        DateTime now = new DateTime();
        List<OrderNo> orders = orderNoRepository.findByStatusIsAndCreatedDateBetween(OrderStatus.PAYMENT.toString(), now.withTimeAtStartOfDay(), JodaTimeUtil.withTimeAtEndOfDay(now));
        List<String> emails = userRepository.findAllEmailByActivatedIsAndAuthorityNameIs(true, AuthoritiesConstants.ADMIN);
        if (CollectionUtils.isNotEmpty(orders) && CollectionUtils.isNotEmpty(emails)) {
        	BigDecimal revenueAmount = orderService.getSumReceivableAmountByStatusCreatedDate(OrderStatus.PAYMENT.toString(), now.withTimeAtStartOfDay(), JodaTimeUtil.withTimeAtEndOfDay(now));
	        Locale locale = Locale.forLanguageTag("vi");
	        Context context = new Context(locale);
	        context.setVariable("now", now);
	        context.setVariable("revenueAmount", revenueAmount);
	        context.setVariable("orders", orders);
	        String to = StringUtils.join(emails, ";");
	        String subject = messageSource.getMessage("revenue.report.email.title", null, locale);
	        String content = templateEngine.process("revenueReportEmail", context);
	        mailService.sendEmail(to, subject, content, false, true);
        }
    }
    
}
