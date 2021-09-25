package org.university.dao.impl;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;
import org.university.dao.CourseDao;
import org.university.entity.Course;

@Repository
public class CourseDaoImpl extends AbstractCrudImpl<Course> implements CourseDao {

    public CourseDaoImpl(EntityManager entityManager) {
        super(entityManager, Course.class);
    }

    @Override
    public Optional<Course> findByName(String name) {
        try {
            Course course = (Course) entityManager.createQuery("from Course where course_name = :name")
                    .setParameter("name", name)
                    .getSingleResult();
            return Optional.ofNullable(course);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
