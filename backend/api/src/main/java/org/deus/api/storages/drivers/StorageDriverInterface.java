package org.deus.api.storages.drivers;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import io.minio.errors.ServerException;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.XmlParserException;
import io.minio.errors.InternalException;

public interface StorageDriverInterface {
    public byte[] getBytes(String bucketName, String path)
            throws
            ServerException, InsufficientDataException,
            ErrorResponseException, IOException,
            NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException,
            InternalException;

    public void put(String bucketName, String path, File file)
            throws
            IOException, ServerException,
            InsufficientDataException, ErrorResponseException,
            NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException,
            InternalException;

    public void put(String bucketName, String path, byte[] bytes)
            throws IOException, ServerException,
            InsufficientDataException, ErrorResponseException,
            NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException,
            InternalException;
}
