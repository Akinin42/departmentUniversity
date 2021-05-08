package org.university.service;

import org.university.entity.Course;
import org.university.entity.Group;
import org.university.entity.Student;

public interface StudentService extends UserService<Student> {
    
    Student login (String email, String password);
    
    void addStudentToGroup(Student student, Group group);
    
    void deleteStudentFromGroup(Student student, Group group);
    
    void addStudentToCourse(Student student, Course course);
    
    void deleteStudentFromCourse(Student student, Course course);
}
