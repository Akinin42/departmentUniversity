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
    private static final String REDIRECT = "redirect:/students";
    
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
    
    @GetMapping("/newStudent")
    public String addStudent(Model model) {       
        model.addAttribute("student", new StudentDto());        
        return "studentform";
    }

    @GetMapping("/other")
    public String getOtherStudents(@RequestParam("number") int inputNumber, Model model) {
        model.addAttribute("students", studentService.findNumberOfUsers(5, number));
        model.addAttribute("courses", courseService.findAllCourses());
        model.addAttribute("student", new StudentDto());
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
    
    @PostMapping("/addStudent")
    public String addStudent(@ModelAttribute("student") StudentDto student) {        
        studentService.registerStudent(student);        
        return REDIRECT;
    }
    
    @PostMapping("/delete")
    public String delete(@ModelAttribute("student") StudentDto student) {        
        studentService.deleteStudent(student);  
        return REDIRECT;
    }
    
    @PostMapping("/addCourse")
    public String addCourse(@ModelAttribute("student") StudentDto student) {
        Course course = courseService.createCourse(student.getCourseName());
        studentService.addStudentToCourse(student, course);         
        return REDIRECT;
    }
    
    @PostMapping("/deleteCourse")
    public String deleteCourse(@ModelAttribute("student") StudentDto student) {
        Course course = courseService.createCourse(student.getCourseName());
        studentService.deleteStudentFromCourse(student, course);         
        return REDIRECT;
    }

}
