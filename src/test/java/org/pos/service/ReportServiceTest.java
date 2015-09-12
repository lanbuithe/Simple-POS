package org.pos.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

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
import org.pos.service.logic.ReportService;
import org.pos.util.OrderStatus;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
public class ReportServiceTest {
	
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
	
	@Inject
	private ReportService reportService;
	
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
	
	@After
	public void reSetUp() {
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
		itemCategoryRepository.flush();
		itemRepository.flush();
		tableNoRepository.flush();
		orderNoRepository.flush();
	}

	@Test
	public void testGenerateRevenueReport() throws JRException, IOException, SQLException {
		reportService.generateRevenueReport();
	}
	
}
