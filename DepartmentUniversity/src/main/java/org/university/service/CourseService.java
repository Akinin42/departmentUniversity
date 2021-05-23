package org.university.service;

import java.util.List;

import org.university.entity.Course;

public interface CourseService {
    
    Course createCourse(String courseName);

    void addCourse(Course course);
    
    List<Course> findAllCourses();
    
    void delete(Course course);

}
