package org.university.controller.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.university.dto.UserDto;
import org.university.entity.TemporaryUser;
import org.university.service.StudentService;
import org.university.service.TeacherService;
import org.university.service.TemporaryUserService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/v1/requests")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserRequestControllerRest {

    StudentService studentService;
    TeacherService teacherService;
    TemporaryUserService temporaryUserService;

    @PostMapping()
    public void registerUser(@RequestBody UserDto user) {
        TemporaryUser temporaryUser = temporaryUserService.getByEmail(user.getEmail());
        user = temporaryUserService.mapEntityToDto(temporaryUser);
        if (user.getDesiredRole().equals("STUDENT")) {
            studentService.register(user);
        } else {
            teacherService.register(user);
        }
        temporaryUserService.delete(user);
    }

    @PostMapping("/unconfirm")
    public void unconfirmUser(@RequestBody UserDto user) {
        temporaryUserService.addConfirmDescription(user);        
    }
}
