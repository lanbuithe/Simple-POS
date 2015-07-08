package org.coffee.web.rest;

import org.coffee.Application;
import org.coffee.domain.logic.OrderDetail;
import org.coffee.repository.logic.OrderDetailRepository;
import org.coffee.web.rest.logic.OrderDetailResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.hasItem;

import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the OrderDetailResource REST controller.
 *
 * @see OrderDetailResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class OrderDetailResourceTest {


    private static final Integer DEFAULT_QUANTITY = 0;
    private static final Integer UPDATED_QUANTITY = 1;

    private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.ZERO;
    private static final BigDecimal UPDATED_AMOUNT = BigDecimal.ONE;

    @Inject
    private OrderDetailRepository orderDetailRepository;

    private MockMvc restOrderDetailMockMvc;

    private OrderDetail orderDetail;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OrderDetailResource orderDetailResource = new OrderDetailResource();
        ReflectionTestUtils.setField(orderDetailResource, "orderDetailRepository", orderDetailRepository);
        this.restOrderDetailMockMvc = MockMvcBuilders.standaloneSetup(orderDetailResource).build();
    }

    @Before
    public void initTest() {
        orderDetail = new OrderDetail();
        orderDetail.setQuantity(DEFAULT_QUANTITY);
        orderDetail.setAmount(DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    public void createOrderDetail() throws Exception {
        int databaseSizeBeforeCreate = orderDetailRepository.findAll().size();

        // Create the OrderDetail
        restOrderDetailMockMvc.perform(post("/api/orderDetails")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(orderDetail)))
                .andExpect(status().isCreated());

        // Validate the OrderDetail in the database
        List<OrderDetail> orderDetails = orderDetailRepository.findAll();
        assertThat(orderDetails).hasSize(databaseSizeBeforeCreate + 1);
        OrderDetail testOrderDetail = orderDetails.get(orderDetails.size() - 1);
        assertThat(testOrderDetail.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testOrderDetail.getAmount()).isEqualTo(DEFAULT_AMOUNT);
    }

    @Test
    @Transactional
    public void checkQuantityIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(orderDetailRepository.findAll()).hasSize(0);
        // set the field null
        orderDetail.setQuantity(null);

        // Create the OrderDetail, which fails.
        restOrderDetailMockMvc.perform(post("/api/orderDetails")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(orderDetail)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<OrderDetail> orderDetails = orderDetailRepository.findAll();
        assertThat(orderDetails).hasSize(0);
    }

    @Test
    @Transactional
    public void checkAmountIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(orderDetailRepository.findAll()).hasSize(0);
        // set the field null
        orderDetail.setAmount(null);

        // Create the OrderDetail, which fails.
        restOrderDetailMockMvc.perform(post("/api/orderDetails")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(orderDetail)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<OrderDetail> orderDetails = orderDetailRepository.findAll();
        assertThat(orderDetails).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllOrderDetails() throws Exception {
        // Initialize the database
        orderDetailRepository.saveAndFlush(orderDetail);

        // Get all the orderDetails
        restOrderDetailMockMvc.perform(get("/api/orderDetails"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(orderDetail.getId().intValue())))
                .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
                .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())));
    }

    @Test
    @Transactional
    public void getOrderDetail() throws Exception {
        // Initialize the database
        orderDetailRepository.saveAndFlush(orderDetail);

        // Get the orderDetail
        restOrderDetailMockMvc.perform(get("/api/orderDetails/{id}", orderDetail.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(orderDetail.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingOrderDetail() throws Exception {
        // Get the orderDetail
        restOrderDetailMockMvc.perform(get("/api/orderDetails/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOrderDetail() throws Exception {
        // Initialize the database
        orderDetailRepository.saveAndFlush(orderDetail);
		
		int databaseSizeBeforeUpdate = orderDetailRepository.findAll().size();

        // Update the orderDetail
        orderDetail.setQuantity(UPDATED_QUANTITY);
        orderDetail.setAmount(UPDATED_AMOUNT);
        restOrderDetailMockMvc.perform(put("/api/orderDetails")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(orderDetail)))
                .andExpect(status().isOk());

        // Validate the OrderDetail in the database
        List<OrderDetail> orderDetails = orderDetailRepository.findAll();
        assertThat(orderDetails).hasSize(databaseSizeBeforeUpdate);
        OrderDetail testOrderDetail = orderDetails.get(orderDetails.size() - 1);
        assertThat(testOrderDetail.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testOrderDetail.getAmount()).isEqualTo(UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    public void deleteOrderDetail() throws Exception {
        // Initialize the database
        orderDetailRepository.saveAndFlush(orderDetail);
		
		int databaseSizeBeforeDelete = orderDetailRepository.findAll().size();

        // Get the orderDetail
        restOrderDetailMockMvc.perform(delete("/api/orderDetails/{id}", orderDetail.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<OrderDetail> orderDetails = orderDetailRepository.findAll();
        assertThat(orderDetails).hasSize(databaseSizeBeforeDelete - 1);
    }
}
