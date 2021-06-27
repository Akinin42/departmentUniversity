package org.university.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.university.dto.ClassroomDto;
import org.university.service.ClassroomService;

@Controller
@RequestMapping("/classrooms")
public class ClassroomController {
    
    private static final String REDIRECT = "redirect:/classrooms";
    
    @Autowired
    private ClassroomService classroomService;
    
    @GetMapping()
    public String getAll(Model model) {
        model.addAttribute("classroom", new ClassroomDto());
        model.addAttribute("classrooms", classroomService.findAllClassrooms());
        return "classrooms";
    }
    
    @PostMapping("/add")
    public String add(@ModelAttribute("classroom") ClassroomDto classroom) {        
        classroomService.addClassroom(classroom);  
        return REDIRECT;
    }
    
    @PostMapping("/delete")
    public String delete(@ModelAttribute("classroom") ClassroomDto classroom) {        
        classroomService.delete(classroom);
        return REDIRECT;
    }
}
