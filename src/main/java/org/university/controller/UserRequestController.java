package org.university.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.university.dto.UserDto;
import org.university.entity.TemporaryUser;
import org.university.service.StudentService;
import org.university.service.TeacherService;
import org.university.service.TemporaryUserService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RequestMapping("/requests")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserRequestController {
    
    StudentService studentService;
    TeacherService teacherService;
    TemporaryUserService temporaryUserService;
    
    @PostMapping()
    public String registerUser(@ModelAttribute("user") UserDto user, Model model) {
        TemporaryUser temporaryUser = temporaryUserService.getByEmail(user.getEmail());
        user = temporaryUserService.mapEntityToDto(temporaryUser);        
        if(user.getDesiredRole().equals("STUDENT")) {
            studentService.register(user);            
        } else {
            teacherService.register(user);
        }
        temporaryUserService.delete(user);
        return "redirect:/temporary";
    }
    
    @PostMapping("/unconfirm")
    public String unconfirmUser(@ModelAttribute("user") UserDto user, Model model) {               
        temporaryUserService.addConfirmDescription(user);
        return "redirect:/temporary";
    }    
}
