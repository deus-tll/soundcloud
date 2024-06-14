package org.deus.src.drivers;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import org.deus.src.exceptions.StorageException;

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
    private final String baseUrl;

    public StorageMinioDriver(MinioClient minioClient, String baseUrl) {
        this.minioClient = minioClient;
        this.existingBuckets = cacheExistingBuckets();
        this.baseUrl = baseUrl;
    }

    private Set<String> cacheExistingBuckets() {
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
                setBucketPolicyPublic(bucketName);
                existingBuckets.add(bucketName);
            } catch (Exception e) {
                throw new RuntimeException("Error creating bucket: " + bucketName, e);
            }
        } else {
            System.out.println("Bucket '" + bucketName + "' already exists.");
        }
    }

    private void setBucketPolicyPublic(String bucketName) {
        String policyJson = String.format("""
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Allow",
                      "Principal": "*",
                      "Action": [
                        "s3:GetObject"
                      ],
                      "Resource": [
                        "arn:aws:s3:::%s/*"
                      ]
                    }
                  ]
                }""", bucketName);

        try {
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(bucketName)
                    .config(policyJson)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Error setting bucket policy to public: " + bucketName, e);
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
        catch (IOException | InvalidKeyException | NoSuchAlgorithmException |
               ServerException | InsufficientDataException |
               ErrorResponseException | InvalidResponseException |
               XmlParserException | InternalException e) {
            throw new StorageException("Error putting bytes", e);
        }
    }

    @Override
    public Boolean isFileExists(String bucketName, String path) throws StorageException {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(path).build());
            return true;
        }
        catch (ErrorResponseException e) {
            return false;
        }
        catch (IOException | InvalidKeyException | NoSuchAlgorithmException |
               ServerException | InsufficientDataException | InvalidResponseException |
               XmlParserException | InternalException e) {
            throw new StorageException("Error checking if certain object exists", e);
        }
    }

    public String getPublicUrl(String bucketName, String path) {
        return String.format("%s/%s/%s", baseUrl, bucketName, path);
    }
}
