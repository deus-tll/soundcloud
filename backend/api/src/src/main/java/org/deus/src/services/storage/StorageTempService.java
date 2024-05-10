package org.deus.src.services.storage;

import lombok.AllArgsConstructor;
import org.deus.src.drivers.StorageDriverInterface;
import org.deus.src.exceptions.StorageException;
import org.deus.src.exceptions.data.DataProcessingException;
import org.deus.src.exceptions.data.DataSavingException;
import org.deus.src.exceptions.data.DataRetrievingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StorageTempService {
    private final StorageDriverInterface storage;
    private static final Logger logger = LoggerFactory.getLogger(StorageTempService.class);
    private final String bucketName = "temp_files";

    private String buildPath(long userId, String fileId) {
        return "/" + userId + "/" + fileId + "/originalBytes";
    }

    public void putOriginalBytes(long userId, String fileId, byte[] originalBytes) throws DataSavingException {
        try {
            this.storage.put(bucketName, buildPath(userId, fileId), originalBytes);
        } catch (StorageException e) {
            String errorMessage = "Error while storing uploaded bytes";
            logger.error(errorMessage, e);
            throw new DataSavingException(errorMessage, e);
        }
    }

    public byte[] getOriginalBytes(long userId, String fileId) throws DataRetrievingException {
        try {
            return storage.getBytes(bucketName, buildPath(userId, fileId));
        }
        catch (StorageException e) {
            String errorMessage = "Error while retrieving original bytes";
            logger.error(errorMessage, e);
            throw new DataRetrievingException(errorMessage, e);
        }
    }

    public boolean isFileExists(long userId, String fileId) throws DataProcessingException {
        try {
            return this.storage.isFileExists(bucketName, buildPath(userId, fileId));
        } catch (StorageException e) {
            String errorMessage = "Error while checking if file exists in storage";
            logger.error(errorMessage, e);
            throw new DataProcessingException(errorMessage, e);
        }
    }
}