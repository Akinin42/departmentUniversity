package org.university.dao.impl;

import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.university.dao.CourseDao;
import org.university.entity.Course;

@Repository
public class CourseDaoImpl extends AbstractCrudImpl<Course> implements CourseDao {

    public CourseDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Course.class);
    }

    @Override
    public Optional<Course> findByName(String name) {
        Course course = (Course) sessionFactory.getCurrentSession().createQuery("from Course where course_name = :name")
                .setParameter("name", name).uniqueResult();
        return Optional.ofNullable(course);
    }
}
