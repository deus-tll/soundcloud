package org.deus.storagestarter.drivers;

import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import org.deus.storagestarter.exceptions.StorageException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

public class StorageMinioDriver implements StorageDriverInterface{
    private final MinioClient minioClient;
    private final Set<String> existingBuckets;

    public StorageMinioDriver(MinioClient minioClient) {
        this.minioClient = minioClient;
        this.existingBuckets = cacheExistingBuckets();
    }

    private Set<String> cacheExistingBuckets() throws RuntimeException {
        Set<String> buckets = new HashSet<>();
        try {
            for (Bucket bucket : minioClient.listBuckets()) {
                buckets.add(bucket.name());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error caching existing buckets", e);
        }
        return buckets;
    }

    private void checkAndCreateBucket(String bucketName) throws RuntimeException {
        if (!existingBuckets.contains(bucketName)) {
            try {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                existingBuckets.add(bucketName);
            } catch (Exception e) {
                throw new RuntimeException("Error creating bucket: " + bucketName, e);
            }
        } else {
            System.out.println("Bucket '" + bucketName + "' already exists.");
        }
    }

    @Override
    public byte[] getBytes(String bucketName, String path)
            throws StorageException
    {
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(path)
                        .build()))
        {
            return stream.readAllBytes();
        }
        catch (IOException | InvalidKeyException | NoSuchAlgorithmException |
               ServerException | InsufficientDataException |
               ErrorResponseException | InvalidResponseException |
               XmlParserException | InternalException e) {
            throw new StorageException("Error getting bytes", e);
        }
    }

    @Override
    public void put(String bucketName, String path, byte[] bytes)
            throws StorageException
    {
        try (ByteArrayInputStream bytesStream = new ByteArrayInputStream(bytes)) {
            checkAndCreateBucket(bucketName);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(path)
                            .stream(bytesStream, bytesStream.available(), -1)
                            .build()
            );
        }
        catch (RuntimeException | IOException | InvalidKeyException | NoSuchAlgorithmException |
               ServerException | InsufficientDataException |
               ErrorResponseException | InvalidResponseException |
               XmlParserException | InternalException e) {
            throw new StorageException("Error putting bytes", e);
        }
    }
}
