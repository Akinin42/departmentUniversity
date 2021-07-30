package org.university.service;

import org.springframework.web.multipart.MultipartFile;

public interface AwsS3Service {

    String uploadFile(final MultipartFile multipartFile);
}
