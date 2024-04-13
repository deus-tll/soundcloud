package org.deus.soundcloudspringbootstarter.config.storage.properties;

import lombok.Data;

@Data
public class BaseStorageProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
}
