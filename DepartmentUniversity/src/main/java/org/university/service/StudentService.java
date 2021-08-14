package org.university.service;

import java.util.List;

import org.university.dto.StudentDto;
import org.university.entity.Course;
import org.university.entity.Student;

public interface StudentService extends UserService<Student> {
    
    Student login(String email, String password);

    void addStudentToCourse(StudentDto studentDto, Course course);

    void deleteStudentFromCourse(StudentDto studentDto, Course course);

    List<Student> findAll();
}
