package org.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.university.dto.CourseDto;
import org.university.service.CourseService;

@Controller
@RequestMapping("/courses")
public class CourseController {
    
    @Autowired
    private CourseService courseService;
    
    @GetMapping()
    public String getAll(Model model) {
        model.addAttribute("course", new CourseDto());
        model.addAttribute("courses", courseService.findAllCourses());
        return "courses";
    }
    
    @PostMapping()
    public String add(@ModelAttribute("course") CourseDto course) {        
        courseService.addCourse(course);  
        return "redirect:/courses";
    }
    
    @PostMapping("/delete")
    public String delete(@ModelAttribute("course") CourseDto course) {        
//        groupService.delete(group);
        return "redirect:/courses";
    }
}
