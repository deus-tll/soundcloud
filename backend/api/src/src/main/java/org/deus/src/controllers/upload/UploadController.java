package org.deus.src.controllers.upload;

import lombok.RequiredArgsConstructor;
import org.deus.src.exceptions.StatusException;
import org.deus.src.services.UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/upload")
public class UploadController {
    private final UploadService uploadService;
    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    @RequestMapping(
            value = {"/file", "/file/**"},
            method = {
                    RequestMethod.POST, RequestMethod.PATCH,
                    RequestMethod.HEAD, RequestMethod.DELETE,
                    RequestMethod.GET
            })
    public void upload(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        logger.info("UPLOAD!!!! - PacketUpload Start");
        this.uploadService.processUpload(servletRequest, servletResponse);
        logger.info("UPLOAD!!!! - PacketUpload Finish");
    }

    @PostMapping("/request")
    public ResponseEntity<Map<String, String>> requestUpload() {
        String fileId = UUID.randomUUID().toString();
        Map<String, String> response = new HashMap<>();
        response.put("fileId", fileId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/check-file/{fileId}")
    public ResponseEntity<Boolean> checkFile(@PathVariable String fileId) throws StatusException {
        Boolean isFileExists = uploadService.checkFile(fileId);
        return new ResponseEntity<>(isFileExists, HttpStatus.OK);
    }

    @Scheduled(fixedDelayString = "PT24H")
    protected void cleanup() {
        this.uploadService.cleanup();
    }
}