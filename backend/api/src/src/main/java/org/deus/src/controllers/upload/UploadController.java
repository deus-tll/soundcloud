package org.deus.src.controllers.upload;

import lombok.RequiredArgsConstructor;
import org.deus.src.exceptions.StatusException;
import org.deus.src.services.UploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/upload")
public class UploadController {
    private final UploadService uploadService;

    @RequestMapping(
            value = "/file",
            method = {
                    RequestMethod.POST, RequestMethod.PATCH,
                    RequestMethod.HEAD, RequestMethod.DELETE,
                    RequestMethod.GET
            })
    public void upload(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        this.uploadService.processUpload(servletRequest, servletResponse);
    }

    @PostMapping("/request")
    public ResponseEntity<String> requestUpload() {
        String fileId = UUID.randomUUID().toString();
        return new ResponseEntity<>(fileId, HttpStatus.CREATED);
    }

    @GetMapping("/check-file/{fileId}")
    public ResponseEntity<?> checkFile(@PathVariable String fileId) throws StatusException {
        boolean isFileExists = uploadService.checkFile(fileId);
        return new ResponseEntity<>(isFileExists, HttpStatus.OK);
    }

    @Scheduled(fixedDelayString = "PT24H")
    protected void cleanup() {
        this.uploadService.cleanup();
    }
}