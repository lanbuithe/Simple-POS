package org.pos.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pos.Application;
import org.pos.service.logic.OrderService;
import org.pos.util.OrderStatus;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class OrderServiceTest {
	
	@Inject
	private OrderService orderService;

	@Test
	public void testGetSumAmountByStatusCreatedDate() {
		// case between
    	DateTime from = new DateTime("2015-06-20T15:00:00.000+07:00");
    	DateTime to = new DateTime("2015-06-26T14:00:00.000+07:00");
    	BigDecimal expectSumAmount = new BigDecimal(83000);
    	BigDecimal sumAmount = orderService.getSumAmountByStatusCreatedDate(OrderStatus.HOLD.name(), from, to);
    	assertThat(sumAmount).isEqualByComparingTo(expectSumAmount);
    	// case before equal
    	to = new DateTime("2015-06-27T14:00:00.000+07:00");
    	expectSumAmount = new BigDecimal(24000);
    	sumAmount = orderService.getSumAmountByStatusCreatedDate(OrderStatus.PAYMENT.name(), null, to);
    	assertThat(sumAmount).isEqualByComparingTo(expectSumAmount);
    	// case after equal
    	from = new DateTime("2015-06-22T14:00:00.000+07:00");
    	expectSumAmount = new BigDecimal(24000);
    	sumAmount = orderService.getSumAmountByStatusCreatedDate(OrderStatus.CANCEL.name(), from, null);
    	assertThat(sumAmount).isEqualByComparingTo(expectSumAmount);
	}
}
