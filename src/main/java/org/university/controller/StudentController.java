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
import org.university.dto.StudentDto;
import org.university.entity.Course;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EmailExistException;
import org.university.exceptions.InvalidPhotoException;
import org.university.service.CourseService;
import org.university.service.PhotoService;
import org.university.service.StudentService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RequestMapping("/students")
@SessionAttributes({ "pagesNumber", "numberUsers" })
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class StudentController {

    private static final String REDIRECT = "redirect:/students";
    private static final String UPDATE_STUDENT_FORM = "updateforms/student";

    StudentService studentService;
    CourseService courseService;
    PhotoService photoService;

    @GetMapping()
    public String getStudents(@ModelAttribute("message") String message, Model model) {
        if (model.getAttribute("numberUsers") == null) {
            model.addAttribute("students", studentService.findNumberOfUsers(5, 0));
            model.addAttribute("numberUsers", Integer.valueOf(5));
        } else {
            model.addAttribute("students",
                    studentService.findNumberOfUsers((int) model.getAttribute("numberUsers"), 0));
        }
        model.addAttribute("courses", courseService.findAllCourses());
        model.addAttribute("student", new StudentDto());
        model.addAttribute("pagesNumber", Integer.valueOf(0));
        return "students";
    }

    @GetMapping("/{page}")
    public String getOtherStudents(@PathVariable("page") int page, Model model) {
        model.addAttribute("students", null);
        model.addAttribute("courses", courseService.findAllCourses());
        model.addAttribute("student", new StudentDto());
        int pagesNumber = (int) model.getAttribute("pagesNumber") + page;
        int numberStudentsOnPage = (int) model.getAttribute("numberUsers");
        if (pagesNumber < 0) {
            pagesNumber = 0;
        }
        if (studentService.findNumberOfUsers(numberStudentsOnPage, pagesNumber).isEmpty()) {
            pagesNumber -= page;
        }
        model.addAttribute("pagesNumber", pagesNumber);
        model.addAttribute("students", studentService.findNumberOfUsers(numberStudentsOnPage, pagesNumber));
        return "students";
    }

    @GetMapping("/numbers/{numbers}")
    public String setNumberUsers(@PathVariable("numbers") int numbers, Model model) {
        model.addAttribute("numberUsers", numbers);
        return REDIRECT;
    }

    @DeleteMapping()
    public String delete(@ModelAttribute("student") StudentDto student) {
        studentService.delete(student);
        return REDIRECT;
    }

    @PostMapping("/course")
    public String addCourse(@ModelAttribute("student") StudentDto student) {
        Course course = courseService.createCourse(student.getCourseName());
        studentService.addStudentToCourse(student, course);
        return REDIRECT;
    }

    @DeleteMapping("/course")
    public String deleteCourse(@ModelAttribute("student") StudentDto student) {
        Course course = courseService.createCourse(student.getCourseName());
        studentService.deleteStudentFromCourse(student, course);
        return REDIRECT;
    }

    @PostMapping("/profile")
    public String getProfile(@ModelAttribute("student") StudentDto studentDto, Model model) {
        model.addAttribute("user", studentService.getByEmail(studentDto.getEmail()));
        return "userprofile";
    }

    @PostMapping("/edit")
    public String getEditForm(@ModelAttribute("student") StudentDto student, @ModelAttribute("message") String message,
            Model model) {
        model.addAttribute("student", student);
        return UPDATE_STUDENT_FORM;
    }

    @PostMapping("/update")
    public String edit(@ModelAttribute("student") @Valid StudentDto student, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            if (bindingResult.hasFieldErrors("password")) {
                model.addAttribute("message", bindingResult.getFieldError("password").getDefaultMessage());
            }
            return UPDATE_STUDENT_FORM;
        }
        try {
            String photoName = photoService.savePhoto(student);
            student.setPhotoName(photoName);
            studentService.edit(student);
            return REDIRECT;
        } catch (EmailExistException | InvalidPhotoException | AuthorisationFailException e) {
            model.addAttribute("message", e.getMessage());
            return UPDATE_STUDENT_FORM;
        }
    }
}
