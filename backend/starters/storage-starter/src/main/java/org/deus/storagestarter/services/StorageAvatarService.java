package org.deus.storagestarter.services;

import lombok.AllArgsConstructor;
import org.deus.storagestarter.drivers.StorageDriverInterface;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Component
public class StorageAvatarService {
    private final StorageDriverInterface storage;
    private final String avatarBucketName = "avatars";

    private String buildPath(long user_id) {
        return "/" + user_id + "/originalBytes";
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
