package org.university.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.university.dto.StudentDto;
import org.university.entity.Course;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EmailExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.exceptions.InvalidEmailException;
import org.university.exceptions.InvalidPhoneException;
import org.university.exceptions.InvalidPhotoException;
import org.university.exceptions.InvalidUserNameException;
import org.university.service.CourseService;
import org.university.service.PhotoService;
import org.university.service.StudentService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@RequestMapping("/students")
@SessionAttributes("numberUsers")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class StudentController {

    private static final String REDIRECT = "redirect:/students";
    private static final String STUDENT_FORM = "studentform";

    StudentService studentService;
    CourseService courseService;
    PhotoService photoService;

    @GetMapping()
    public String getStudents(@ModelAttribute("message") String message, Model model) {
        model.addAttribute("students", studentService.findNumberOfUsers(5, 0));
        model.addAttribute("courses", courseService.findAllCourses());
        model.addAttribute("student", new StudentDto());
        model.addAttribute("numberUsers", Integer.valueOf(0));
        return "students";
    }

    @GetMapping("/{page}")
    public String getOtherStudents(@PathVariable("page") int page, Model model) {
        model.addAttribute("students", null);
        model.addAttribute("courses", courseService.findAllCourses());
        model.addAttribute("student", new StudentDto());
        int number = (int) model.getAttribute("numberUsers") + (page * 5);
        if (number < 0) {
            number = 0;
        }
        if (studentService.findNumberOfUsers(5, number).isEmpty()) {
            number -= (page * 5);
        }
        model.addAttribute("students", studentService.findNumberOfUsers(5, number));
        return "students";
    }

    @GetMapping("/new")
    public String newStudent(Model model) {
        model.addAttribute("student", new StudentDto());
        return STUDENT_FORM;
    }

    @PostMapping()
    public String addStudent(@ModelAttribute("student") StudentDto student, Model model) {
        try {
            String photoName = photoService.savePhoto(student);
            student.setPhotoName(photoName);
            studentService.register(student);
            return REDIRECT;
        } catch (InvalidEmailException | InvalidPhoneException | InvalidUserNameException | EmailExistException | InvalidPhotoException e) {
            model.addAttribute("message", e.getMessage());
            return STUDENT_FORM;
        }
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

    @PostMapping("/login")
    public String login(@ModelAttribute("student") StudentDto studentDto, Model model) {
        try {
            model.addAttribute("student", studentService.login(studentDto.getEmail(), studentDto.getPassword()));
            return "studentprofile";
        } catch (EntityNotExistException e) {
            return STUDENT_FORM;
        } catch (AuthorisationFailException e) {
            model.addAttribute("message", "passworddontcorrect");
            return REDIRECT;
        }
    }

    @PostMapping("/edit")
    public String getEditForm(@ModelAttribute("student") StudentDto student, @ModelAttribute("message") String message,
            Model model) {
        model.addAttribute("student", student);
        return "updateforms/student";
    }

    @PatchMapping()
    public String edit(@ModelAttribute("student") StudentDto student, Model model) {
        try {
            studentService.edit(student);
            return REDIRECT;
        } catch (InvalidEmailException | InvalidPhoneException | InvalidUserNameException | EmailExistException e) {
            model.addAttribute("message", e.getMessage());
            return "updateforms/student";
        }
    }
}
