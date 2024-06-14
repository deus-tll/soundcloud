package org.deus.src.services.storage;

import lombok.RequiredArgsConstructor;
import org.deus.src.drivers.StorageDriverInterface;
import org.deus.src.exceptions.StorageException;
import org.deus.src.exceptions.data.DataSavingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoragePerformerPhotoService {
    private final StorageDriverInterface storage;
    private final String bucketName = "performers-photos";
    private static final Logger logger = LoggerFactory.getLogger(StoragePerformerPhotoService.class);

    private String buildPathToOriginalBytes(long performerId) {
        return "/" + performerId + "/originalBytes";
    }
    private String buildPathToFile(long performerId) {
        return performerId + "/photo.webp";
    }

    public void putOriginalBytes(long performerId, byte[] bytes) throws DataSavingException {
        try {
            storage.put(bucketName, buildPathToOriginalBytes(performerId), bytes);
        }
        catch (StorageException e) {
            String errorMessage = "Error while putting original bytes to store, bucket/container: \"" + bucketName + "\"";
            logger.error(errorMessage, e);
            throw new DataSavingException(errorMessage, e);
        }
    }

    public void putNewBytesAsFile(long performerId, byte[] bytes) throws DataSavingException {
        try {
            storage.put(bucketName, "/" + buildPathToFile(performerId), bytes);
        }
        catch (StorageException e) {
            String errorMessage = "Error while putting bytes to store, bucket/container: \"" + bucketName + "\"";
            logger.error(errorMessage, e);
            throw new DataSavingException(errorMessage, e);
        }
    }

    public Optional<byte[]> getOriginalBytes(long performerId) {
        try {
            byte[] bytes = storage.getBytes(bucketName, buildPathToOriginalBytes(performerId));
            return Optional.ofNullable(bytes);
        }
        catch (StorageException e) {
            logger.error("Error while getting original bytes from store, bucket/container: \"" + bucketName + "\"", e);
            return Optional.empty();
        }
    }

    public String getPathToFile(long performerId) {
        return storage.getPublicUrl(bucketName, buildPathToFile(performerId));
    }
}
