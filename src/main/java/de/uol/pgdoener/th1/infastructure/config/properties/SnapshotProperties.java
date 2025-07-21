package de.uol.pgdoener.th1.infastructure.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "th1.snapshot")
public class SnapshotProperties {

    /**
     * The directory where the snapshots are stored.
     */
    private String dir;

}
