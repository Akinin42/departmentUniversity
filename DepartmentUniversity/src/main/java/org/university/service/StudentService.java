package org.university.service;

import java.util.List;
import org.university.dto.StudentDto;
import org.university.entity.Course;
import org.university.entity.Group;
import org.university.entity.Student;

public interface StudentService extends UserService<Student> {
    
    void registerStudent(StudentDto studentDto);
    
    void deleteStudent(StudentDto studentDto);

    Student login(String email, String password);

    void addStudentToGroup(StudentDto studentDto, Group group);

    void deleteStudentFromGroup(StudentDto studentDto, Group group);

    void addStudentToCourse(StudentDto studentDto, Course course);

    void deleteStudentFromCourse(StudentDto studentDto, Course course);

    List<Student> findAll();
}
