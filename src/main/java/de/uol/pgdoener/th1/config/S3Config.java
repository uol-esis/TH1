package de.uol.pgdoener.th1.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    @Value("${th1.s3.url}")
    private String url;
    @Value("${th1.s3.access-key}")
    private String accessKey;
    @Value("${th1.s3.secret-key}")
    private String secretKey;
    @Value("${th1.s3.region}")
    private String region;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .region(region)
                .build();
    }
}
