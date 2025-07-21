package de.uol.pgdoener.th1.config;

import de.uol.pgdoener.th1.autoconfigure.SecurityProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;

/**
 * @see <a href="https://stackoverflow.com/a/77139978">stackoverflow</a>
 */
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
@Profile("prod")
@Configuration
public class OpenApiConfig {

    /**
     * This method is needed to allow sending multipart requests. For example, when an item is
     * created together with an image. If this is not set the request will return an exception with:
     * <p>
     * Resolved [org.springframework.web.HttpMediaTypeNotSupportedException: Content-Type
     * 'application/octet-stream' is not supported]
     *
     * @param converter
     * @param openAPI   partly configured openAPI config generated from the specs file.
     */
    public OpenApiConfig(MappingJackson2HttpMessageConverter converter, OpenAPI openAPI, SecurityProperties securityProperties) {
        var supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
        supportedMediaTypes.add(new MediaType("application", "octet-stream"));
        converter.setSupportedMediaTypes(supportedMediaTypes);

        OAuthFlow flow = new OAuthFlow();
        flow.authorizationUrl(securityProperties.getAuthorizationUrl());
        flow.refreshUrl(securityProperties.getTokenUrl());
        flow.tokenUrl(securityProperties.getTokenUrl());

        OAuthFlows flows = new OAuthFlows();
        flows.authorizationCode(flow);


        openAPI.getComponents().getSecuritySchemes().get("oAuth2Auth").flows(flows);
        log.info(openAPI.toString());
    }
}