package org.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.university.dto.UserDto;
import org.university.service.StudentService;
import org.university.service.TeacherService;

@Controller
@RequestMapping("/login")
public class LoginController {
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private TeacherService teacherService;    
    
    @PostMapping()
    public String login(@ModelAttribute("user") UserDto user) {        
        
        return null;
    }

}
