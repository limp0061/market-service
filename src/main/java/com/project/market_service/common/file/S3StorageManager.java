package com.project.market_service.common.file;

import com.project.market_service.common.config.S3Properties;
import com.project.market_service.common.exception.CommonErrorCode;
import com.project.market_service.common.exception.InvalidValueException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3StorageManager {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public void upload(String key, MultipartFile file) {
        upload(key, file, "public, max-age=31536000");
    }

    public void upload(String key, MultipartFile file, String cacheControl) {

        if (file == null || file.isEmpty()) {
            throw new InvalidValueException(CommonErrorCode.FILE_UPLOAD_ERROR);
        }

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.bucket())
                .key(key)
                .contentLength(file.getSize())
                .contentType(file.getContentType())
                .cacheControl(cacheControl)
                .build();
        try {
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            log.info("[File Upload Success] key: {}", key);
        } catch (IOException | S3Exception e) {
            throw new InvalidValueException(CommonErrorCode.FILE_UPLOAD_ERROR, "key: " + key);
        }
    }

    public String getPublicUrl(String key) {
        return String.format("%s/%s", s3Properties.publicUrl(), key);
    }
}
