package org.deus.tusuploadfilestarter.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;
import org.deus.tusuploadfilestarter.config.TusProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
@Component
public class TusFileUploadWrapperService {
    private final TusFileUploadService tusFileUploadService;
    private final Path tusUploadDirectory;
    private static final Logger logger = LoggerFactory.getLogger(TusFileUploadWrapperService.class);

    public TusFileUploadWrapperService(TusFileUploadService tusFileUploadService, TusProperties tusProperties) {
        this.tusFileUploadService = tusFileUploadService;
        this.tusUploadDirectory = Path.of(tusProperties.getUploadDirectory());
    }

    public void processRequest(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        this.tusFileUploadService.process(servletRequest, servletResponse);
    }

    public Optional<UploadInfo> getUploadInfo(String uploadURI) {
        UploadInfo uploadInfo = null;
        try {
            uploadInfo = this.tusFileUploadService.getUploadInfo(uploadURI);
        } catch (IOException | TusException e) {
            logger.error("Error while getting UploadInfo", e);
        }
        return Optional.ofNullable(uploadInfo);
    }

    public Optional<InputStream> getUploadedBytes(String uploadURI) {
        InputStream inputStream = null;
        try {
            inputStream = this.tusFileUploadService.getUploadedBytes(uploadURI);
        }
        catch (IOException | TusException e) {
            logger.error("Error while deleting uploaded data", e);
        }

        return Optional.ofNullable(inputStream);
    }

    public void deleteUpload(String uploadURI) {
        try {
            this.tusFileUploadService.deleteUpload(uploadURI);
        } catch (IOException | TusException e) {
            logger.error("Error while deleting uploaded data", e);
        }
    }

    public void cleanup() {
        Path locksDir = this.tusUploadDirectory.resolve("locks");
        if (Files.exists(locksDir)) {
            try {
                this.tusFileUploadService.cleanup();
            } catch (IOException e) {
                logger.error("Error during cleanup", e);
            }
        }
    }
}