package org.deus.soundcloudspringbootstarter.config.upload;

import me.desair.tus.server.TusFileUploadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(TusProperties.class)
@PropertySource(value = "classpath:custom.properties")
public class TusFileUploadConfiguration {

    private final TusProperties tusProperties;

    @Autowired
    public TusFileUploadConfiguration(TusProperties tusProperties) {
        this.tusProperties = tusProperties;
    }

    @Bean
    public TusFileUploadService tusFileUploadService() {
        return new TusFileUploadService().withStoragePath(tusProperties.getUploadDirectory())
                .withUploadUri("/api/upload/file");
    }
}
