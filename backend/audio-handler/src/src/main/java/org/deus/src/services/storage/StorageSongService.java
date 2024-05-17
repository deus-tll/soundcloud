package org.deus.src.services.storage;

import lombok.RequiredArgsConstructor;
import org.deus.src.drivers.StorageDriverInterface;
import org.deus.src.exceptions.StorageException;
import org.deus.src.exceptions.data.DataSavingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StorageSongService {
    private final StorageDriverInterface storage;
    private static final Logger logger = LoggerFactory.getLogger(StorageSongService.class);
    private final String bucketName = "songs";

    private String buildPath(long songId, String type) {
        return "/" + songId + "/song" + type;
    }

    public void putConvertedBytes(long songId, byte[] convertedBytes, String type) throws DataSavingException {
        try {
            this.storage.put(bucketName, buildPath(songId, type), convertedBytes);
        } catch (StorageException e) {
            String errorMessage = "Error while putting converted bytes of file with type \"" + type + "\" to store, bucket/container: \"" + bucketName + "\"";
            logger.error(errorMessage, e);
            throw new DataSavingException(errorMessage, e);
        }
    }

    public String getPathToSong(long songId, String type) {
        return buildPath(songId, type);
    }
}
