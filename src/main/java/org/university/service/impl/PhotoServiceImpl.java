package org.university.service.impl;

import org.springframework.stereotype.Service;
import org.university.dto.StudentDto;
import org.university.dto.UserDto;
import org.university.service.AwsS3Service;
import org.university.service.PhotoService;
import org.university.service.validator.PhotoValidator;
import org.university.utils.Sex;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PhotoServiceImpl implements PhotoService {

    private static final String DEFAULT_PATH = "~/university/static/images/";

    AwsS3Service awsS3Service;
    PhotoValidator validator;

    @Override
    public String savePhoto(UserDto user) {
        String photoName = null;
        if (!user.getPhoto().isEmpty()) {
            validator.validate(user.getPhoto());
            photoName = awsS3Service.uploadFile(user.getPhoto());
        } else {
            if (checkUserIsStudent(user)) {
                photoName = checkUserIsMan(user) ? String.format("%smalestudent.png", DEFAULT_PATH)
                        : String.format("%sfemalestudent.png", DEFAULT_PATH);
            } else {
                photoName = checkUserIsMan(user) ? String.format("%smaleteacher.png", DEFAULT_PATH)
                        : String.format("%sfemaleteacher.png", DEFAULT_PATH);
            }
        }
        return photoName;
    }

    private boolean checkUserIsStudent(UserDto user) {
        return (user.getClass() == StudentDto.class
                || (user.getDesiredRole() != null && user.getDesiredRole().equals("STUDENT")));
    }

    private boolean checkUserIsMan(UserDto user) {
        return user.getSex() == Sex.MALE;
    }
}
