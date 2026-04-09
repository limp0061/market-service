package com.project.market_service.common.infrastructure.file;

import static com.project.market_service.common.constants.FilePathConstants.PRODUCT_PATH;

import com.project.market_service.common.application.port.out.FilePort;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class S3FileAdapter implements FilePort {

    private final FileValidator fileValidator;
    private final S3StorageManager s3StorageManager;

    @Override
    public List<String> uploadProductImage(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return Collections.emptyList();
        }

        return images.stream()
                .map(this::singleFileUpload)
                .toList();
    }

    @Override
    public String singleFileUpload(MultipartFile file) {

        String filePath = makeImagePath(file);
        s3StorageManager.upload(filePath, file);
        return s3StorageManager.getPublicUrl(filePath);
    }

    private String makeImagePath(MultipartFile file) {

        fileValidator.validateImage(file);

        String filename = fileValidator.normalizeFilename(file.getOriginalFilename());
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        return PRODUCT_PATH + "/" + today + "/" + filename;
    }
}
