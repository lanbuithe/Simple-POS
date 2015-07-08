package org.coffee.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.coffee.Application;
import org.coffee.domain.logic.OrderNo;
import org.coffee.repository.logic.OrderNoRepository;
import org.coffee.util.OrderStatus;
import org.coffee.web.rest.util.PaginationUtil;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class OrderNoRepositoryTest {

	@Inject
    private OrderNoRepository orderNoRepository;
	
	@Test
	public void testFindByStatusIs() {
		Page<OrderNo> page = orderNoRepository.findByStatusIs(OrderStatus.HOLD.name(), PaginationUtil.generatePageRequest(1, 1));
		assertThat(page).isNotNull();
		assertThat(page.getContent()).isNotNull().isNotEmpty();
		assertThat(page.getContent().size()).isEqualTo(1);
	}
	
	@Test
	public void testFindByStatusIsAndCreatedDateBetween() {
    	DateTime from = new DateTime("2015-06-01T00:00:00.000+07:00");
    	DateTime to = new DateTime("2015-06-30T00:00:00.000+07:00");
		Page<OrderNo> page = orderNoRepository.findByStatusIsAndCreatedDateBetween(OrderStatus.HOLD.name(), from, to, PaginationUtil.generatePageRequest(1, 2));
		assertThat(page).isNotNull();
		assertThat(page.getContent()).isNotNull().isNotEmpty();		
	}
	
	@Test
	public void testFindByStatusIsAndCreatedDateLessThanEqual() {
    	DateTime createdDate = new DateTime("2015-06-30T00:00:00.000+07:00");
		Page<OrderNo> page = orderNoRepository.findByStatusIsAndCreatedDateLessThanEqual(OrderStatus.CANCEL.name(), createdDate, PaginationUtil.generatePageRequest(1, 2));
		assertThat(page).isNotNull();
		assertThat(page.getContent()).isNotNull().isNotEmpty();		
	}
	
	@Test
	public void testFindByStatusIsAndCreatedDateGreaterThanEqual() {
    	DateTime createdDate = new DateTime("2015-06-01T00:00:00.000+07:00");
		Page<OrderNo> page = orderNoRepository.findByStatusIsAndCreatedDateGreaterThanEqual(OrderStatus.PAYMENT.name(), createdDate, PaginationUtil.generatePageRequest(1, 2));
		assertThat(page).isNotNull();
		assertThat(page.getContent()).isNotNull().isNotEmpty();		
	}	
    
    @Test
    public void testGetSumAmountByStatusCreatedDateBetween() {
    	DateTime from = new DateTime("2015-06-01T00:00:00.000+07:00");
    	DateTime to = new DateTime("2015-06-30T00:00:00.000+07:00");
    	BigDecimal expectSumAmount = new BigDecimal(83000);
    	BigDecimal sumAmount = orderNoRepository.getSumAmountByStatusCreatedDateBetween(OrderStatus.HOLD.name(), from, to);
    	assertThat(sumAmount).isEqualByComparingTo(expectSumAmount);
    }
    
    @Test
    public void testGetSumAmountByStatusCreatedDateAfterEqual() {
    	DateTime createdDate = new DateTime("2015-06-01T00:00:00.000+07:00");
    	BigDecimal expectSumAmount = new BigDecimal(24000);
    	BigDecimal sumAmount = orderNoRepository.getSumAmountByStatusCreatedDateAfterEqual(OrderStatus.PAYMENT.name(), createdDate);
    	assertThat(sumAmount).isEqualByComparingTo(expectSumAmount);
    }
    
    @Test
    public void testGetSumAmountByStatusCreatedDateBeforeEqual() {
    	DateTime createdDate = new DateTime("2015-06-30T00:00:00.000+07:00");
    	BigDecimal expectSumAmount = new BigDecimal(24000);
    	BigDecimal sumAmount = orderNoRepository.getSumAmountByStatusCreatedDateBeforeEqual(OrderStatus.CANCEL.name(), createdDate);
    	assertThat(sumAmount).isEqualByComparingTo(expectSumAmount);    	
    }
    
}
