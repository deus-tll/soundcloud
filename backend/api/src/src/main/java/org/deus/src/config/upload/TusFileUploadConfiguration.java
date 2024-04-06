package org.deus.src.config.upload;

import org.deus.src.config.AppProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import me.desair.tus.server.TusFileUploadService;

@Configuration
public class TusFileUploadConfiguration {
    @Bean
    public TusFileUploadService tusFileUploadService(AppProperties appProperties) {
        return new TusFileUploadService().withStoragePath(appProperties.getTusUploadDirectory())
                .withUploadUri("/api/upload/file");
    }
}
