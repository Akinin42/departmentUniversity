package org.university.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.university.dao.CourseDao;
import org.university.dao.RoleDao;
import org.university.dao.StudentDao;
import org.university.dto.StudentDto;
import org.university.dto.UserDto;
import org.university.entity.Course;
import org.university.entity.Student;
import org.university.entity.User;
import org.university.exceptions.EntityNotExistException;
import org.university.service.StudentService;
import org.university.service.validator.Validator;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Transactional
public class StudentServiceImpl extends AbstractUserServiceImpl<Student> implements StudentService {

    private static final String STUDENT = "STUDENT";
    
    StudentDao studentDao;
    CourseDao courseDao;
    RoleDao roleDao;
    PasswordEncoder encoder;    

    public StudentServiceImpl(StudentDao studentDao, CourseDao courseDao,
            Validator<User> validator, PasswordEncoder encoder, RoleDao roleDao) {
        super(studentDao, validator);        
        this.studentDao = studentDao;
        this.courseDao = courseDao;
        this.encoder = encoder;
        this.roleDao = roleDao;
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
                .withRole(roleDao.findByName(STUDENT).get())
                .withEnabled(true)
                .build();
    }

    @Override
    public void addStudentToCourse(@NonNull StudentDto studentDto, @NonNull Course course) {
        Student student = mapDtoToEntity(studentDto);
        existsStudentAndCourse(student, course);
        student = studentDao.findById(studentDto.getId()).get();
        if (!student.getCourses().contains(course)) {
            student.addCourse(course);
            studentDao.save(student);
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
            studentDao.save(student);
            log.info("Student with id {} deleted from course {}!", student.getId(), course.getName());
        }
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
                .withRole(roleDao.findByName(STUDENT).get())
                .withEnabled(true)
                .build();
    }

    @Override
    public Student getByEmail(String email) {        
        return studentDao.findByEmail(email).orElseThrow(() -> new EntityNotExistException("Student with " + email+ " not found"));
    }
}
