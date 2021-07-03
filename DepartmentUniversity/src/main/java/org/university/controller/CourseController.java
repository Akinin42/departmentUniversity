package org.university.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.university.dto.CourseDto;
import org.university.service.CourseService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RequestMapping("/courses")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CourseController {
    
    private static final String REDIRECT = "redirect:/courses";
    CourseService courseService;
    
    @GetMapping()
    public String getAll(Model model) {
        model.addAttribute("course", new CourseDto());
        model.addAttribute("courses", courseService.findAllCourses());
        return "courses";
    }
    
    @PostMapping()
    public String add(@ModelAttribute("course") CourseDto course) {        
        courseService.addCourse(course);  
        return REDIRECT;
    }
    
    @DeleteMapping()
    public String delete(@ModelAttribute("course") CourseDto course) {        
        courseService.delete(course);
        return REDIRECT;
    }
}
