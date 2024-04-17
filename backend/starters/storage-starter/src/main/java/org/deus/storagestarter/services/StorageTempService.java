package org.deus.storagestarter.services;

import lombok.AllArgsConstructor;
import org.deus.storagestarter.drivers.StorageDriverInterface;
import org.deus.storagestarter.exceptions.StorageException;
import org.deus.tusuploadfilestarter.services.TusFileUploadWrapperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
@Component
public class StorageTempService {
    private final StorageDriverInterface storage;

    private final TusFileUploadWrapperService tusFileUploadWrapperService ;
    private static final Logger logger = LoggerFactory.getLogger(StorageTempService.class);
    private final String tempBucketName = "temp_files";

    private String buildPath(long userId, String fileId) {
        return "/" + userId + "/" + fileId + "/originalBytes";
    }

    public void putContent(long userId, String username, String uploadURI, Map<String, String> metadata) {
        Optional<InputStream> optionalInputStream = this.tusFileUploadWrapperService.getUploadedBytes(uploadURI);

        optionalInputStream.ifPresentOrElse(inputStream -> {
            try (InputStream is = inputStream) {
                processInputStream(is, userId, metadata, uploadURI);
            } catch (IOException e) {
                logger.error("Error while processing uploaded bytes", e);
            }
        }, () -> {
            logger.error("Uploaded bytes not found for URI: " + uploadURI);
        });
    }

    private void processInputStream(InputStream inputStream, long userId, Map<String, String> metadata, String uploadURI) throws IOException {
        String fileId = metadata.get("fileId");
        byte[] fileBytes = inputStream.readAllBytes();

        storage.put(tempBucketName, buildPath(userId, fileId), fileBytes);

        this.tusFileUploadWrapperService.deleteUpload(uploadURI);
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