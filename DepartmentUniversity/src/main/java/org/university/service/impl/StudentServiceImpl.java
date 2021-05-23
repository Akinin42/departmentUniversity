package org.university.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.university.dao.CourseDao;
import org.university.dao.GroupDao;
import org.university.dao.StudentDao;
import org.university.entity.Course;
import org.university.entity.Group;
import org.university.entity.Student;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EntityNotExistException;
import org.university.service.StudentService;
import org.university.service.validator.UserValidator;

@Component
public class StudentServiceImpl extends AbstractUserServiceImpl<Student> implements StudentService {

    private final StudentDao studentDao;
    private final GroupDao groupDao;
    private final CourseDao courseDao;
    private final PasswordEncoder encoder;

    public StudentServiceImpl(StudentDao studentDao, GroupDao groupDao, CourseDao courseDao,
            UserValidator<Student> validator, PasswordEncoder encoder) {
        super(studentDao, validator);
        this.studentDao = studentDao;
        this.groupDao = groupDao;
        this.courseDao = courseDao;
        this.encoder = encoder;
    }

    @Override
    public Student login(String email, String password) {
        if(!studentDao.findByEmail(email).isPresent()) {
            throw new EntityNotExistException();
        }
        Student student = studentDao.findByEmail(email).get();
        if (!encoder.matches(password, student.getPassword())) {
            throw new AuthorisationFailException();
        }
        return student;
    }

    @Override
    protected Student mapUserWithPassword(Student user) {
        return Student.builder()
                .withSex(user.getSex())
                .withName(user.getName())
                .withEmail(user.getEmail())
                .withPhone(user.getPhone())
                .withPassword(encoder.encode(user.getPassword()))
                .build();
    }

    @Override
    public void addStudentToGroup(Student student, Group group) {
        existsStudentAndGroup(student, group);
        if (!studentDao.findAllByGroup(group.getName()).contains(student)) {
            studentDao.insertStudentToGroup(student, group);
        }
    }

    @Override
    public void deleteStudentFromGroup(Student student, Group group) {
        existsStudentAndGroup(student, group);        
        studentDao.deleteStudentFromGroup(student.getId(), group.getId());        
    }

    @Override
    public void addStudentToCourse(Student student, Course course) {
        existsStudentAndCourse(student, course);
        if (!studentDao.findAllByCourse(course.getName()).contains(student)) {
            List<Course> courses = new ArrayList<>();
            courses.add(course);
            studentDao.insertStudentToCourses(student, courses);
        }
    }

    @Override
    public void deleteStudentFromCourse(Student student, Course course) {
        existsStudentAndCourse(student, course);        
        studentDao.deleteStudentFromCourse(student.getId(), course.getId());        
    }

    private void existsStudentAndCourse(Student student, Course course) {
        if(student == null || course == null) {
            throw new IllegalArgumentException();
        }
        if (!existsUser(student) || courseDao.findById(course.getId()).equals(Optional.empty())) {
            throw new EntityNotExistException();
        }
    }

    private void existsStudentAndGroup(Student student, Group group) {
        if(student == null || group == null) {
            throw new IllegalArgumentException();
        }
        if (!existsUser(student) || groupDao.findById(group.getId()).equals(Optional.empty())) {
            throw new EntityNotExistException();
        }
    }
}
