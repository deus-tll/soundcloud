package org.deus.storagestarter.services;

import lombok.AllArgsConstructor;
import org.deus.storagestarter.drivers.StorageDriverInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

@Service
@AllArgsConstructor
@Component
public class StorageAvatarService {
    private final StorageDriverInterface storage;
    private final String avatarBucketName = "avatars";

    private String buildPathToOriginalBytes(long userId) {
        return "/" + userId + "/originalBytes";
    }
    private static final Logger logger = LoggerFactory.getLogger(StorageAvatarService.class);

    public void putOriginalBytes(long userId, byte[] bytes) {
        try {
            storage.put(avatarBucketName, buildPathToOriginalBytes(userId), bytes);
        } catch (Exception e) {
            logger.error("Error while putting original bytes to store, bucket/container: \"" + avatarBucketName + "\"", e);
        }
    }

    public void putWebP(long userId, byte[] bytes) {
        try {
            storage.put(avatarBucketName,  "/" + userId + "/avatar.webp", bytes);
        } catch (Exception e) {
            logger.error("Error while putting WebP bytes to store, bucket/container: \"" + avatarBucketName + "\"", e);
        }
    }

    public Optional<byte[]> getOriginalBytes(long userId) {
        try {
            byte[] bytes = storage.getBytes(avatarBucketName, buildPathToOriginalBytes(userId));
            return Optional.ofNullable(bytes);
        } catch (Exception e) {
            logger.error("Error while getting original bytes from store, bucket/container: \"" + avatarBucketName + "\"", e);
            return Optional.empty();
        }
    }

    public void gravatarDownloadAndPut(long userId, String gravatarUrl) {
        try {
            URL url = URI.create(gravatarUrl).toURL();
            try (InputStream inputStream = url.openStream()) {
                this.putOriginalBytes(userId, inputStream.readAllBytes());
            }
        } catch (IOException e) {
            logger.error("Error while putting gravatar's original bytes to store, bucket/container: \"" + avatarBucketName + "\"", e);
        }
    }
}
