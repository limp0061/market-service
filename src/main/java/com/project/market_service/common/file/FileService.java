package com.project.market_service.common.file;

import static com.project.market_service.common.file.FilePathConstants.PRODUCT_PATH;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileValidator fileValidator;
    private final S3StorageManager s3StorageManager;

    public List<String> uploadProductImage(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return Collections.emptyList();
        }

        return images.stream()
                .map(this::singleFileUpload)
                .toList();
    }

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
