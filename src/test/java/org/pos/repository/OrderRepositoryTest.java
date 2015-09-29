package org.pos.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pos.Application;
import org.pos.config.Constants;
import org.pos.domain.logic.Item;
import org.pos.domain.logic.ItemCategory;
import org.pos.domain.logic.OrderDetail;
import org.pos.domain.logic.OrderNo;
import org.pos.domain.logic.TableNo;
import org.pos.repository.logic.ItemCategoryRepository;
import org.pos.repository.logic.ItemRepository;
import org.pos.repository.logic.OrderDetailRepository;
import org.pos.repository.logic.OrderNoRepository;
import org.pos.repository.logic.TableNoRepository;
import org.pos.util.DateTimePattern;
import org.pos.util.JodaTimeUtil;
import org.pos.util.OrderStatus;
import org.pos.web.rest.dto.logic.LineChartDTO;
import org.pos.web.rest.dto.logic.PieChartDTO;
import org.pos.web.rest.util.PaginationUtil;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
public class OrderRepositoryTest {
	
	@Inject
	private ItemCategoryRepository itemCategoryRepository;
	
	@Inject
	private ItemRepository itemRepository;
	
	@Inject
	private TableNoRepository tableNoRepository;

	@Inject
    private OrderNoRepository orderNoRepository;
	
	@Inject
    private OrderDetailRepository orderDetailRepository;
	
	private final String defaultCreatedBy = "admin";
	
	private BigDecimal defaultItemPrice = new BigDecimal(12000);
	
	private Integer defaultOrderDetailQuantity = 1;
	
	private TableNo defaultTable = null;
	
	private OrderNo defaultOrder = null;
	
	private BigDecimal expectSumReceivableAmount = new BigDecimal(24000);
	
	@Before
	public void setUp() {
		// delete all data
		orderDetailRepository.deleteAll();
		orderNoRepository.deleteAll();
		tableNoRepository.deleteAll();
		itemRepository.deleteAll();
		itemCategoryRepository.deleteAll();
		// create new item category
		ItemCategory itemCategory = new ItemCategory();
		itemCategory.setName("Cà phê");
		itemCategory.setDescription("Thức uống");
		itemCategory.setCreatedBy(defaultCreatedBy);
		itemCategory = itemCategoryRepository.saveAndFlush(itemCategory);
		// create new item
		Item item = new Item();
		item.setName("Cà phê đen đá");
		item.setDescription("Cà phê đen đá");
		item.setPrice(defaultItemPrice);
		item.setCreatedBy(defaultCreatedBy);
		item.setCategory(itemCategory);
		item = itemRepository.saveAndFlush(item);
		// create new table
		defaultTable = new TableNo();
		defaultTable.setName("Bàn số 1");
		defaultTable.setDescription("Bàn tại vị trí số 1");
		defaultTable.setCreatedBy(defaultCreatedBy);
		defaultTable = tableNoRepository.saveAndFlush(defaultTable);
		// create order
		defaultOrder = new OrderNo();
		defaultOrder.setStatus(OrderStatus.PAYMENT.toString());
		defaultOrder.setTableNo(defaultTable);
		defaultOrder.setCreatedBy(defaultCreatedBy);
		// create order detail
		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setQuantity(defaultOrderDetailQuantity);
		orderDetail.setItem(item);
		orderDetail.setCreatedBy(defaultCreatedBy);
		orderDetail.setOrderNo(defaultOrder);
		List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
		orderDetails.add(orderDetail);
		// create new item
		item = new Item();
		item.setName("Cà phê sữa đá");
		item.setDescription("Cà phê sữa đá");
		item.setPrice(defaultItemPrice);
		item.setCreatedBy(defaultCreatedBy);
		item.setCategory(itemCategory);
		item = itemRepository.saveAndFlush(item);
		// create order detail
		orderDetail = new OrderDetail();
		orderDetail.setQuantity(defaultOrderDetailQuantity);
		orderDetail.setItem(item);
		orderDetail.setCreatedBy(defaultCreatedBy);
		orderDetail.setOrderNo(defaultOrder);
		orderDetails.add(orderDetail);
		// set details
		defaultOrder.setDetails(orderDetails);
		defaultOrder = orderNoRepository.saveAndFlush(defaultOrder);
	}
	
