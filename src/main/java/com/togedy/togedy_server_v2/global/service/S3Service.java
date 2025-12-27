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

    /**
     * S3에 파일을 업로드한다.
     *
     * @param multipartFile 이미지 파일
     * @return              파일 URL
     */
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

    /**
     * S3에서 해당 파일을 제거한다.
     *
     * @param fileName  파일명
     */
    public void deleteFile(String fileName) {
        if (fileName == null) {
            return;
        }

        s3Template.deleteObject(bucket, fileName);
    }

    /**
     * UUID + 확장자 형식으로 파일명을 생성한다.
     *
     * @param fileName  기존 파일명
     * @return          생성된 파일명
     */
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
