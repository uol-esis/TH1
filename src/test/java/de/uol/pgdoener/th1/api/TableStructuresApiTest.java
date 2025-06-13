package de.uol.pgdoener.th1.api;

import de.uol.pgdoener.th1.data.repository.TableStructureRepository;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("integrationTest")
class TableStructuresApiTest {

    private static final String basePath = "/api/v1/table-structures";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");
    @Value("classpath:integrationTests/tableStructure.json")
    private Resource tableStructure;
    @Autowired
    private TableStructureRepository tableStructureRepository;
    @Autowired
    private MockMvc mockMvc;

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

    private SecurityMockMvcRequestPostProcessors.OAuth2LoginRequestPostProcessor authorizedLogin() {
        return oauth2Login()
                .authorities(
                        new SimpleGrantedAuthority("write:tablestructure"),
                        new SimpleGrantedAuthority("read:tablestructure"));
    }

    @Test
    void createTableStructureEndpoint() throws Exception {

        // load from resources
        String tableStructureJson = tableStructure.getContentAsString(StandardCharsets.UTF_8);

        mockMvc.perform(post(basePath)
                        .with(authorizedLogin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tableStructureJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(new BaseMatcher<>() {
                    @Override
                    public boolean matches(Object actual) {
                        if (actual instanceof String id) {
                            return tableStructureRepository.existsById(Long.parseLong(id));
                        }
                        return false;
                    }

                    @Override
                    public void describeTo(Description description) {
                        // irrelevant
                    }
                }));

        Assertions.assertEquals(1, tableStructureRepository.count());

    }

    @Test
    void createTableStructureEndpointSameIDConflict() throws Exception {

        // load from resources
        String tableStructureJson = tableStructure.getContentAsString(StandardCharsets.UTF_8);

        mockMvc.perform(post(basePath)
                        .with(authorizedLogin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tableStructureJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        mockMvc.perform(post(basePath)
                        .with(authorizedLogin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tableStructureJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        Assertions.assertEquals(1, tableStructureRepository.count());

    }

    @Test
    void getAllTableStructuresEndpoint() throws Exception {
        // upload test structure
        String tableStructureJson = tableStructure.getContentAsString(StandardCharsets.UTF_8);

        mockMvc.perform(post(basePath)
                .with(authorizedLogin())
                .contentType(MediaType.APPLICATION_JSON)
                .content(tableStructureJson)
                .accept(MediaType.APPLICATION_JSON));

        // begin test
        mockMvc.perform(get(basePath)
                        .with(authorizedLogin()))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteTableStructuresEndpoint() throws Exception {

        // load from resources
        String tableStructureJson = tableStructure.getContentAsString(StandardCharsets.UTF_8);

        mockMvc.perform(post(basePath)
                        .with(authorizedLogin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tableStructureJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Long id = tableStructureRepository.findAll().iterator().next().getId();

        mockMvc.perform(delete(basePath + "/" + id)
                        .with(authorizedLogin()))
                .andExpect(status().isNoContent());

        Assertions.assertEquals(0, tableStructureRepository.count());

    }

    @Test
    void deleteTableStructuresEndpointNonExistent() throws Exception {

        mockMvc.perform(delete(basePath + "/1")
                        .with(authorizedLogin()))
                .andExpect(status().isNotFound());

        Assertions.assertEquals(0, tableStructureRepository.count());

    }

    @Test
    void deleteTableStructuresEndpointUnauthorized() throws Exception {
        mockMvc.perform(delete(basePath + "/1"))
                .andExpect(status().isUnauthorized());
        Assertions.assertEquals(0, tableStructureRepository.count());
    }

}