	@Test
	public void testFindByStatusIs() {
		Page<OrderNo> page = orderNoRepository.findByStatusIs(OrderStatus.PAYMENT.name(), PaginationUtil.generatePageRequest(1, 6));
		testFindBy(page);
	}
	
	@Test
	public void testFindByStatusIsAndCreatedDateBetween() {
    	DateTime now = new DateTime();
    	DateTime to = JodaTimeUtil.withTimeAtEndOfDay(now);
		Page<OrderNo> page = orderNoRepository.findByStatusIsAndCreatedDateBetween(OrderStatus.PAYMENT.name(), now.withTimeAtStartOfDay(), to, PaginationUtil.generatePageRequest(1, 6));
		testFindBy(page);	
	}
	
	@Test
	public void testFindByStatusIsAndCreatedDateLessThanEqual() {
		DateTime now = new DateTime();
		Page<OrderNo> page = orderNoRepository.findByStatusIsAndCreatedDateLessThanEqual(OrderStatus.PAYMENT.name(), JodaTimeUtil.withTimeAtEndOfDay(now), PaginationUtil.generatePageRequest(1, 6));
		testFindBy(page);		
	}
	
	@Test
	public void testFindByStatusIsAndCreatedDateGreaterThanEqual() {
		DateTime now = new DateTime();
		Page<OrderNo> page = orderNoRepository.findByStatusIsAndCreatedDateGreaterThanEqual(OrderStatus.PAYMENT.name(), now.withTimeAtStartOfDay(), PaginationUtil.generatePageRequest(1, 6));
		testFindBy(page);		
	}
	
	@Test
	public void testFindByTableNoIdIsAndStatusIs() {
		Page<OrderNo> page = orderNoRepository.findByTableNoIdIsAndStatusIs(defaultTable.getId(), OrderStatus.PAYMENT.name(), PaginationUtil.generatePageRequest(1, 6));
		testFindBy(page);
	}
	
	@Test
	public void testFindByTableNoIdIsAndStatusIsAndCreatedDateBetween() {
    	DateTime now = new DateTime();
    	DateTime to = JodaTimeUtil.withTimeAtEndOfDay(now);
		Page<OrderNo> page = orderNoRepository.findByTableNoIdIsAndStatusIsAndCreatedDateBetween(defaultTable.getId(), OrderStatus.PAYMENT.name(), now.withTimeAtStartOfDay(), to, PaginationUtil.generatePageRequest(1, 6));
		testFindBy(page);
	}
	
	@Test
	public void testFindByTableNoIdIsAndStatusIsAndCreatedDateLessThanEqual() {
		DateTime now = new DateTime();
		Page<OrderNo> page = orderNoRepository.findByTableNoIdIsAndStatusIsAndCreatedDateLessThanEqual(defaultTable.getId(), OrderStatus.PAYMENT.name(), JodaTimeUtil.withTimeAtEndOfDay(now), PaginationUtil.generatePageRequest(1, 6));
		testFindBy(page);		
	}
	
	@Test
	public void testFindByTableNoIdIsAndStatusIsAndCreatedDateGreaterThanEqual() {
		DateTime now = new DateTime();
		Page<OrderNo> page = orderNoRepository.findByTableNoIdIsAndStatusIsAndCreatedDateGreaterThanEqual(defaultTable.getId(), OrderStatus.PAYMENT.name(), now.withTimeAtStartOfDay(), PaginationUtil.generatePageRequest(1, 6));
		testFindBy(page);			
	}
	
	private void testFindBy(Page<OrderNo> page) {
		assertThat(page).isNotNull();
		assertThat(page.getContent()).isNotNull().isNotEmpty();
		assertThat(page.getContent().size()).isEqualTo(1);
		assertThat(page.getContent().get(0).getDetails().size()).isEqualTo(2);			
	}
    
