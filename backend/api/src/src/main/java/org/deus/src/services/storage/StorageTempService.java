package org.deus.src.services.storage;

import lombok.AllArgsConstructor;
import org.deus.src.drivers.StorageDriverInterface;
import org.deus.src.exceptions.StorageException;
import org.deus.src.exceptions.data.DataDeletingException;
import org.deus.src.exceptions.data.DataNotFoundException;
import org.deus.src.exceptions.data.DataProcessingException;
import org.deus.src.exceptions.data.DataSavingException;
import org.deus.src.exceptions.data.DataRetrievingException;
import org.deus.src.services.TusFileUploadWrapperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StorageTempService {
    private final StorageDriverInterface storage;

    private final TusFileUploadWrapperService tusFileUploadWrapperService;
    private static final Logger logger = LoggerFactory.getLogger(StorageTempService.class);
    private final String tempBucketName = "temp_files";

    private String buildPath(long userId, String fileId) {
        return "/" + userId + "/" + fileId + "/originalBytes";
    }

    public void putContent(long userId, String uploadURI, Map<String, String> metadata) throws DataProcessingException, DataNotFoundException {
        Optional<InputStream> optionalInputStream = this.tusFileUploadWrapperService.getUploadedBytes(uploadURI);

        if (optionalInputStream.isPresent()) {
            try (InputStream inputStream = optionalInputStream.get()) {
                this.processInputStream(inputStream, userId, metadata, uploadURI);
            }
            catch (IOException | DataProcessingException | DataSavingException e) {
                String errorMessage = "Error while processing uploaded bytes";
                logger.error(errorMessage, e);
                throw new DataProcessingException(errorMessage, e);
            }
        }
        else {
            String errorMessage = "Uploaded bytes not found for URI: " + uploadURI;
            logger.error(errorMessage);
            throw new DataNotFoundException(errorMessage);
        }
    }

    private void processInputStream(InputStream inputStream, long userId, Map<String, String> metadata, String uploadURI) throws DataProcessingException, DataSavingException {
        try {
            String fileId = metadata.get("fileId");
            byte[] fileBytes = inputStream.readAllBytes();
            storage.put(tempBucketName, buildPath(userId, fileId), fileBytes);
            this.tusFileUploadWrapperService.deleteUpload(uploadURI);
        }
        catch (IOException | OutOfMemoryError e) {
            String errorMessage = "Error while trying to read bytes from provided InputStream";
            logger.error(errorMessage, e);
            throw new DataProcessingException(errorMessage, e);
        }
        catch (StorageException e) {
            String errorMessage = "Error while storing uploaded bytes";
            logger.error(errorMessage, e);
            throw new DataSavingException(errorMessage, e);
        }
        catch (DataDeletingException e) {
            throw new DataProcessingException(e);
        }
    }

    public byte[] getOriginalBytes(long userId, String fileId) throws DataRetrievingException {
        try {
            return storage.getBytes(tempBucketName, buildPath(userId, fileId));
        }
        catch (StorageException e) {
            String errorMessage = "Error while retrieving original bytes";
            logger.error(errorMessage, e);
            throw new DataRetrievingException(errorMessage, e);
        }
    }

    public boolean isFileExists(long userId, String fileId) {

        return false;
    }
}