package org.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.university.dto.StudentDto;
import org.university.entity.Course;
import org.university.service.CourseService;
import org.university.service.StudentService;

@Controller
@RequestMapping("/students")
public class StudentController {

    private int number;
    @Autowired
    private StudentService studentService;
    @Autowired
    private CourseService courseService;

    @GetMapping()
    public String getStudents(Model model) {        
        model.addAttribute("students", studentService.findNumberOfUsers(5, number));
        model.addAttribute("courses", courseService.findAllCourses());
        model.addAttribute("student", new StudentDto());        
        return "students";
    }

    @GetMapping("/other")
    public String getOtherStudents(@RequestParam("number") int inputNumber, Model model) {
        number += inputNumber;
        if (number < 0) {
            number = 0;
        }        
        if (studentService.findNumberOfUsers(5, number).isEmpty()) {
            number -= inputNumber;
        }
        model.addAttribute("students", studentService.findNumberOfUsers(5, number));
        return "students";
    }
    
    @PostMapping("/addCourse")
    public String addCourse(@ModelAttribute("student") StudentDto student) {
        Course course = courseService.createCourse(student.getCourseName());
        studentService.addStudentToCourse(student, course);         
        return "redirect:/students";
    }

}
