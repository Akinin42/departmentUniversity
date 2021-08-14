package org.university.dao.impl;

import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.university.dao.ClassroomDao;
import org.university.entity.Classroom;

@Repository
public class ClassroomDaoImpl extends AbstractCrudImpl<Classroom> implements ClassroomDao {

    public ClassroomDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Classroom.class);
    }

    @Override
    public Optional<Classroom> findByNumber(int number) {
        Classroom classroom = (Classroom) sessionFactory.getCurrentSession().createQuery("from Classroom where classroom_number = :number")
                .setParameter("number", number).uniqueResult();
        return Optional.ofNullable(classroom);
    }
}
