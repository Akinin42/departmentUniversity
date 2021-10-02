package org.university.dao;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.university.entity.Student;

@Repository
public interface StudentDao extends UserDao<Student> {
    
    Optional<Student> findByEmail(String email);
}
