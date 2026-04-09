package com.project.market_service.common.application.port.out;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface FilePort {
    List<String> uploadProductImage(List<MultipartFile> images);

    String singleFileUpload(MultipartFile file);
}
