package com.sobolev.spring.userservice.service;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class MinioService {
    private final MinioClient minioClient;
    private final String bucketName;

    @Autowired
    public MinioService(MinioClient minioClient,
                        @Value("${minio.bucketName}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    public String uploadProfilePicture(MultipartFile file, String username) {
        try {
            String fileName = "profile_picture/" + username + UUID.randomUUID() +".jpg";

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .method(Method.GET)
                            .build()
            );

        } catch (Exception e){
            throw new RuntimeException("Error uploading profile picture",e);
        }
    }

    public void deleteProfilePicture(String username) {
        try{
            String fileName = "profile_picture/" + username;

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (MinioException e){
            throw new RuntimeException("MinIO exception",e);
        } catch (Exception e){
            throw new RuntimeException("Error deleting profile picture",e);
        }
    }

    public InputStreamResource downloadProfilePicture(String fileUrl) {
        try{

            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/profile-pictures/") + "/profile-pictures/".length(), fileUrl.indexOf("?"));

            return new InputStreamResource(minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
            ));
        }catch (Exception e){
            throw new RuntimeException("Error downloading profile picture",e);
        }
    }
}
