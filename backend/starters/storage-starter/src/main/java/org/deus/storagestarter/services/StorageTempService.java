package org.deus.storagestarter.services;

import org.deus.storagestarter.drivers.StorageDriverInterface;
import org.deus.storagestarter.exceptions.StorageException;

import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StorageTempService {
    private final StorageDriverInterface storage;

    // doesn't work because it can't import from starter
    private final TusFileUploadWrapperService tusFileUploadWrapperService ;
    private static final Logger logger = LoggerFactory.getLogger(StorageTempService.class);
    private final String tempBucketName = "temp_files";

    private String buildPath(long userId, String fileId) {
        return "/" + userId + "/" + fileId + "/originalBytes";
    }

    public void putContent(long userId, String username, String uploadURI, Map<String, String> metadata) {
        try (InputStream inputStream = this.tusFileUploadWrapperService.getUploadedBytes(uploadURI)) {
            String fileId = metadata.get("fileId");
            byte[] fileBytes = inputStream.readAllBytes();

            storage.put(tempBucketName, buildPath(userId, fileId), fileBytes);

            this.tusFileUploadWrapperService.deleteUpload(uploadURI);
        }
        catch (StorageException | IOException e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
    }

    public byte[] getOriginalBytes(long userId, String fileId) {
        try {
            return storage.getBytes(tempBucketName, buildPath(userId, fileId));
        }
        catch (StorageException e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
    }

    public boolean isFileExists(long userId, String fileId) {

        return false;
    }
}