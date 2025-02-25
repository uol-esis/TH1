package de.uol.pgdoener.th1.api;

import de.uol.pgdoener.th1.data.repository.TableStructureRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("integrationTest")
public class TableStructuresApiTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void beforeEach() {
       tableStructureRepository.deleteAll();
    }

    @Autowired
    private MockMvc mockMvc;

    @Value("classpath:integrationTests/tableStructure.json")
    Resource tableStructure;

    @Autowired
    TableStructureRepository tableStructureRepository;

    @Test
    void createTableStructureEndpoint() throws Exception {

        // load from resources
        String tableStructureJson = tableStructure.getContentAsString(StandardCharsets.UTF_8);

        mockMvc.perform(post("/api/v1/table-structures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tableStructureJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Assertions.assertEquals(1, tableStructureRepository.count());

    }

    @Test
    void createTableStructureEndpointSameIDConflict() throws Exception {

        // load from resources
        String tableStructureJson = tableStructure.getContentAsString(StandardCharsets.UTF_8);

        mockMvc.perform(post("/api/v1/table-structures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tableStructureJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/table-structures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tableStructureJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        Assertions.assertEquals(1, tableStructureRepository.count());

    }

}
