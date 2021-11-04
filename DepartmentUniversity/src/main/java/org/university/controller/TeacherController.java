package org.university.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.university.dto.DayTimetableDto;
import org.university.dto.UserDto;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EmailExistException;
import org.university.exceptions.InvalidPhotoException;
import org.university.service.PhotoService;
import org.university.service.TeacherService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RequestMapping("/teachers")
@SessionAttributes({ "pagesNumber", "numberUsers" })
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class TeacherController {

    private static final String REDIRECT = "redirect:/teachers";
    private static final String UPDATE_TEACHER_FORM = "updateforms/teacher";

    TeacherService teacherService;
    PhotoService photoService;

    @GetMapping()
    public String getTeachers(@ModelAttribute("message") String message, Model model) {
        if (model.getAttribute("numberUsers") == null) {
            model.addAttribute("teachers", teacherService.findNumberOfUsers(5, 0));
            model.addAttribute("numberUsers", Integer.valueOf(5));
        } else {
            model.addAttribute("teachers",
                    teacherService.findNumberOfUsers((int) model.getAttribute("numberUsers"), 0));
        }
        model.addAttribute("teacher", new UserDto());
        model.addAttribute("timetable", new DayTimetableDto());
        model.addAttribute("pagesNumber", Integer.valueOf(0));
        return "teachers";
    }

    @GetMapping("/{page}")
    public String getOtherTeachers(@PathVariable("page") int page, Model model) {
        model.addAttribute("teachers", null);
        model.addAttribute("teacher", new UserDto());
        model.addAttribute("timetable", new DayTimetableDto());
        int pagesNumber = (int) model.getAttribute("pagesNumber") + page;
        int numberTeachersOnPage = (int) model.getAttribute("numberUsers");
        if (pagesNumber < 0) {
            pagesNumber = 0;
        }
        if (teacherService.findNumberOfUsers(numberTeachersOnPage, pagesNumber).isEmpty()) {
            pagesNumber -= page;
        }
        model.addAttribute("pagesNumber", pagesNumber);
        model.addAttribute("teachers", teacherService.findNumberOfUsers(numberTeachersOnPage, pagesNumber));
        return "teachers";
    }

    @GetMapping("/numbers/{numbers}")
    public String setNumberUsers(@PathVariable("numbers") int numbers, Model model) {
        model.addAttribute("numberUsers", numbers);
        return REDIRECT;
    }

    @DeleteMapping()
    public String delete(@ModelAttribute("teacher") UserDto teacher) {
        teacherService.delete(teacher);
        return REDIRECT;
    }

    @PostMapping("/profile")
    public String getProfile(@ModelAttribute("teacher") UserDto userDto, Model model) {
        model.addAttribute("user", teacherService.getByEmail(userDto.getEmail()));
        return "userprofile";
    }

    @PostMapping("/edit")
    public String getEditForm(@ModelAttribute("teacher") UserDto teacher, @ModelAttribute("message") String message,
            Model model) {
        model.addAttribute("teacher", teacher);
        return UPDATE_TEACHER_FORM;
    }

    @PostMapping("/update")
    public String edit(@ModelAttribute("teacher") @Valid UserDto teacher, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            if (bindingResult.hasFieldErrors("password")) {
                model.addAttribute("message", bindingResult.getFieldError("password").getDefaultMessage());
            }
            return UPDATE_TEACHER_FORM;
        }
        try {
            String photoName = photoService.savePhoto(teacher);
            teacher.setPhotoName(photoName);
            teacherService.edit(teacher);
            return REDIRECT;
        } catch (EmailExistException | InvalidPhotoException | AuthorisationFailException e) {
            model.addAttribute("message", e.getMessage());
            return UPDATE_TEACHER_FORM;
        }
    }
}
