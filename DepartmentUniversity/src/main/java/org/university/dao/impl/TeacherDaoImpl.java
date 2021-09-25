package org.university.dao.impl;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;
import org.university.dao.TeacherDao;
import org.university.entity.Teacher;

@Repository
public class TeacherDaoImpl extends AbstractCrudImpl<Teacher> implements TeacherDao {

    public TeacherDaoImpl(EntityManager entityManager) {
        super(entityManager, Teacher.class);
    }

    @Override
    public Optional<Teacher> findByEmail(String email) {
        try {
            Teacher teacher = (Teacher) entityManager.createQuery("from Teacher where email = :email")
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.ofNullable(teacher);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
