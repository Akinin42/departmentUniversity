package org.university.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.university.exceptions.UploadS3Exception;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;

class AwsS3ServiceImplTest {

    private AmazonS3 amazonS3;
    private AwsS3ServiceImpl awsS3Service;

    @BeforeEach
    public void initMocks() {
        amazonS3 = mock(AmazonS3.class);
        awsS3Service = new AwsS3ServiceImpl(amazonS3);
    }

    @Test
    void uploadFileShouldReturnExpectedUrl() throws IOException {
        File file = new File("src/test/resources/validimage.png");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("validimage", file.getName(), "image/png",
                IOUtils.toByteArray(input));
        String time = LocalDateTime.now().toString().replace(".", "").replace("-", "").replace(":", "");
        String uniqueFileName = String.format("%s%s", time, multipartFile.getOriginalFilename());
        URL url = new URL("https", "akininuniversity.s3.eu-central-1.amazonaws.com", uniqueFileName);
        when(amazonS3.generatePresignedUrl(any())).thenReturn(url);
        assertThat(awsS3Service.uploadFile(multipartFile))
                .isEqualTo(String.format("https://akininuniversity.s3.eu-central-1.amazonaws.com%s", uniqueFileName));
    }

    @Test
    void uploadShouldThrowUploadS3ExceptionWhenUploadFileFailed() throws IOException {
        File file = new File("src/test/resources/validimage.png");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("validimage", file.getName(), "image/png",
                IOUtils.toByteArray(input));
        when(amazonS3.generatePresignedUrl(any())).thenThrow(AmazonServiceException.class);
        assertThatThrownBy(() -> awsS3Service.uploadFile(multipartFile)).isInstanceOf(UploadS3Exception.class);
    }
}
