package org.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.university.dto.StudentDto;
import org.university.service.StudentService;

@Controller
@RequestMapping("/students")
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    @GetMapping()
    public String getFirstTenStudents(Model model) {
        int number = 0;
        model.addAttribute("number", number);
        model.addAttribute("student", new StudentDto());
        model.addAttribute("students", studentService.findNumberOfUsers(10, number));
        return "students";
    }

}
