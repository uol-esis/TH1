package de.uol.pgdoener.th1.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "th1.security")
public class SecurityProperties {

    private List<String> allowedOrigins;

    private String authorizationUrl;
    private String tokenUrl;

}
