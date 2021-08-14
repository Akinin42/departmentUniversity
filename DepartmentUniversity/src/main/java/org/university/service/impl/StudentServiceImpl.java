package org.university.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.university.dao.CourseDao;
import org.university.dao.GroupDao;
import org.university.dao.StudentDao;
import org.university.dto.StudentDto;
import org.university.dto.UserDto;
import org.university.entity.Course;
import org.university.entity.Student;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EntityNotExistException;
import org.university.service.StudentService;
import org.university.service.validator.UserValidator;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Transactional
public class StudentServiceImpl extends AbstractUserServiceImpl<Student> implements StudentService {

    StudentDao studentDao;
    CourseDao courseDao;
    PasswordEncoder encoder;

    public StudentServiceImpl(StudentDao studentDao, GroupDao groupDao, CourseDao courseDao,
            UserValidator<Student> validator, PasswordEncoder encoder) {
        super(studentDao, validator);
        this.studentDao = studentDao;
        this.courseDao = courseDao;
        this.encoder = encoder;
    }

    @Override
    public Student login(String email, String password) {
        if (!studentDao.findByEmail(email).isPresent()) {
            throw new EntityNotExistException();
        }
        Student student = studentDao.findByEmail(email).get();
        if (!encoder.matches(password, student.getPassword())) {
            throw new AuthorisationFailException();
        }
        log.info("Authorisation for student with id {} succesfull!", student.getId());
        return student;
    }

    @Override
    protected Student mapUserWithPassword(Student user) {
        return Student.builder()
                .withId(user.getId())
                .withSex(user.getSex())
                .withName(user.getName())
                .withEmail(user.getEmail())
                .withPhone(user.getPhone())
                .withPassword(encoder.encode(user.getPassword()))
                .withPhoto(user.getPhoto())
                .build();
    }

    @Override
    public void addStudentToCourse(@NonNull StudentDto studentDto, @NonNull Course course) {
        Student student = mapDtoToEntity(studentDto);
        existsStudentAndCourse(student, course);
        student = studentDao.findById(studentDto.getId()).get();
        if (!student.getCourses().contains(course)) {
            student.addCourse(course);
            studentDao.update(student);
            log.info("Student with id {} added to course {}!", student.getId(), course.getName());
        }
    }

    @Override
    public void deleteStudentFromCourse(@NonNull StudentDto studentDto, @NonNull Course course) {
        Student student = mapDtoToEntity(studentDto);
        existsStudentAndCourse(student, course);
        student = studentDao.findById(studentDto.getId()).get();
        if (student.getCourses().contains(course)) {
            student.removeCourse(course);
            studentDao.update(student);
            log.info("Student with id {} deleted from course {}!", student.getId(), course.getName());
        }
    }

    @Override
    public List<Student> findNumberOfUsers(int quantity, int number) {
        return studentDao.findAll(quantity, number);
    }

    @Override
    public List<Student> findAll() {
        return studentDao.findAll();
    }

    private void existsStudentAndCourse(Student student, Course course) {
        if (!existsUser(student) || courseDao.findById(course.getId()).equals(Optional.empty())) {
            throw new EntityNotExistException();
        }
    }

    protected Student mapDtoToEntity(UserDto user) {
        return Student.builder()
                .withId(user.getId())
                .withSex(user.getSex())
                .withName(user.getName())
                .withEmail(user.getEmail())
                .withPhone(user.getPhone())
                .withPassword(user.getPassword())
                .withPhoto(user.getPhotoName())
                .build();
    }
}
