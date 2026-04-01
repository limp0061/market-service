package com.project.market_service.common.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.project.market_service.common.exception.CommonErrorCode;
import com.project.market_service.common.exception.InvalidValueException;
import com.project.market_service.common.util.UuidGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class FileValidatorTest {

    @Mock
    private UuidGenerator uuidGenerator;

    @InjectMocks
    private FileValidator fileValidator;

    @Test
    @DisplayName("파일이 비어있는 경우 예외가 발생한다")
    void validateImage() {
        // when & then
        assertThatThrownBy(() -> fileValidator.validateImage(null))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage(CommonErrorCode.FILE_EMPTY.getMessage());
    }

    @Test
    @DisplayName("파일이 비어있으면 예외가 발생한다")
    void validateImage_emptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "file.jpg", "image/jpeg", new byte[0]);

        assertThatThrownBy(() -> fileValidator.validateImage(emptyFile))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage(CommonErrorCode.FILE_EMPTY.getMessage());
    }

    @Test
    @DisplayName("허용되지 않은 마임 타입인 경우 예외가 발생한다")
    void validateImage_notAllowedMimeType() {
        MockMultipartFile invalidFile = new MockMultipartFile("file", "file.jpg", "text/plain", "text".getBytes());
        assertThatThrownBy(() -> fileValidator.validateImage(invalidFile))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage(CommonErrorCode.INVALID_FILE_TYPE.getMessage());
    }

    @Test
    @DisplayName("파일 이름이 없는 경우 예외가 발생한다")
    void validateImage_filename_empty() {
        MockMultipartFile invalidFile = new MockMultipartFile("file", "", "image/jpeg", "text".getBytes());
        assertThatThrownBy(() -> fileValidator.validateImage(invalidFile))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage(CommonErrorCode.INVALID_FILE_NAME_EMPTY.getMessage());
    }

    @Test
    @DisplayName("허용되지 없는 파일 확장자인 경우 예외가 발생한다")
    void validateImage_notAllowedExtension() {
        MockMultipartFile invalidFile = new MockMultipartFile("file", "file.txt", "image/jpeg", "text".getBytes());
        assertThatThrownBy(() -> fileValidator.validateImage(invalidFile))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage(CommonErrorCode.INVALID_FILE_EXTENSION.getMessage());
    }

    @Test
    @DisplayName("정상적인 이미지 파일이면 예외가 발생하지 않는다")
    void validateImage_success() {
        MockMultipartFile file = new MockMultipartFile("file", "file.jpg", "image/jpeg", "data".getBytes());

        assertThatCode(() -> fileValidator.validateImage(file))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("정상 파일명은 uuid_파일명.확장자 형식으로 반환한다")
    void normalizeFilename_success() {
        given(uuidGenerator.generate()).willReturn("aaaaaa111111");

        String result = fileValidator.normalizeFilename("test.jpg");

        assertThat(result).isEqualTo("aaaaaa111111_test.jpg");
    }

    @Test
    @DisplayName("특수문자는 언더스코어로 변환된다")
    void normalizeFilename_specialCharacters() {
        given(uuidGenerator.generate()).willReturn("aaaaaa111111");

        String result = fileValidator.normalizeFilename("te st!@.jpg");

        assertThat(result).isEqualTo("aaaaaa111111_te_st__.jpg");
    }
}