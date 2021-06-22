package org.university.service;

import java.util.List;

import org.university.dto.TeacherDto;
import org.university.entity.Teacher;

public interface TeacherService extends UserService<Teacher> {
    Teacher login(String email, String password);
    
    void deleteTeacher (TeacherDto teacherDto);
    
    void registerTeacher(TeacherDto teacherDto);
    
    List<Teacher> findAll();
}