    @Test
    public void testGetSumReceivableAmountByStatusCreatedDateBetween() {
    	DateTime now = new DateTime();
    	DateTime to = JodaTimeUtil.withTimeAtEndOfDay(now);
    	BigDecimal sumReceivableAmount = orderNoRepository.getSumReceivableAmountByStatusCreatedDateBetween(OrderStatus.PAYMENT.name(), now.withTimeAtStartOfDay(), to);
    	testGetSumReceivableAmountBy(sumReceivableAmount);
    }
    
    @Test
    public void testGetSumReceivableAmountByStatusCreatedDateAfterEqual() {
    	DateTime now = new DateTime();
    	BigDecimal sumReceivableAmount = orderNoRepository.getSumReceivableAmountByStatusCreatedDateAfterEqual(OrderStatus.PAYMENT.name(), now.withTimeAtStartOfDay());
    	testGetSumReceivableAmountBy(sumReceivableAmount);
    }
    
    @Test
    public void testGetSumReceivableAmountByStatusCreatedDateBeforeEqual() {
    	DateTime now = new DateTime();
    	DateTime to = JodaTimeUtil.withTimeAtEndOfDay(now);
    	BigDecimal sumReceivableAmount = orderNoRepository.getSumReceivableAmountByStatusCreatedDateBeforeEqual(OrderStatus.PAYMENT.name(), to);
    	testGetSumReceivableAmountBy(sumReceivableAmount);   	
    }
    
    private void testGetSumReceivableAmountBy(BigDecimal sumReceivableAmount) {
    	assertThat(sumReceivableAmount).isEqualByComparingTo(expectSumReceivableAmount);
    }
    
    @Test
    public void testFindById() {
    	OrderNo orderNo = orderNoRepository.findById(defaultOrder.getId());
    	assertThat(orderNo).isNotNull();
		assertThat(orderNo.getDetails().size()).isEqualTo(2);
		assertThat(orderNo.getReceivableAmount()).isEqualByComparingTo(expectSumReceivableAmount);
		assertThat(orderNo.getTableNo()).isEqualTo(defaultTable);
    }
    
    @Test
    public void testGetSaleByStatusCreatedDateBetween() {
    	DateTime now = new DateTime();
    	DateTime to = JodaTimeUtil.withTimeAtEndOfDay(now);
    	List<LineChartDTO> lineChartDTOs = orderNoRepository.getSaleByStatusIsAndCreatedDateBetween(OrderStatus.PAYMENT.name(), now.withTimeAtStartOfDay(), to);
		assertThat(lineChartDTOs).isNotNull();
		assertThat(lineChartDTOs).isNotEmpty();
		assertThat(lineChartDTOs.size()).isEqualTo(1);
		assertThat(String.valueOf(lineChartDTOs.get(0).getKey())).isEqualTo(OrderStatus.PAYMENT.name());
		assertThat(String.valueOf(lineChartDTOs.get(0).getX())).isEqualTo(now.toString(DateTimePattern.ISO_DATE));
		assertThat(new BigDecimal(String.valueOf(lineChartDTOs.get(0).getY()))).isEqualByComparingTo(expectSumReceivableAmount);   	
    }
    
    @Test
    public void testGetSaleItemByStatusCreatedDateBetween() {
    	DateTime now = new DateTime();
    	DateTime to = JodaTimeUtil.withTimeAtEndOfDay(now);
    	List<PieChartDTO> pieChartDTOs = orderDetailRepository.getSaleItemByStatusIsAndCreatedDateBetween(OrderStatus.PAYMENT.name(), now.withTimeAtStartOfDay(), to);
		assertThat(pieChartDTOs).isNotNull();
		assertThat(pieChartDTOs).isNotEmpty();
		assertThat(pieChartDTOs.size()).isEqualTo(2);
		assertThat(String.valueOf(pieChartDTOs.get(0).getKey())).isNotNull();
		assertThat(new BigDecimal(String.valueOf(pieChartDTOs.get(0).getY()))).isEqualByComparingTo(defaultItemPrice.multiply(new BigDecimal(defaultOrderDetailQuantity))); 
    }
    
}
