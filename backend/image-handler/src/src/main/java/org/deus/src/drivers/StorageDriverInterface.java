package org.deus.src.drivers;

import org.deus.src.exceptions.StorageException;

public interface StorageDriverInterface {
    public byte[] getBytes(String bucketName, String path)
            throws StorageException;

    public void put(String bucketName, String path, byte[] bytes)
            throws StorageException;
}
