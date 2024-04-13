package org.deus.soundcloudspringbootstarter.config.storage.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "storage.minio")
@Component
@EqualsAndHashCode(callSuper = true)
public class MinioStorageProperties extends BaseStorageProperties{
}
