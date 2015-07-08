package org.pos.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.hasItem;

import org.mockito.MockitoAnnotations;
import org.pos.Application;
import org.pos.domain.logic.TableNo;
import org.pos.repository.logic.TableNoRepository;
import org.pos.web.rest.logic.TableNoResource;
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
 * Test class for the TableNoResource REST controller.
 *
 * @see TableNoResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TableNoResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";

    @Inject
    private TableNoRepository tableNoRepository;

    private MockMvc restTableNoMockMvc;

    private TableNo tableNo;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TableNoResource tableNoResource = new TableNoResource();
        ReflectionTestUtils.setField(tableNoResource, "tableNoRepository", tableNoRepository);
        this.restTableNoMockMvc = MockMvcBuilders.standaloneSetup(tableNoResource).build();
    }

    @Before
    public void initTest() {
        tableNo = new TableNo();
        tableNo.setName(DEFAULT_NAME);
        tableNo.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createTableNo() throws Exception {
        int databaseSizeBeforeCreate = tableNoRepository.findAll().size();

        // Create the TableNo
        restTableNoMockMvc.perform(post("/api/tableNos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tableNo)))
                .andExpect(status().isCreated());

        // Validate the TableNo in the database
        List<TableNo> tableNos = tableNoRepository.findAll();
        assertThat(tableNos).hasSize(databaseSizeBeforeCreate + 1);
        TableNo testTableNo = tableNos.get(tableNos.size() - 1);
        assertThat(testTableNo.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTableNo.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(tableNoRepository.findAll()).hasSize(0);
        // set the field null
        tableNo.setName(null);

        // Create the TableNo, which fails.
        restTableNoMockMvc.perform(post("/api/tableNos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tableNo)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<TableNo> tableNos = tableNoRepository.findAll();
        assertThat(tableNos).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllTableNos() throws Exception {
        // Initialize the database
        tableNoRepository.saveAndFlush(tableNo);

        // Get all the tableNos
        restTableNoMockMvc.perform(get("/api/tableNos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(tableNo.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getTableNo() throws Exception {
        // Initialize the database
        tableNoRepository.saveAndFlush(tableNo);

        // Get the tableNo
        restTableNoMockMvc.perform(get("/api/tableNos/{id}", tableNo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(tableNo.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTableNo() throws Exception {
        // Get the tableNo
        restTableNoMockMvc.perform(get("/api/tableNos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTableNo() throws Exception {
        // Initialize the database
        tableNoRepository.saveAndFlush(tableNo);
		
		int databaseSizeBeforeUpdate = tableNoRepository.findAll().size();

        // Update the tableNo
        tableNo.setName(UPDATED_NAME);
        tableNo.setDescription(UPDATED_DESCRIPTION);
        restTableNoMockMvc.perform(put("/api/tableNos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(tableNo)))
                .andExpect(status().isOk());

        // Validate the TableNo in the database
        List<TableNo> tableNos = tableNoRepository.findAll();
        assertThat(tableNos).hasSize(databaseSizeBeforeUpdate);
        TableNo testTableNo = tableNos.get(tableNos.size() - 1);
        assertThat(testTableNo.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTableNo.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void deleteTableNo() throws Exception {
        // Initialize the database
        tableNoRepository.saveAndFlush(tableNo);
		
		int databaseSizeBeforeDelete = tableNoRepository.findAll().size();

        // Get the tableNo
        restTableNoMockMvc.perform(delete("/api/tableNos/{id}", tableNo.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<TableNo> tableNos = tableNoRepository.findAll();
        assertThat(tableNos).hasSize(databaseSizeBeforeDelete - 1);
    }
}
