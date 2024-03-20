package org.deus.api.services.storages;

import org.deus.api.storages.drivers.StorageDriverInterface;

import org.springframework.stereotype.Service;

import java.io.File;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StorageAvatarService {
    private final StorageDriverInterface storage;
    private final String avatarBucketName = "avatars";

    private String buildPath(long user_id) {
        return "/" + user_id + "/originalBytes";
    }

    public void putOriginal(long user_id, File file) {
        try {
            storage.put(avatarBucketName, buildPath(user_id), file);
        } catch (Exception e) {
            System.out.println(" Error ");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void putOriginal(long user_id, byte[] bytes) {
        try {
            storage.put(avatarBucketName, buildPath(user_id), bytes);
        } catch (Exception e) {
            System.out.println(" Error ");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void putWebP(long user_id, byte[] bytes) {
        try {
            storage.put(avatarBucketName,  "/" + user_id + "/avatar.webp", bytes);
        } catch (Exception e) {
            System.out.println(" Error ");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public byte[] getOriginalBytes(long user_id) {
        try {
            return storage.getBytes(avatarBucketName, buildPath(user_id));
        } catch (Exception e) {
            System.out.println(" Error ");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
