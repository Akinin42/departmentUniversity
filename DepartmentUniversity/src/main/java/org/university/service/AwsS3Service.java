package org.university.service;

import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.model.S3Object;

public interface AwsS3Service {

    String uploadFile(MultipartFile multipartFile);
    
    S3Object downloadFile(String fileName);
}
