package org.university.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.university.dto.ClassroomDto;
import org.university.exceptions.InvalidAddressException;
import org.university.exceptions.InvalidClassroomCapacityException;
import org.university.exceptions.InvalidClassroomNumberException;
import org.university.service.ClassroomService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RequestMapping("/classrooms")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ClassroomController {
    
    private static final String REDIRECT = "redirect:/classrooms";    
    ClassroomService classroomService;
    
    @GetMapping()
    public String getAll(@ModelAttribute("message") String message, Model model) {
        model.addAttribute("classroom", new ClassroomDto());
        model.addAttribute("classrooms", classroomService.findAllClassrooms());
        return "classrooms";
    }
    
    @PostMapping()
    public String add(@ModelAttribute("classroom") ClassroomDto classroom , Model model) {
        try {
        classroomService.addClassroom(classroom);  
        return REDIRECT;
        } catch (InvalidClassroomNumberException | InvalidClassroomCapacityException | InvalidAddressException e) {
            model.addAttribute("message", e.getMessage());
            return REDIRECT;
        }
    }
    
    @DeleteMapping()
    public String delete(@ModelAttribute("classroom") ClassroomDto classroom) {        
        classroomService.delete(classroom);
        return REDIRECT;
    }
}
