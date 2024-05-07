package org.deus.src.config.tus;

import me.desair.tus.server.TusFileUploadService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TusProperties.class)
public class TusFileUploadConfiguration {
    @Bean
    public TusFileUploadService tusFileUploadService(TusProperties tusProperties) {
        return new TusFileUploadService().withStoragePath(tusProperties.getUploadDirectory())
                .withUploadUri("/api/upload/file");
    }
}