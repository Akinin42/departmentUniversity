package org.university.controller.rest;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.university.dto.CourseDto;
import org.university.entity.Course;
import org.university.service.CourseService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/api/v1/courses")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CourseControllerRest {

    CourseService courseService;

    @GetMapping()
    public List<Course> getAll() {
        return courseService.findAllCourses();
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@Valid @RequestBody CourseDto course) {
        courseService.addCourse(course);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.OK)
    public void delete(@RequestBody CourseDto course) {
        courseService.delete(course);
    }

    @PatchMapping()
    @ResponseStatus(HttpStatus.OK)
    public void edit(@Valid @RequestBody CourseDto course) {
        courseService.edit(course);
    }
}
