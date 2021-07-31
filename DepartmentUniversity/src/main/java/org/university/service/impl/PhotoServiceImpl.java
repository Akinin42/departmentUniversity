package org.university.service.impl;

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
                photoName = user.getSex().equals("male") ? "malestudent.png" : "femalestudent.png";
            } else {
                photoName = user.getSex().equals("male") ? "maleteacher.png" : "femaleteacher.png";
            }
        }
        return photoName;
    }

    @Override
    public void createPhoto(String fileName) {
        // TODO Auto-generated method stub

    }

}
