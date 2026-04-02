package com.project.market_service.common.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.project.market_service.common.exception.CommonErrorCode;
import com.project.market_service.common.exception.InvalidValueException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    FileValidator fileValidator;
    @Mock
    S3StorageManager s3StorageManager;

    @InjectMocks
    private FileService fileService;

    @Test
    @DisplayName("파일 업로드 시 파일 업로드에 성공한다")
    void uploadProductImage() {
        // given
        MockMultipartFile file1 = new MockMultipartFile("file1", "file1.jpg", "images/jpeg", "file1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("file2", "file2.jpg", "images/jpeg", "file1".getBytes());
        List<MultipartFile> images = List.of(file1, file2);

        given(s3StorageManager.getPublicUrl(anyString())).willReturn("https://s3.bucket/test.jpg");

        // when
        List<String> result = fileService.uploadProductImage(images);

        // then
        assertAll(
                () -> assertThat(result).hasSize(2),
                () -> verify(s3StorageManager, times(2)).upload(anyString(), any(MultipartFile.class)),
                () -> verify(s3StorageManager, times(2)).getPublicUrl(anyString())
        );
    }

    @Test
    @DisplayName("빈 리스트 전달 시 빈 리스트를 반환한다")
    void uploadProductImage_emptyList() {
        // when
        List<String> result = fileService.uploadProductImage(List.of());

        // then
        assertAll(
                () -> assertThat(result).isEmpty(),
                () -> verify(s3StorageManager, never()).upload(anyString(), any(MultipartFile.class))
        );

    }

    @Test
    @DisplayName("유효하지 않은 파일 전달 시 예외가 발생한다")
    void uploadProductImage_invalidFile() {
        // given
        MockMultipartFile invalidFile = new MockMultipartFile("file1", "file1.jpg", "text/plain", "file1".getBytes());

        willThrow(new InvalidValueException(CommonErrorCode.INVALID_FILE_TYPE))
                .given(fileValidator).validateImage(any());

        // when & then
        assertThatThrownBy(() -> fileService.uploadProductImage(List.of(invalidFile)))
                .isInstanceOf(InvalidValueException.class);

        verify(s3StorageManager, never()).upload(anyString(), any());
    }
}