package de.uol.pgdoener.th1.config;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerJacksonConfig {

    private final ObjectMapperProvider objectMapperProvider;

    @PostConstruct
    public void init() {
        objectMapperProvider.jsonMapper().registerModule(new Jdk8Module());
    }

}
