package org.deus.tusuploadfilestarter.config;

import me.desair.tus.server.TusFileUploadService;
import org.deus.tusuploadfilestarter.services.TusFileUploadWrapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(TusProperties.class)
@PropertySource(value = "classpath:custom.properties")
public class TusFileUploadWrapperAutoConfiguration {
    private final TusFileUploadService tusFileUploadService;
    private final TusProperties tusProperties;

    @Autowired
    public TusFileUploadWrapperAutoConfiguration(TusFileUploadService tusFileUploadService, TusProperties tusProperties) {
        this.tusFileUploadService = tusFileUploadService;
        this.tusProperties = tusProperties;
    }

    @Bean
    public TusFileUploadWrapperService tusFileUploadWrapperService() {
        return new TusFileUploadWrapperService(tusFileUploadService, tusProperties);
    }
}
