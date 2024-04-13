package org.deus.soundcloudspringbootstarter.config.storage;

import io.minio.MinioClient;
import org.deus.soundcloudspringbootstarter.storages.drivers.StorageDriverInterface;
import org.deus.soundcloudspringbootstarter.storages.drivers.StorageMinioDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfiguration {

    @Value("${storage.minio.endpoint}")
    private String endpoint;

    @Value("${storage.minio.access.key}")
    private String accessKey;

    @Value("${storage.minio.secret.key}")
    private String secretKey;

    @Bean
    public StorageDriverInterface StorageService() {
        MinioClient client =
                MinioClient.builder()
                        .endpoint(endpoint)
                        .credentials(accessKey, secretKey)
                        .build();

        return new StorageMinioDriver(client);
    }
}
