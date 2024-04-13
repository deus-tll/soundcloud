package org.deus.soundcloudspringbootstarter.config.upload;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app.tus")
@Component
@Data
public class TusProperties {
    private String uploadDirectory;
}
