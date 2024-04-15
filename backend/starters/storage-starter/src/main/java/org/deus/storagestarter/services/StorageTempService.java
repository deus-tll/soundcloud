package org.deus.storagestarter.services;

import io.minio.errors.*;
import lombok.AllArgsConstructor;

import org.deus.storagestarter.drivers.StorageDriverInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

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
        }
        catch (IOException | ServerException | InsufficientDataException | ErrorResponseException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            logger.error("", e);
        }

        try {
            this.tusFileUploadWrapperService.deleteUpload(uploadURI);
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    public byte[] getOriginalBytes(long userId, String fileId) {
        try {
            return storage.getBytes(tempBucketName, buildPath(userId, fileId));
        }
        catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
    }

    public boolean isFileExists(long userId, String fileId) {

        return false;
    }
}