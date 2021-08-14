package org.university.dao;

import java.util.Optional;

import org.university.entity.Teacher;

public interface TeacherDao extends CrudDao<Teacher, Integer> {

    Optional<Teacher> findByEmail(String email);
}
