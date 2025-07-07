package de.uol.pgdoener.th1.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "th1.security")
public class SecurityProperties {

    /**
     * Allowed origins for CORS.
     * example for keycloak would be: http://localhost:8080
     */
    private List<String> allowedOrigins;

    /**
     * Authorization URL for OAuth2.
     * example for keycloak would be: http://localhost:8080/realms/example/protocol/openid-connect/auth
     */
    private String authorizationUrl;
    /**
     * Token URL for OAuth2.
     * example for keycloak would be: http://localhost:8080/realms/example/protocol/openid-connect/token
     */
    private String tokenUrl;

}
