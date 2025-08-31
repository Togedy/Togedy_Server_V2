package com.togedy.togedy_server_v2.global.service;

import com.togedy.togedy_server_v2.global.exception.StorageUploadFailedException;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    private final S3Template s3Template;

    public String uploadFile(MultipartFile multipartFile) {
        String key = createFileName(multipartFile.getOriginalFilename());

        ObjectMetadata meta = ObjectMetadata.builder()
                .contentType(multipartFile.getContentType())
                .contentLength(multipartFile.getSize())
                .build();

        try (InputStream is = multipartFile.getInputStream()) {
            S3Resource res = s3Template.upload(bucket, key, is, meta);
            return res.getURL().toExternalForm();
        } catch (IOException e) {
            throw new StorageUploadFailedException();
        }
    }

    public void deleteFile(String fileName) {
        s3Template.deleteObject(bucket, fileName);
    }

    private String createFileName(String fileName){
        String extension = "";
        if (fileName != null) {
            int lastDot = fileName.lastIndexOf('.');
            if (lastDot >= 0) {
                extension = fileName.substring(lastDot);
            }
        }
        return UUID.randomUUID() + extension;
    }

}
