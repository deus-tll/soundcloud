package org.deus.src.controllers;

import org.deus.src.SrcApplication;
import org.deus.src.config.AppProperties;
import org.deus.src.enums.FileType;
import org.deus.src.services.auth.UserService;
import org.deus.src.services.storages.StorageTempService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    private final TusFileUploadService tusFileUploadService;
    private final StorageTempService storageTempService;
    private final UserService userService;
    private final Path tusUploadDirectory;
    private final String[] REQUIRED_METADATA_KEYS = {"uploadingFileType", "fileId"};

    public UploadController(TusFileUploadService tusFileUploadService, StorageTempService storageTempService, UserService userService, AppProperties appProperties) {
        this.tusFileUploadService = tusFileUploadService;
        this.storageTempService = storageTempService;
        this.userService = userService;
        this.tusUploadDirectory = Paths.get(appProperties.getTusUploadDirectory());
    }

    @RequestMapping(
            value = "/file",
            method = {
                    RequestMethod.POST, RequestMethod.PATCH,
                    RequestMethod.HEAD, RequestMethod.DELETE,
                    RequestMethod.GET
            })
    public void upload(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        this.tusFileUploadService.process(servletRequest, servletResponse);

        String uploadURI = servletRequest.getRequestURI();

        UploadInfo uploadInfo = null;

        try {
            uploadInfo = this.tusFileUploadService.getUploadInfo(uploadURI);
        } catch (IOException | TusException e) {
            SrcApplication.logger.error("Get upload info", e);
        }

        if (uploadInfo == null) {
            return;
        }

        Map<String, String> metadata = uploadInfo.getMetadata();

        if (!checkMetadata(metadata, servletResponse, REQUIRED_METADATA_KEYS)) {
            return;
        }

        if (!uploadInfo.isUploadInProgress()) {
            CompletableFuture.runAsync(() -> {
                try {
                    this.storageTempService.putContent(userService.getCurrentUser().getId(), userService.getCurrentUser().getUsername(), uploadURI, metadata);
                } catch (Exception e) {
                    SrcApplication.logger.error("Error during file upload", e);
                }
            });
        }
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestUpload() {
        String fileId = UUID.randomUUID().toString();
        return new ResponseEntity<>(fileId, HttpStatus.CREATED);
    }

    private boolean checkMetadata(Map<String, String> metadata, HttpServletResponse servletResponse, String... requiredKeys) throws IOException {
        for (String key : requiredKeys) {
            if (!metadata.containsKey(key)) {
                // Generating an error message
                String errorMessage = "You need to provide '" + key + "' inside metadata.";

                // Setting the HTTP status code for a client error (400 - Bad Request)
                servletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                // Sending a response with an error message
                servletResponse.getWriter().println(errorMessage);
                servletResponse.getWriter().flush();

                return false;
            }
        }
        return true;
    }

    private FileType determineFileType(String mimeType) {
        if (mimeType != null) {
            if (mimeType.startsWith("audio/")) {
                return FileType.AUDIO;
            } else if (mimeType.startsWith("video/")) {
                return FileType.VIDEO;
            }
        }
        return FileType.UNKNOWN;
    }

    @Scheduled(fixedDelayString = "PT24H")
    private void cleanup() {
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