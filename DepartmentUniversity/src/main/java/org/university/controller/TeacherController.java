package org.university.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.university.dto.DayTimetableDto;
import org.university.dto.TeacherDto;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EntityNotExistException;
import org.university.exceptions.InvalidEmailException;
import org.university.exceptions.InvalidPhoneException;
import org.university.exceptions.InvalidUserNameException;
import org.university.service.TeacherService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RequestMapping("/teachers")
@SessionAttributes("numberUsers")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class TeacherController {

    private TeacherService teacherService;
    private static final String REDIRECT = "redirect:/teachers";
    private static final String TEACHER_FORM = "teacherform";

    @GetMapping()
    public String getTeachers(@ModelAttribute("message") String message, Model model) {
        model.addAttribute("teachers", teacherService.findNumberOfUsers(5, 0));
        model.addAttribute("teacher", new TeacherDto());
        model.addAttribute("timetable", new DayTimetableDto());
        model.addAttribute("numberUsers", Integer.valueOf(0));
        return "teachers";
    }

    @GetMapping("/{page}")
    public String getOtherTeachers(@PathVariable("page") int page, Model model) {
        model.addAttribute("teachers", null);
        model.addAttribute("teacher", new TeacherDto());
        model.addAttribute("timetable", new DayTimetableDto());
        int number = (int) model.getAttribute("numberUsers") + (page * 5);
        if (number < 0) {
            number = 0;
        }
        if (teacherService.findNumberOfUsers(5, number).isEmpty()) {
            number -= (page * 5);
        }
        model.addAttribute("teachers", teacherService.findNumberOfUsers(5, number));
        return "teachers";
    }

    @GetMapping("/new")
    public String newTeacher(Model model) {
        model.addAttribute("teacher", new TeacherDto());
        return TEACHER_FORM;
    }

    @PostMapping()
    public String addTeacher(@ModelAttribute("teacher") TeacherDto teacher, Model model) {
        try {
            teacherService.register(teacher);
            return REDIRECT;
        } catch (InvalidEmailException | InvalidPhoneException | InvalidUserNameException e) {
            model.addAttribute("message", e.getMessage());
            return TEACHER_FORM;
        }
    }

    @DeleteMapping()
    public String delete(@ModelAttribute("teacher") TeacherDto teacher) {
        teacherService.delete(teacher);
        return REDIRECT;
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("teacher") TeacherDto teacherDto, Model model) {
        try {
            model.addAttribute("teacher", teacherService.login(teacherDto.getEmail(), teacherDto.getPassword()));
            return "teacherprofile";
        } catch (EntityNotExistException e) {
            return TEACHER_FORM;
        } catch (AuthorisationFailException e) {
            model.addAttribute("message", "Password isn't correct!");
            return REDIRECT;
        }
    }
}
