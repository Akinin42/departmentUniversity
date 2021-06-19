package org.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.university.dto.StudentDto;
import org.university.service.StudentService;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;
    private int number;

    @GetMapping()
    public String getStudents(Model model) {        
        model.addAttribute("student", new StudentDto());
        model.addAttribute("students", studentService.findNumberOfUsers(5, number));
        return "students";
    }

    @GetMapping("/other")
    public String getOtherStudents(@RequestParam("number")int inputNumber, Model model) {
        number += inputNumber;        
        model.addAttribute("student", new StudentDto());
        model.addAttribute("students", studentService.findNumberOfUsers(5, number));
        return "students";
    }

}
