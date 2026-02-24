package com.togedy.togedy_server_v2.global.service;

import com.togedy.togedy_server_v2.global.enums.ImageCategory;
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
import java.net.URI;
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
     * @param imageCategory 이미지 카테고리
     * @return              파일 URL
     */
    public String uploadFile(MultipartFile multipartFile, ImageCategory imageCategory) {
        String key = createFileKey(multipartFile.getOriginalFilename(), imageCategory);

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
     * @param fileNameOrUrl  파일 key 또는 URL
     */
    public void deleteFile(String fileNameOrUrl) {
        if (fileNameOrUrl == null || fileNameOrUrl.isBlank()) {
            return;
        }

        s3Template.deleteObject(bucket, extractObjectKey(fileNameOrUrl));
    }

    /**
     * prefix/UUID + 확장자 형식으로 파일 key를 생성한다.
     *
     * @param fileName  기존 파일명
     * @param imageCategory 이미지 카테고리
     * @return          생성된 key
     */
    private String createFileKey(String fileName, ImageCategory imageCategory){
        String extension = "";
        if (fileName != null) {
            int lastDot = fileName.lastIndexOf('.');
            if (lastDot >= 0) {
                extension = fileName.substring(lastDot);
            }
        }
        return imageCategory.getPrefix() + "/" + UUID.randomUUID() + extension;
    }

    /**
     * 삭제 입력값에서 S3 object key를 추출한다.
     * URL이 전달되면 path에서 key를 분리하고, key가 전달되면 그대로 반환한다.
     */
    private String extractObjectKey(String fileNameOrUrl) {
        String trimmed = fileNameOrUrl.trim();

        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            String path = URI.create(trimmed).getPath();
            String key = path.startsWith("/") ? path.substring(1) : path;

            // Path-style endpoint 대응: /{bucket}/{key}
            if (key.startsWith(bucket + "/")) {
                return key.substring(bucket.length() + 1);
            }
            return key;
        }

        return trimmed.startsWith("/") ? trimmed.substring(1) : trimmed;
    }

}
