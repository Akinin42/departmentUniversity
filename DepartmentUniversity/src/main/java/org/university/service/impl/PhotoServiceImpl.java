package org.university.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.university.dto.StudentDto;
import org.university.dto.UserDto;
import org.university.service.AwsS3Service;
import org.university.service.PhotoService;
import org.university.service.validator.PhotoValidator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;


@Component
@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PhotoServiceImpl implements PhotoService {

    AwsS3Service awsS3Service;
    PhotoValidator validator;

    @Override
    public String savePhoto(UserDto user) {
        String photoName = null;
        if (!user.getPhoto().isEmpty()) {
            validator.validate(user.getPhoto());
            photoName = awsS3Service.uploadFile(user.getPhoto());
        } else {
            if (user.getClass() == StudentDto.class) {
                photoName = user.getSex().equals("Male") ? "malestudent.png" : "femalestudent.png";
            } else {
                photoName = user.getSex().equals("Male") ? "maleteacher.png" : "femaleteacher.png";
            }
        }
        return photoName;
    }

    @Override
    public String createPhoto(String fileName) {
        String fileURL = null;
        List<String> defoltPhotos = new ArrayList<>();
        defoltPhotos.add("malestudent.png");
        defoltPhotos.add("femalestudent.png");
        defoltPhotos.add("maleteacher.png");
        defoltPhotos.add("femaleteacher.png");
        if(defoltPhotos.contains(fileName)) {
            fileURL = String.format("~/DepartmentUniversity/static/images/%s", fileName);
        }
        else {
            fileURL = fileName;
        }
        return fileURL;
    }

}
