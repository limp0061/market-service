package com.project.market_service.common.file;

import com.project.market_service.common.exception.CommonErrorCode;
import com.project.market_service.common.exception.InvalidValueException;
import com.project.market_service.common.util.UuidGenerator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileValidator {

    private final UuidGenerator uuidGenerator;

    private static final List<String> ALLOWED_MIME_TYPES = List.of("image/jpeg", "image/png", "image/webp",
            "image/gif");

    private static final List<String> WHITELIST_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp", "gif");

    public void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidValueException(CommonErrorCode.FILE_EMPTY);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            log.warn("Invalid content type - {}", contentType);
            throw new InvalidValueException(CommonErrorCode.INVALID_FILE_TYPE);
        }

        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new InvalidValueException(CommonErrorCode.INVALID_FILE_NAME_EMPTY);
        }

        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!WHITELIST_EXTENSIONS.contains(ext)) {
            log.warn("Invalid file extension - {}", ext);
            throw new InvalidValueException(CommonErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    public String normalizeFilename(String originalFilename) {
        String uuid = uuidGenerator.generate();
        if (!StringUtils.hasText(originalFilename)) {
            throw new InvalidValueException(CommonErrorCode.INVALID_FILE_NAME_EMPTY);
        }

        int dotIndex = originalFilename.lastIndexOf(".");
        String name = dotIndex == -1 ? originalFilename : originalFilename.substring(0, dotIndex);
        String ext = dotIndex == -1 ? "" : originalFilename.substring(dotIndex);

        String normalize = name.replaceAll("[^a-zA-Z0-9가-힣-_]", "_");

        return uuid + "_" + normalize + ext;
    }
}
