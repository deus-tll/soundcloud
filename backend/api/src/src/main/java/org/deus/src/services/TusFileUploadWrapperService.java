package org.deus.src.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;
import org.deus.src.config.tus.TusProperties;
import org.deus.src.exceptions.data.DataDeletingException;
import org.deus.src.services.auth.UserService;
import org.deus.src.services.storage.StorageTempService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class TusFileUploadWrapperService {
    private final TusFileUploadService tusFileUploadService;
    private final Path tusUploadDirectory;
    private static final Logger logger = LoggerFactory.getLogger(TusFileUploadWrapperService.class);

    public TusFileUploadWrapperService(TusFileUploadService tusFileUploadService, StorageTempService storageTempService, UserService userService, RabbitMQService rabbitMQService, TusProperties tusProperties) {
        this.tusFileUploadService = tusFileUploadService;
        this.tusUploadDirectory = Path.of(tusProperties.getUploadDirectory());
    }

    public void processRequest(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        this.tusFileUploadService.process(servletRequest, servletResponse);
    }

    public Optional<UploadInfo> getUploadInfo(String uploadURI) {
        try {
            UploadInfo uploadInfo = this.tusFileUploadService.getUploadInfo(uploadURI);
            return Optional.ofNullable(uploadInfo);
        } catch (IOException | TusException e) {
            logger.error("Error while getting UploadInfo", e);
            return Optional.empty();
        }
    }

    public Optional<InputStream> getUploadedBytes(String uploadURI) {
        try {
            InputStream inputStream = this.tusFileUploadService.getUploadedBytes(uploadURI);
            return Optional.ofNullable(inputStream);
        }
        catch (IOException | TusException e) {
            logger.error("Error while getting uploaded bytes from temporary directory of tus uploads. UploadURI: " + uploadURI, e);
            return Optional.empty();
        }
    }

    public void deleteUpload(String uploadURI) throws DataDeletingException {
        try {
            this.tusFileUploadService.deleteUpload(uploadURI);
        } catch (IOException | TusException e) {
            String errorMessage = "Error while deleting uploaded bytes from temporary directory of tus uploads. UploadURI: " + uploadURI;
            logger.error(errorMessage, e);
            throw new DataDeletingException(e);
        }
    }

    public void cleanup(){
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