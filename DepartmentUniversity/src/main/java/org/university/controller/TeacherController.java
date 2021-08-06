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
import org.university.exceptions.EmailExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.exceptions.InvalidEmailException;
import org.university.exceptions.InvalidPhoneException;
import org.university.exceptions.InvalidPhotoException;
import org.university.exceptions.InvalidUserNameException;
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
    private static final String TEACHER_FORM = "teacherform";

    private TeacherService teacherService;
    PhotoService photoService;

    @GetMapping()
    public String getTeachers(@ModelAttribute("message") String message, Model model) {
        if (model.getAttribute("numberUsers") == null) {
            model.addAttribute("teachers", teacherService.findNumberOfUsers(5, 0));
        } else {
            model.addAttribute("teachers",
                    teacherService.findNumberOfUsers((int) model.getAttribute("numberUsers"), 0));
        }
        model.addAttribute("teacher", new TeacherDto());
        model.addAttribute("timetable", new DayTimetableDto());
        model.addAttribute("pagesNumber", Integer.valueOf(0));
        return "teachers";
    }

    @GetMapping("/{page}")
    public String getOtherTeachers(@PathVariable("page") int page, Model model) {
        model.addAttribute("teachers", null);
        model.addAttribute("teacher", new TeacherDto());
        model.addAttribute("timetable", new DayTimetableDto());
        int pagesNumber = (int) model.getAttribute("pagesNumber") + page;
        int numberTeachersOnPage = (int) model.getAttribute("numberUsers");
        if (pagesNumber < 0) {
            pagesNumber = 0;
        }
        if (teacherService.findNumberOfUsers(numberTeachersOnPage, (pagesNumber * numberTeachersOnPage)).isEmpty()) {
            pagesNumber -= page;
        }
        model.addAttribute("numberUsers", pagesNumber);
        model.addAttribute("teachers",
                teacherService.findNumberOfUsers(numberTeachersOnPage, pagesNumber * numberTeachersOnPage));
        return "teachers";
    }

    @GetMapping("/new")
    public String newTeacher(Model model) {
        model.addAttribute("teacher", new TeacherDto());
        return TEACHER_FORM;
    }
    
    @GetMapping("/numbers/{numbers}")
    public String numberStudents(@PathVariable("numbers") int numbers, Model model) {
        model.addAttribute("numberUsers", numbers);
        return REDIRECT;
    }

    @PostMapping()
    public String addTeacher(@ModelAttribute("teacher") TeacherDto teacher, Model model) {
        try {
            String photoName = photoService.savePhoto(teacher);
            teacher.setPhotoName(photoName);
            teacherService.register(teacher);
            return REDIRECT;
        } catch (InvalidEmailException | InvalidPhoneException | InvalidUserNameException | EmailExistException
                | InvalidPhotoException e) {
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
        }
    }

    @PostMapping("/edit")
    public String getEditForm(@ModelAttribute("teacher") TeacherDto teacher, @ModelAttribute("message") String message,
            Model model) {
        model.addAttribute("teacher", teacher);
        return "updateforms/teacher";
    }

    @PostMapping("/update")
    public String edit(@ModelAttribute("teacher") TeacherDto teacher, Model model) {
        try {
            String photoName = photoService.savePhoto(teacher);
            teacher.setPhotoName(photoName);
            teacherService.edit(teacher);
            return REDIRECT;
        } catch (InvalidEmailException | InvalidPhoneException | InvalidUserNameException | EmailExistException e) {
            model.addAttribute("message", e.getMessage());
            return "updateforms/teacher";
        }
    }
}
