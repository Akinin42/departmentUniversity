package org.university.controller.rest;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.university.dto.StudentDto;
import org.university.entity.Course;
import org.university.entity.Student;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EmailExistException;
import org.university.exceptions.InvalidPhotoException;
import org.university.service.CourseService;
import org.university.service.PhotoService;
import org.university.service.StudentService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/v1/students")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class StudentControllerRest {

    StudentService studentService;
    CourseService courseService;
    PhotoService photoService;

    @GetMapping(params = { "page", "size" })
    public List<Student> getStudents(@RequestParam("page") int page, @RequestParam("size") int size) {
        int lastPage = getLastPage(size);
        if (page < 0) {
            page = 0;
        }
        if (studentService.findNumberOfUsers(size, page).isEmpty()) {
            page = lastPage;
        }
        return studentService.findNumberOfUsers(size, page);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.OK)
    public void delete(@RequestBody StudentDto student) {
        studentService.delete(student);
    }

    @PostMapping("/course")
    @ResponseStatus(HttpStatus.OK)
    public void addCourse(@RequestBody StudentDto student) {
        Course course = courseService.createCourse(student.getCourseName());
        studentService.addStudentToCourse(student, course);
    }

    @DeleteMapping("/course")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCourse(@RequestBody StudentDto student) {
        Course course = courseService.createCourse(student.getCourseName());
        studentService.deleteStudentFromCourse(student, course);
    }

    @GetMapping("/{email}")
    public Student findStudentByEmail(@PathVariable("email") String email) {
        return studentService.getByEmail(email);
    }

    @PostMapping(consumes = { "multipart/form-data" })
    @ResponseStatus(HttpStatus.OK)
    public void edit(@Valid @RequestPart("student") StudentDto student, @RequestPart("photo") MultipartFile photo) {
        try {
            student.setPhoto(photo);
            String photoName = photoService.savePhoto(student);
            student.setPhotoName(photoName);
            studentService.edit(student);
        } catch (EmailExistException | InvalidPhotoException | AuthorisationFailException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private int getLastPage(int size) {
        if ((studentService.findAll().size() % size) != 0) {
            return studentService.findAll().size() / size;
        } else {
            return (studentService.findAll().size() / size) - 1;
        }
    }
}
