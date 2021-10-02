package org.university.dao;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.university.entity.Teacher;

@Repository
public interface TeacherDao extends UserDao<Teacher> {
    
    Optional<Teacher> findByEmail(String email);
}
