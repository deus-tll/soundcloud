package org.deus.src.services.storages;

import org.deus.src.SrcApplication;
import org.deus.src.storages.drivers.StorageDriverInterface;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;

import io.minio.errors.*;

@Service
@AllArgsConstructor
public class StorageTempService {
    private final StorageDriverInterface storage;
    private final TusFileUploadService tusFileUploadService;
    private final String tempBucketName = "temp_files";

    private String buildPath(long userId, String fileId) {
        return "/" + userId + "/" + fileId + "/originalBytes";
    }

    public void putContent(long userId, String uploadURI, Map<String, String> metadata) {
        try (InputStream inputStream = this.tusFileUploadService.getUploadedBytes(uploadURI)) {
            String fileId = metadata.get("fileId");
            byte[] fileBytes = inputStream.readAllBytes();
            storage.put(tempBucketName, buildPath(userId, fileId), fileBytes);
        }
        catch (IOException | TusException | ServerException | InsufficientDataException | ErrorResponseException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            SrcApplication.logger.error("Error while getting uploaded bytes", e);
        }

        try {
            this.tusFileUploadService.deleteUpload(uploadURI);
        } catch (IOException | TusException e) {
            SrcApplication.logger.error("Error while deleting uploaded data", e);
        }
    }

    public byte[] getOriginalBytes(long userId, String fileId) {
        try {
            return storage.getBytes(tempBucketName, buildPath(userId, fileId));
        }
        catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            SrcApplication.logger.error("Error while deleting uploaded data", e);
            throw new RuntimeException(e);
        }
    }
}