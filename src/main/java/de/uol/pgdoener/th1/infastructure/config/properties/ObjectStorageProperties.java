package de.uol.pgdoener.th1.infastructure.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "th1.objectstorage")
public class ObjectStorageProperties {

    /**
     * The type of object storage to be used.
     */
    private ObjectStorageType type = ObjectStorageType.NONE;

    private S3 s3 = new S3();

    @Data
    public static class S3 {

        /**
         * The URL of the S3 service.
         */
        private String url;
        /**
         * The access key for the S3 service.
         */
        private String accessKey;
        /**
         * The secret key for the S3 service.
         */
        private String secretKey;
        /**
         * The region for the S3 service.
         */
        private String region = "garage";

        private Bucket bucket = new Bucket();

        @Data
        public static class Bucket {
            /**
             * The name of the bucket.
             */
            private String name = "th1-bucket";

        }

    }

    public enum ObjectStorageType {
        NONE, S3
    }

}
