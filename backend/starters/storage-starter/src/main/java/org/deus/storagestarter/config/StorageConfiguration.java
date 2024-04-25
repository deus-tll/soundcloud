package org.deus.storagestarter.config;

import io.minio.MinioClient;
import org.deus.storagestarter.config.properties.MinioStorageProperties;
import org.deus.storagestarter.config.properties.S3StorageProperties;
import org.deus.storagestarter.drivers.StorageDriverInterface;
import org.deus.storagestarter.drivers.StorageMinioDriver;
import org.deus.storagestarter.enums.StorageEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties({MinioStorageProperties.class, S3StorageProperties.class})
@PropertySource(value = "classpath:custom.properties")
public class StorageConfiguration {

    private final MinioStorageProperties minioStorageProperties;
    private final S3StorageProperties s3StorageProperties;

    @Autowired
    public StorageConfiguration(MinioStorageProperties minioStorageProperties, S3StorageProperties s3StorageProperties) {
        this.minioStorageProperties = minioStorageProperties;
        this.s3StorageProperties = s3StorageProperties;
    }

    @Bean
    public StorageDriverInterface storageService() {
        StorageEnum currentMainStorage = StorageEnum.MINIO;
        StorageDriverInterface storageClient = null;

        switch (currentMainStorage) {
            case MINIO -> {
                MinioClient client =
                        MinioClient.builder()
                                .endpoint(minioStorageProperties.getEndpoint())
                                .credentials(minioStorageProperties.getAccessKey(), minioStorageProperties.getSecretKey())
                                .build();

                storageClient = new StorageMinioDriver(client);
            }

            case S3 -> {

            }
        }

        return storageClient;
    }
}
