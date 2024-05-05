package org.deus.tusuploadfilestarter.config;

import me.desair.tus.server.TusFileUploadService;
import org.deus.tusuploadfilestarter.services.TusFileUploadWrapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class TusFileUploadWrapperAutoConfiguration {
    private final TusFileUploadService tusFileUploadService;
    private final String uploadDirectory;

    @Autowired
    public TusFileUploadWrapperAutoConfiguration(TusFileUploadService tusFileUploadService, String uploadDirectory) {
        this.tusFileUploadService = tusFileUploadService;
        this.uploadDirectory = uploadDirectory;
    }

    @Bean
    public TusFileUploadWrapperService tusFileUploadWrapperService() {
        return new TusFileUploadWrapperService(tusFileUploadService, uploadDirectory);
    }
}
