package org.university.dao.impl;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;
import org.university.dao.StudentDao;
import org.university.entity.Student;

@Repository
public class StudentDaoImpl extends AbstractCrudImpl<Student> implements StudentDao {

    public StudentDaoImpl(EntityManager entityManager) {
        super(entityManager, Student.class);
    }

    public void deleteById(Integer id) {
        Student student = findById(id).get();
        student.getCourses().clear();
        entityManager.remove(student);
    }

    @Override
    public Optional<Student> findByEmail(String email) {
        try {
            Student student = (Student) entityManager.createQuery("from Student where email = :email")
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.ofNullable(student);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
