package org.deus.storagestarter.config;

import org.deus.storagestarter.drivers.StorageDriverInterface;
import org.deus.storagestarter.services.StorageAvatarService;
import org.deus.storagestarter.services.StorageTempService;
import org.deus.tusuploadfilestarter.services.TusFileUploadWrapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;

@AutoConfiguration
@ComponentScan(basePackageClasses = {TusFileUploadWrapperService.class})
public class StorageServicesAutoConfiguration {
    private final StorageDriverInterface storage;
    private final TusFileUploadWrapperService tusFileUploadWrapperService;

    @Autowired
    public StorageServicesAutoConfiguration(StorageDriverInterface storage, TusFileUploadWrapperService tusFileUploadWrapperService) {
        this.storage = storage;
        this.tusFileUploadWrapperService = tusFileUploadWrapperService;
    }

    @Bean
    @DependsOn("storageDriverInterface")
    public StorageTempService storageTempService() {
        return new StorageTempService(storage, tusFileUploadWrapperService);
    }

    @Bean
    @DependsOn("storageDriverInterface")
    public StorageAvatarService storageAvatarService() {
        return new StorageAvatarService(storage);
    }
}
