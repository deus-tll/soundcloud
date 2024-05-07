package org.deus.src.services;

import lombok.AllArgsConstructor;
import org.deus.src.drivers.StorageDriverInterface;
import org.deus.src.exceptions.StorageException;
import org.deus.src.exceptions.data.DataSavingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StorageAvatarService {
    private final StorageDriverInterface storage;
    private final String bucketName = "avatars";
    private static final Logger logger = LoggerFactory.getLogger(StorageAvatarService.class);


    private String buildPathToOriginalBytes(long userId) {
        return "/" + userId + "/originalBytes";
    }

    public void putOriginalBytes(long userId, byte[] bytes) throws DataSavingException {
        try {
            storage.put(bucketName, buildPathToOriginalBytes(userId), bytes);
        }
        catch (StorageException e) {
            String errorMessage = "Error while putting original bytes to store, bucket/container: \"" + bucketName + "\"";
            logger.error(errorMessage, e);
            throw new DataSavingException(errorMessage, e);
        }
    }

    public void putWebP(long userId, byte[] bytes) throws DataSavingException {
        try {
            storage.put(bucketName,  "/" + userId + "/avatar.webp", bytes);
        }
        catch (StorageException e) {
            String errorMessage = "Error while putting WebP bytes to store, bucket/container: \"" + bucketName + "\"";
            logger.error(errorMessage, e);
            throw new DataSavingException(errorMessage, e);
        }
    }

    public Optional<byte[]> getOriginalBytes(long userId) {
        try {
            byte[] bytes = storage.getBytes(bucketName, buildPathToOriginalBytes(userId));
            return Optional.ofNullable(bytes);
        }
        catch (StorageException e) {
            logger.error("Error while getting original bytes from store, bucket/container: \"" + bucketName + "\"", e);
            return Optional.empty();
        }
    }

    public void gravatarDownloadAndPut(long userId, String gravatarUrl) throws DataSavingException {
        try {
            byte[] imageData = downloadImage(gravatarUrl);
            this.putOriginalBytes(userId, imageData);
        }
        catch (IOException e) {
            String errorMessage = "Error while downloading image by url";
            logger.error(errorMessage, e);
            throw new DataSavingException(errorMessage, e);
        }
        catch (DataSavingException e) {
            String errorMessage = "Error while putting gravatar's original bytes to store, bucket/container: \"" + bucketName + "\"";
            logger.error(errorMessage, e);
            throw new DataSavingException(errorMessage, e);
        }
    }

    private byte[] downloadImage(String imageUrl) throws IOException {
        URL url = URI.create(imageUrl).toURL();
        try (InputStream inputStream = url.openStream()) {
            return inputStream.readAllBytes();
        }
    }
}