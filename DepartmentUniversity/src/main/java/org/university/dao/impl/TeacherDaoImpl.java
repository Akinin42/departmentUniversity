package org.university.dao.impl;

import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.university.dao.TeacherDao;
import org.university.entity.Teacher;

@Repository
public class TeacherDaoImpl extends AbstractCrudImpl<Teacher> implements TeacherDao {

    public TeacherDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Teacher.class);
    }

    @Override
    public Optional<Teacher> findByEmail(String email) {
        Teacher teacher = (Teacher) sessionFactory.getCurrentSession().createQuery("from Teacher where email = :email")
                .setParameter("email", email).uniqueResult();
        return Optional.ofNullable(teacher);
    }
}
