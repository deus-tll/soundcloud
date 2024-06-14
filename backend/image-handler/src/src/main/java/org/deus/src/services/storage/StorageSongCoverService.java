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
public class StorageSongCoverService {
    private final StorageDriverInterface storage;
    private final String bucketName = "songs-covers";
    private static final Logger logger = LoggerFactory.getLogger(StorageSongCoverService.class);

    private String buildPathToOriginalBytes(long songId) {
        return "/" + songId + "/originalBytes";
    }
    private String buildPathToFile(long songId) {
        return songId + "/cover.webp";
    }

    public void putOriginalBytes(long songId, byte[] bytes) throws DataSavingException {
        try {
            storage.put(bucketName, buildPathToOriginalBytes(songId), bytes);
        }
        catch (StorageException e) {
            String errorMessage = "Error while putting original bytes to store, bucket/container: \"" + bucketName + "\"";
            logger.error(errorMessage, e);
            throw new DataSavingException(errorMessage, e);
        }
    }

    public void putNewBytesAsFile(long songId, byte[] bytes) throws DataSavingException {
        try {
            storage.put(bucketName, "/" + buildPathToFile(songId), bytes);
        }
        catch (StorageException e) {
            String errorMessage = "Error while putting bytes to store, bucket/container: \"" + bucketName + "\"";
            logger.error(errorMessage, e);
            throw new DataSavingException(errorMessage, e);
        }
    }

    public Optional<byte[]> getOriginalBytes(long songId) {
        try {
            byte[] bytes = storage.getBytes(bucketName, buildPathToOriginalBytes(songId));
            return Optional.ofNullable(bytes);
        }
        catch (StorageException e) {
            logger.error("Error while getting original bytes from store, bucket/container: \"" + bucketName + "\"", e);
            return Optional.empty();
        }
    }

    public String getPathToFile(long songId) {
        return storage.getPublicUrl(bucketName, buildPathToFile(songId));
    }
}
