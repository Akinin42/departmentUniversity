package org.university.service;

import java.util.List;

import org.university.dto.CourseDto;
import org.university.entity.Course;

public interface CourseService {
    
    Course createCourse(String courseName);

    void addCourse(CourseDto courseDto);
    
    List<Course> findAllCourses();
    
    void delete(Course course);

}
