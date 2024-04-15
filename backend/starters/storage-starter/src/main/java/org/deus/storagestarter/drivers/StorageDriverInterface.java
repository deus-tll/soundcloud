package org.deus.storagestarter.drivers;

import io.minio.errors.*;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
