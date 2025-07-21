package de.uol.pgdoener.th1.infastructure.storage.impl;

import de.uol.pgdoener.th1.infastructure.config.properties.ObjectStorageProperties;
import de.uol.pgdoener.th1.infastructure.storage.StorageService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

/**
 * @see <a href=https://gurselgazii.medium.com/integrating-minio-with-spring-boot-a-guide-to-simplified-object-storage-525d5a7686cc>medium.com</a>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "th1.objectstorage.type", havingValue = "s3")
public class S3StorageService implements StorageService {

    private final ObjectStorageProperties objectStorageProperties;


    private final MinioClient minioClient;

    @Override
    public Optional<UUID> store(InputStream inputStream) {
        UUID objectID = UUID.randomUUID();
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(objectStorageProperties.getS3().getBucket().getName())
                    .object(objectID.toString())
                    .stream(inputStream, inputStream.available(), -1)
                    .build());
            log.debug("Stored object in objectStorage");
            return Optional.of(objectID);
        } catch (Exception e) {
            log.warn("Upload failed", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<InputStream> load(UUID objectID) {
        try {
            var response = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(objectStorageProperties.getS3().getBucket().getName())
                    .object(objectID.toString())
                    .build());
            return Optional.of(response);
        } catch (Exception e) {
            log.warn("Download failed", e);
            return Optional.empty();
        }
    }


    @PostConstruct
    private void ensureBucketExistence() {
        log.warn("Bucket existence not ensured yet!");
        // FIXME implement this
    }
}
