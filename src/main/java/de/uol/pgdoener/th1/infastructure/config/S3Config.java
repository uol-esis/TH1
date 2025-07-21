package de.uol.pgdoener.th1.infastructure.config;

import de.uol.pgdoener.th1.infastructure.config.properties.ObjectStorageProperties;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(value = "th1.objectstorage.type", havingValue = "s3")
public class S3Config {

    private final ObjectStorageProperties osProperties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(osProperties.getS3().getUrl())
                .credentials(osProperties.getS3().getAccessKey(), osProperties.getS3().getSecretKey())
                .region(osProperties.getS3().getRegion())
                .build();
    }
}
