package org.deus.api.controllers;

import org.deus.api.ApiApplication;
import org.deus.api.enums.FileType;
import org.deus.api.services.storages.StorageTempService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;

@RestController
@AllArgsConstructor
@RequestMapping("/api/upload")
public class UploadController {
    private final TusFileUploadService tusFileUploadService;
    private final StorageTempService storageTempService;
    private final String[] REQUIRED_METADATA_KEYS = {"uploadingFileType", "fileId"};

    @RequestMapping(
            value = "/file",
            method = {
                    RequestMethod.POST, RequestMethod.PATCH,
                    RequestMethod.HEAD, RequestMethod.DELETE,
                    RequestMethod.GET
            })
    public void upload(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws  IOException {
        this.tusFileUploadService.process(servletRequest, servletResponse);

        String uploadURI = servletRequest.getRequestURI();

        UploadInfo uploadInfo = null;

        try {
            uploadInfo = this.tusFileUploadService.getUploadInfo(uploadURI);
        } catch (IOException | TusException e) {
            ApiApplication.logger.error("Get upload info", e);
        }

        if (uploadInfo == null) {
            return;
        }

        Map<String, String> metadata = uploadInfo.getMetadata();

        if (!checkMetadata(metadata, servletResponse, REQUIRED_METADATA_KEYS)) {
            return;
        }

        if (!uploadInfo.isUploadInProgress()) {
            FileType fileType = determineFileType(uploadInfo.getFileMimeType());
            this.storageTempService.putContent(uploadURI, metadata, fileType);
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
}