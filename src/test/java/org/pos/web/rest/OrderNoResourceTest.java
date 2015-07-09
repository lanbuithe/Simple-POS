package org.pos.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.pos.Application;
import org.pos.domain.logic.OrderNo;
import org.pos.repository.logic.OrderNoRepository;
import org.pos.service.logic.OrderService;
import org.pos.util.OrderStatus;
import org.pos.web.rest.logic.OrderNoResource;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test class for the OrderNoResource REST controller.
 *
 * @see OrderNoResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class OrderNoResourceTest {


    private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.ZERO;
    private static final BigDecimal UPDATED_AMOUNT = BigDecimal.ONE;
    private static final String DEFAULT_STATUS = "SAMPLE_TEXT";
    private static final String UPDATED_STATUS = "UPDATED_TEXT";

    private static final LocalDate DEFAULT_CREATED_DATE = new LocalDate(0L);
    private static final LocalDate UPDATED_CREATED_DATE = new LocalDate();

    private static final LocalDate DEFAULT_LAST_MODIFIED_DATE = new LocalDate(0L);
    private static final LocalDate UPDATED_LAST_MODIFIED_DATE = new LocalDate();

    @Inject
    private OrderNoRepository orderNoRepository;
    
    @Inject
    private OrderService orderService;

    private MockMvc restOrderNoMockMvc;

    private OrderNo orderNo;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OrderNoResource orderNoResource = new OrderNoResource();
        ReflectionTestUtils.setField(orderNoResource, "orderNoRepository", orderNoRepository);
        ReflectionTestUtils.setField(orderNoResource, "orderService", orderService);
        this.restOrderNoMockMvc = MockMvcBuilders.standaloneSetup(orderNoResource).build();
    }

    @Before
    public void initTest() {
        orderNo = new OrderNo();
        orderNo.setAmount(DEFAULT_AMOUNT);
        orderNo.setStatus(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void createOrderNo() throws Exception {
        int databaseSizeBeforeCreate = orderNoRepository.findAll().size();

        // Create the OrderNo
        restOrderNoMockMvc.perform(post("/api/orderNos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(orderNo)))
                .andExpect(status().isCreated());

        // Validate the OrderNo in the database
        List<OrderNo> orderNos = orderNoRepository.findAll();
        assertThat(orderNos).hasSize(databaseSizeBeforeCreate + 1);
        OrderNo testOrderNo = orderNos.get(orderNos.size() - 1);
        assertThat(testOrderNo.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testOrderNo.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testOrderNo.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testOrderNo.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void checkAmountIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(orderNoRepository.findAll()).hasSize(0);
        // set the field null
        orderNo.setAmount(null);

        // Create the OrderNo, which fails.
        restOrderNoMockMvc.perform(post("/api/orderNos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(orderNo)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<OrderNo> orderNos = orderNoRepository.findAll();
        assertThat(orderNos).hasSize(0);
    }

    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(orderNoRepository.findAll()).hasSize(0);
        // set the field null
        orderNo.setStatus(null);

        // Create the OrderNo, which fails.
        restOrderNoMockMvc.perform(post("/api/orderNos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(orderNo)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<OrderNo> orderNos = orderNoRepository.findAll();
        assertThat(orderNos).hasSize(0);
    }

    @Test
    @Transactional
    public void checkLastModifiedDateIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(orderNoRepository.findAll()).hasSize(0);
        // set the field null
        orderNo.setLastModifiedDate(null);

        // Create the OrderNo, which fails.
        restOrderNoMockMvc.perform(post("/api/orderNos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(orderNo)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<OrderNo> orderNos = orderNoRepository.findAll();
        assertThat(orderNos).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllOrderNos() throws Exception {
        // Initialize the database
        orderNoRepository.saveAndFlush(orderNo);

        // Get all the orderNos
        restOrderNoMockMvc.perform(get("/api/orderNos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(orderNo.getId().intValue())))
                .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
                .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
                .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }

    @Test
    @Transactional
    public void getOrderNo() throws Exception {
        // Initialize the database
        orderNoRepository.saveAndFlush(orderNo);

        // Get the orderNo
        restOrderNoMockMvc.perform(get("/api/orderNos/{id}", orderNo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(orderNo.getId().intValue()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingOrderNo() throws Exception {
        // Get the orderNo
        restOrderNoMockMvc.perform(get("/api/orderNos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOrderNo() throws Exception {
        // Initialize the database
        orderNoRepository.saveAndFlush(orderNo);
		
		int databaseSizeBeforeUpdate = orderNoRepository.findAll().size();

        // Update the orderNo
        orderNo.setAmount(UPDATED_AMOUNT);
        orderNo.setStatus(UPDATED_STATUS);
        restOrderNoMockMvc.perform(put("/api/orderNos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(orderNo)))
                .andExpect(status().isOk());

        // Validate the OrderNo in the database
        List<OrderNo> orderNos = orderNoRepository.findAll();
        assertThat(orderNos).hasSize(databaseSizeBeforeUpdate);
        OrderNo testOrderNo = orderNos.get(orderNos.size() - 1);
        assertThat(testOrderNo.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testOrderNo.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testOrderNo.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testOrderNo.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    public void deleteOrderNo() throws Exception {
        // Initialize the database
        orderNoRepository.saveAndFlush(orderNo);
		
		int databaseSizeBeforeDelete = orderNoRepository.findAll().size();

        // Get the orderNo
        restOrderNoMockMvc.perform(delete("/api/orderNos/{id}", orderNo.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<OrderNo> orderNos = orderNoRepository.findAll();
        assertThat(orderNos).hasSize(databaseSizeBeforeDelete - 1);
    }
    
    @Test
    @Transactional
    public void getByStatusCreatedDate() throws Exception {
        // Get hold order
    	// case null
    	restOrderNoMockMvc.perform(get("/api/orders")
        		.param("status", "HOLD")
        		.param("page", "1")
        		.param("per_page", "1"))
        	.andExpect(status().isOk());   	
    }
    
    @Test
    @Transactional
    public void testGetSumAmountByStatusCreatedDate() throws Exception {
        // Get sum amount
    	// case null
    	MvcResult mvcResult = restOrderNoMockMvc.perform(get("/api/orders/amount")
        		.param("status",  OrderStatus.HOLD.name())
        		.param("from", DateTime.now().toString())
        		.param("to", DateTime.now().toString()))
        	.andExpect(status().isOk())
        	.andReturn();
    	
    	String sumAmount = mvcResult.getResponse().getContentAsString();
    	assertThat(sumAmount).isEmpty();
    	// case between
    	restOrderNoMockMvc.perform(get("/api/orders/amount")
        		.param("status", OrderStatus.HOLD.name())
        		.param("from", "2015-06-20T15:00:00.000+07:00")
        		.param("to", "2015-06-26T14:00:00.000+07:00"))
        	.andExpect(status().isOk())
        	.andExpect(content().string("83000.00"));
    	// case before equal
    	restOrderNoMockMvc.perform(get("/api/orders/amount")
        		.param("status", OrderStatus.PAYMENT.name())
        		.param("to", "2015-06-27T14:00:00.000+07:00"))
        	.andExpect(status().isOk())
        	.andExpect(content().string("24000.00"));    	
    	// case after equal
    	restOrderNoMockMvc.perform(get("/api/orders/amount")
        		.param("status", OrderStatus.CANCEL.name())
        		.param("from", "2015-06-22T14:00:00.000+07:00"))
        	.andExpect(status().isOk())
        	.andExpect(content().string("24000.00"));     	
    }
}
