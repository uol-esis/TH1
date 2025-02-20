package de.uol.pgdoener.th1.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class TableStructuresApiIT {

    @Autowired
    private MockMvc mockMvc;

    @Value("classpath:integrationTests/tableStructure.json")
    Resource tableStructure;

    @Test
    void createTableStructureEndpoint() throws Exception {

        // load from resources
        String tableStructureJson = tableStructure.getContentAsString(StandardCharsets.UTF_8);

        ObjectMapper mapper = new ObjectMapper();
        mockMvc.perform(post("/api/v1/table-structures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tableStructureJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("statusCode" , is(201)))
                .andExpect(jsonPath("message.usersId", is(5)));



    }

}
