package org.deus.soundcloudspringbootstarter.config.storage;

import io.minio.MinioClient;
import org.deus.soundcloudspringbootstarter.config.storage.properties.MinioStorageProperties;
import org.deus.soundcloudspringbootstarter.config.storage.properties.S3StorageProperties;
import org.deus.soundcloudspringbootstarter.enums.StorageEnum;
import org.deus.soundcloudspringbootstarter.storages.drivers.StorageDriverInterface;
import org.deus.soundcloudspringbootstarter.storages.drivers.StorageMinioDriver;
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
    public StorageConfiguration (MinioStorageProperties minioStorageProperties, S3StorageProperties s3StorageProperties) {
        this.minioStorageProperties = minioStorageProperties;
        this.s3StorageProperties = s3StorageProperties;
    }

    @Bean
    public StorageDriverInterface StorageService() {
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
