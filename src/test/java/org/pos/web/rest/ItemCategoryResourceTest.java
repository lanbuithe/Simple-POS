package org.pos.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.hasItem;

import org.mockito.MockitoAnnotations;
import org.pos.Application;
import org.pos.domain.logic.ItemCategory;
import org.pos.repository.logic.ItemCategoryRepository;
import org.pos.web.rest.logic.ItemCategoryResource;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ItemCategoryResource REST controller.
 *
 * @see ItemCategoryResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ItemCategoryResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";

    @Inject
    private ItemCategoryRepository itemCategoryRepository;

    private MockMvc restItemCategoryMockMvc;

    private ItemCategory itemCategory;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ItemCategoryResource itemCategoryResource = new ItemCategoryResource();
        ReflectionTestUtils.setField(itemCategoryResource, "itemCategoryRepository", itemCategoryRepository);
        this.restItemCategoryMockMvc = MockMvcBuilders.standaloneSetup(itemCategoryResource).build();
    }

    @Before
    public void initTest() {
        itemCategory = new ItemCategory();
        itemCategory.setName(DEFAULT_NAME);
        itemCategory.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createItemCategory() throws Exception {
        int databaseSizeBeforeCreate = itemCategoryRepository.findAll().size();

        // Create the ItemCategory
        restItemCategoryMockMvc.perform(post("/api/itemCategorys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(itemCategory)))
                .andExpect(status().isCreated());

        // Validate the ItemCategory in the database
        List<ItemCategory> itemCategorys = itemCategoryRepository.findAll();
        assertThat(itemCategorys).hasSize(databaseSizeBeforeCreate + 1);
        ItemCategory testItemCategory = itemCategorys.get(itemCategorys.size() - 1);
        assertThat(testItemCategory.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testItemCategory.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(itemCategoryRepository.findAll()).hasSize(0);
        // set the field null
        itemCategory.setName(null);

        // Create the ItemCategory, which fails.
        restItemCategoryMockMvc.perform(post("/api/itemCategorys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(itemCategory)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<ItemCategory> itemCategorys = itemCategoryRepository.findAll();
        assertThat(itemCategorys).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllItemCategorys() throws Exception {
        // Initialize the database
        itemCategoryRepository.saveAndFlush(itemCategory);

        // Get all the itemCategorys
        restItemCategoryMockMvc.perform(get("/api/itemCategorys"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(itemCategory.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getItemCategory() throws Exception {
        // Initialize the database
        itemCategoryRepository.saveAndFlush(itemCategory);

        // Get the itemCategory
        restItemCategoryMockMvc.perform(get("/api/itemCategorys/{id}", itemCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(itemCategory.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingItemCategory() throws Exception {
        // Get the itemCategory
        restItemCategoryMockMvc.perform(get("/api/itemCategorys/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateItemCategory() throws Exception {
        // Initialize the database
        itemCategoryRepository.saveAndFlush(itemCategory);
		
		int databaseSizeBeforeUpdate = itemCategoryRepository.findAll().size();

        // Update the itemCategory
        itemCategory.setName(UPDATED_NAME);
        itemCategory.setDescription(UPDATED_DESCRIPTION);
        restItemCategoryMockMvc.perform(put("/api/itemCategorys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(itemCategory)))
                .andExpect(status().isOk());

        // Validate the ItemCategory in the database
        List<ItemCategory> itemCategorys = itemCategoryRepository.findAll();
        assertThat(itemCategorys).hasSize(databaseSizeBeforeUpdate);
        ItemCategory testItemCategory = itemCategorys.get(itemCategorys.size() - 1);
        assertThat(testItemCategory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testItemCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void deleteItemCategory() throws Exception {
        // Initialize the database
        itemCategoryRepository.saveAndFlush(itemCategory);
		
		int databaseSizeBeforeDelete = itemCategoryRepository.findAll().size();

        // Get the itemCategory
        restItemCategoryMockMvc.perform(delete("/api/itemCategorys/{id}", itemCategory.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ItemCategory> itemCategorys = itemCategoryRepository.findAll();
        assertThat(itemCategorys).hasSize(databaseSizeBeforeDelete - 1);
    }
}
