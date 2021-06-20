package org.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.university.service.TeacherService;

@Controller
@RequestMapping("/teachers")
public class TeacherController {
    
    private int number;
    private static final String REDIRECT = "redirect:/teachers";
    
    @Autowired
    private TeacherService teacherService;
    
    @GetMapping()
    public String getTeachers(Model model) {        
        model.addAttribute("teachers", teacherService.findNumberOfUsers(5, number));      
        return "teachers";
    }
    
    @GetMapping("/other")
    public String getOtherTeachers(@RequestParam("number") int inputNumber, Model model) {
        model.addAttribute("teachers", teacherService.findNumberOfUsers(5, number));
        number += inputNumber;
        if (number < 0) {
            number = 0;
        }        
        if (teacherService.findNumberOfUsers(5, number).isEmpty()) {
            number -= inputNumber;
        }
        model.addAttribute("teachers", teacherService.findNumberOfUsers(5, number));
        return "teachers";
    }

}
