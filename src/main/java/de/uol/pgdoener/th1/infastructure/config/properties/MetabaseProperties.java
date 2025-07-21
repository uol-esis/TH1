package de.uol.pgdoener.th1.infastructure.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "th1.mb")
public class MetabaseProperties {

    /**
     * The base path of the Metabase instance.
     * For example: "http://localhost:3000"
     */
    private String basePath;

    private Api api = new Api();

    @Data
    public static class Api {
        /**
         * The API key used to notify Metabase to update its cache.
         * It can be set in Metabase using the "MB_API_KEY" environment variable.
         */
        private String key;
        /**
         * This key is used for all other API calls.
         * This key has to be created manually in Metabase.
         * See <a href="https://www.metabase.com/docs/latest/people-and-groups/api-keys#create-an-api-key">here</a> for more information.
         */
        private String generalKey;
    }

}
