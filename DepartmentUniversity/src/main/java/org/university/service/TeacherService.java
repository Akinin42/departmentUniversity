package org.university.service;

import org.university.entity.Teacher;

public interface TeacherService extends UserService<Teacher> {
    Teacher login(String email, String password);
}
