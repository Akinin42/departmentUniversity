package org.university.service.impl;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.university.service.AwsS3Service;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Service
public class AwsS3ServiceImpl implements AwsS3Service {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile multipartFile) {
        String time = LocalDateTime.now().toString().replace(".", "").replace("-", "").replace(":", "");
        String uniqueFileName = String.format("%s%s", time, multipartFile.getOriginalFilename());
        String fileURL = null;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());
        try {
            amazonS3.putObject(
                    new PutObjectRequest(bucketName, uniqueFileName, multipartFile.getInputStream(), metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
            GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName, uniqueFileName);
            URL url = amazonS3.generatePresignedUrl(urlRequest);
            String[] urlArr = url.toString().split("\\?");
            fileURL = urlArr[0];
            log.info("file is uploaded successfully");
        } catch (AmazonServiceException | IOException e) {
            log.error("file uploading is filed " + e.getMessage());
        }
        return fileURL;
    }
}
