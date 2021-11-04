package org.university.service;

import java.util.List;

import org.university.entity.Teacher;

public interface TeacherService extends UserService<Teacher> {
    
    List<Teacher> findAll();
}
