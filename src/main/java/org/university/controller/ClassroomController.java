package org.university.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.university.dto.ClassroomDto;
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
    public String add(@ModelAttribute("classroom") @Valid ClassroomDto classroom, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("classrooms", classroomService.findAllClassrooms());
            return "classrooms";
        }
        classroomService.addClassroom(classroom);
        return REDIRECT;
    }

    @DeleteMapping()
    public String delete(@ModelAttribute("classroom") ClassroomDto classroom) {
        classroomService.delete(classroom);
        return REDIRECT;
    }

    @PostMapping("/edit")
    public String getEditForm(@ModelAttribute("classroom") ClassroomDto classroom,
            @ModelAttribute("message") String message, Model model) {
        model.addAttribute("classroom", classroom);
        return "updateforms/classroom";
    }

    @PatchMapping()
    public String edit(@ModelAttribute("classroom") @Valid ClassroomDto classroom, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "updateforms/classroom";
        }
        classroomService.edit(classroom);
        return REDIRECT;
    }
}
