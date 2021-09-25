package org.university.dao.impl;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;
import org.university.dao.ClassroomDao;
import org.university.entity.Classroom;

@Repository
public class ClassroomDaoImpl extends AbstractCrudImpl<Classroom> implements ClassroomDao {

    public ClassroomDaoImpl(EntityManager entityManager) {
        super(entityManager, Classroom.class);
    }

    @Override
    public Optional<Classroom> findByNumber(int number) {
        try {
            Classroom classroom = (Classroom) entityManager
                    .createQuery("from Classroom where classroom_number = :number")
                    .setParameter("number", number)
                    .getSingleResult();
            return Optional.ofNullable(classroom);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
