package org.university.dao;

import java.util.Optional;

import org.university.entity.Student;

public interface StudentDao extends CrudDao<Student, Integer> {

    Optional<Student> findByEmail(String email);
}
