package org.deus.src.config.storage;

import io.minio.MinioClient;
import org.deus.src.config.storage.properties.MinioStorageProperties;
import org.deus.src.config.storage.properties.S3StorageProperties;
import org.deus.src.drivers.StorageDriverInterface;
import org.deus.src.drivers.StorageMinioDriver;
import org.deus.src.enums.StorageEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfiguration {
    @Bean
    public StorageDriverInterface storageDriverInterface(MinioStorageProperties minioStorageProperties, S3StorageProperties s3StorageProperties) {
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
