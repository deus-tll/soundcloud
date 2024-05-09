package org.deus.src.controllers;

import lombok.AllArgsConstructor;
import org.deus.src.exceptions.data.DataNotFoundException;
import org.deus.src.exceptions.data.DataProcessingException;
import org.deus.src.models.auth.UserModel;
import org.deus.src.services.RabbitMQService;
import org.deus.src.services.TusFileUploadWrapperService;
import org.deus.src.services.auth.UserService;

import org.deus.src.services.storage.StorageTempService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import me.desair.tus.server.upload.UploadInfo;

@RestController
@AllArgsConstructor
@RequestMapping("/api/upload")
public class UploadController {
    private final TusFileUploadWrapperService tusFileUploadWrapperService;
    private final StorageTempService storageTempService;
    private final UserService userService;
    private final RabbitMQService rabbitMQService;
    private final String[] REQUIRED_METADATA_KEYS = {"fileId"};
    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    @RequestMapping(
            value = "/file",
            method = {
                    RequestMethod.POST, RequestMethod.PATCH,
                    RequestMethod.HEAD, RequestMethod.DELETE,
                    RequestMethod.GET
            })
    public void upload(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {

        this.tusFileUploadWrapperService.processRequest(servletRequest, servletResponse);

        String uploadURI = servletRequest.getRequestURI();

        Optional<UploadInfo> uploadInfoObject = this.tusFileUploadWrapperService.getUploadInfo(uploadURI);

        if (uploadInfoObject.isEmpty()) {
            logger.error("Couldn't get UploadInfo due to some problems");
            return;
        }

        UploadInfo uploadInfo = uploadInfoObject.get();

        Map<String, String> metadata = uploadInfo.getMetadata();

        if (!checkMetadata(metadata, servletResponse, REQUIRED_METADATA_KEYS)) {
            logger.error("User didn't provide necessary metadata");
            return;
        }

        if (!uploadInfo.isUploadInProgress()) {
            CompletableFuture.runAsync(() -> {
                UserModel user = userService.getCurrentUser();

                try {
                    this.storageTempService.putContent(user.getId(), uploadURI, metadata);

                    this.rabbitMQService.sendWebsocketMessageDTO(
                            "websocket.message.send",
                            user.getUsername(),
                            "/topic/file.upload.ready." + metadata.get("fileId"),
                            "Your avatar is ready!",
                            null);
                }
                catch (DataNotFoundException | DataProcessingException e) {
                    logger.error("Error during storing file upload with URI \"" + uploadURI + "\", for user with id \"" + user.getId() + "\" and metadata: [" + metadata.toString() + "]", e);
                }
            });
        }
    }

    @PostMapping("/request")
    public ResponseEntity<String> requestUpload() {
        String fileId = UUID.randomUUID().toString();
        return new ResponseEntity<>(fileId, HttpStatus.CREATED);
    }

    @GetMapping("/check-file/{fileId}")
    public ResponseEntity<?> checkFile(@PathVariable String fileId) {
        boolean isFileExists = false;

        try {
            isFileExists = this.storageTempService.isFileExists(userService.getCurrentUser().getId(), fileId);
        } catch (Exception e) {
            logger.error("User was trying to check if file exists in storage and error occurred");
        }

        return new ResponseEntity<>(isFileExists, HttpStatus.OK);
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

    @Scheduled(fixedDelayString = "PT24H")
    private void cleanup() {
        this.tusFileUploadWrapperService.cleanup();
    }
}