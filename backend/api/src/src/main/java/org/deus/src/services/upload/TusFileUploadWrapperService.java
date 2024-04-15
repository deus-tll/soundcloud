package org.deus.src.services.upload;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;
import org.deus.src.SrcApplication;
import org.deus.src.config.AppProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class TusFileUploadWrapperService {
    private final TusFileUploadService tusFileUploadService;
    private final Path tusUploadDirectory;

    public TusFileUploadWrapperService(TusFileUploadService tusFileUploadService, AppProperties appProperties) {
        this.tusFileUploadService = tusFileUploadService;
        this.tusUploadDirectory = Path.of(appProperties.getTusUploadDirectory());
    }

    public void processRequest(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        this.tusFileUploadService.process(servletRequest, servletResponse);
    }

    public Optional<UploadInfo> getUploadInfo(String uploadURI) {
        UploadInfo uploadInfo = null;
        try {
            uploadInfo = this.tusFileUploadService.getUploadInfo(uploadURI);
        } catch (IOException | TusException e) {
            SrcApplication.logger.error("Error while getting UploadInfo", e);
        }
        return Optional.ofNullable(uploadInfo);
    }

    public void cleanup() {
        Path locksDir = this.tusUploadDirectory.resolve("locks");
        if (Files.exists(locksDir)) {
            try {
                this.tusFileUploadService.cleanup();
            } catch (IOException e) {
                SrcApplication.logger.error("Error during cleanup", e);
            }
        }
    }
}
