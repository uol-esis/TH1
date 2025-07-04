package de.uol.pgdoener.th1.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "th1.mb")
public class MetabaseProperties {

    private String key;
    private String generalKey;
    private String basePath;

}
