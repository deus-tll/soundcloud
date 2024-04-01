package org.deus.api.config.upload;

import me.desair.tus.server.TusFileUploadService;
import org.deus.api.config.AppProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TusFileUploadConfiguration {
    @Bean
    public TusFileUploadService tusFileUploadService(AppProperties appProperties) {
        return new TusFileUploadService().withStoragePath(appProperties.getTusUploadDirectory())
                .withUploadUri("/api/upload/file");
    }
}
