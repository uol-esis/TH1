package de.uol.pgdoener.th1.config;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.swagger.v3.core.util.Json31;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerJacksonConfig {

    @PostConstruct
    public void init() {
        Json31.mapper().registerModule(new Jdk8Module());
    }

}
