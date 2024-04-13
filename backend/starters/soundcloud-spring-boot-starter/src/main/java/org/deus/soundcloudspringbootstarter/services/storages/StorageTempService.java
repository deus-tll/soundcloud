package org.deus.soundcloudspringbootstarter.services.storages;

import io.minio.errors.*;
import lombok.AllArgsConstructor;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import org.deus.soundcloudspringbootstarter.storages.drivers.StorageDriverInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final TusFileUploadService tusFileUploadService;
    //private final SimpMessagingTemplate messagingTemplate;
    private static final Logger logger = LoggerFactory.getLogger(StorageTempService.class);
    private final String tempBucketName = "temp_files";

    private String buildPath(long userId, String fileId) {
        return "/" + userId + "/" + fileId + "/originalBytes";
    }

    public void putContent(long userId, String username, String uploadURI, Map<String, String> metadata) {
        try (InputStream inputStream = this.tusFileUploadService.getUploadedBytes(uploadURI)) {
            String fileId = metadata.get("fileId");
            byte[] fileBytes = inputStream.readAllBytes();

            storage.put(tempBucketName, buildPath(userId, fileId), fileBytes);

//            messagingTemplate.convertAndSendToUser(
//                    username,
//                    "/topic/tempfile-uploaded." + fileId,
//                    "The file is completely uploaded to the temp storage. Proceed with your next action associated with this file."
//            );
        }
        catch (IOException | TusException | ServerException | InsufficientDataException | ErrorResponseException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            logger.error("Error while getting uploaded bytes", e);
        }

        try {
            this.tusFileUploadService.deleteUpload(uploadURI);
        } catch (IOException | TusException e) {
            logger.error("Error while deleting uploaded data", e);
        }
    }

    public byte[] getOriginalBytes(long userId, String fileId) {
        try {
            return storage.getBytes(tempBucketName, buildPath(userId, fileId));
        }
        catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            logger.error("Error while deleting uploaded data", e);
            throw new RuntimeException(e);
        }
    }

    public boolean isFileExists(long userId, String fileId) {

        return false;
    }
}