package org.university.service;

import java.util.List;
import org.university.entity.Teacher;

public interface TeacherService extends UserService<Teacher> {
    
    Teacher login(String email, String password);
    
    List<Teacher> findAll();
}
