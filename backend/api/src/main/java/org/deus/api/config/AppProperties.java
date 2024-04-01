package org.deus.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties(prefix = "app")
@Component
@Data
public class AppProperties {
    private String tusUploadDirectory;

    private String appUploadDirectory;
}
