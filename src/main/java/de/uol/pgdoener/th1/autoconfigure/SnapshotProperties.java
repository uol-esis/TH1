package de.uol.pgdoener.th1.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "th1.snapshot")
public class SnapshotProperties {

    private String dir;

}
