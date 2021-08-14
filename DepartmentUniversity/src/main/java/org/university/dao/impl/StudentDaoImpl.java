package org.university.dao.impl;

import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.university.dao.StudentDao;
import org.university.entity.Student;

@Repository
public class StudentDaoImpl extends AbstractCrudImpl<Student> implements StudentDao {

    public StudentDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Student.class);
    }

    public void deleteById(Integer id) {
        Student student = findById(id).get();
        student.getCourses().clear();
        sessionFactory.getCurrentSession().delete(student);
    }

    @Override
    public Optional<Student> findByEmail(String email) {
        Student student = (Student) sessionFactory.getCurrentSession().createQuery("from Student where email = :email")
                .setParameter("email", email).uniqueResult();
        return Optional.ofNullable(student);
    }
}
