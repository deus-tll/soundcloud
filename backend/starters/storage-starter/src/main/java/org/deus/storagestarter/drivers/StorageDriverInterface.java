package org.deus.storagestarter.drivers;

import org.deus.storagestarter.exceptions.StorageException;

public interface StorageDriverInterface {
    public byte[] getBytes(String bucketName, String path)
            throws StorageException;

    public void put(String bucketName, String path, byte[] bytes)
            throws StorageException;
}
