package org.university.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.university.dao.CourseDao;
import org.university.dao.GroupDao;
import org.university.dao.StudentDao;
import org.university.dto.StudentDto;
import org.university.dto.UserDto;
import org.university.entity.Course;
import org.university.entity.Group;
import org.university.entity.Student;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EntityNotExistException;
import org.university.service.StudentService;
import org.university.service.validator.UserValidator;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class StudentServiceImpl extends AbstractUserServiceImpl<Student> implements StudentService {

    StudentDao studentDao;
    GroupDao groupDao;
    CourseDao courseDao;
    PasswordEncoder encoder;

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
        if (!studentDao.findByEmail(email).isPresent()) {
            throw new EntityNotExistException();
        }
        Student student = studentDao.findByEmail(email).get();
        List<Course> courses = courseDao.findAllByStudent(student.getId());
        student = addCoursesToStudent(student, courses);
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
                .build();
    }

    @Override
    public void addStudentToGroup(@NonNull StudentDto studentDto, @NonNull Group group) {
        Student student = mapDtoToEntity(studentDto);
        existsStudentAndGroup(student, group);
        student = studentDao.findById(studentDto.getId()).get();
        if (!studentDao.findAllByGroup(group.getName()).contains(student)) {
            studentDao.insertStudentToGroup(student, group);
            log.info("Student with id {} added to group {}!", student.getId(), group.getName());
        }
    }

    @Override
    public void deleteStudentFromGroup(@NonNull StudentDto studentDto, @NonNull Group group) {
        Student student = mapDtoToEntity(studentDto);
        existsStudentAndGroup(student, group);
        studentDao.deleteStudentFromGroup(student.getId(), group.getId());
        log.info("Student with id {} deleted from group {}!", student.getId(), group.getName());
    }

    @Override
    public void addStudentToCourse(@NonNull StudentDto studentDto, @NonNull Course course) {
        Student student = mapDtoToEntity(studentDto);
        existsStudentAndCourse(student, course);
        student = studentDao.findById(studentDto.getId()).get();
        if (!studentDao.findAllByCourse(course.getName()).contains(student)) {
            List<Course> courses = new ArrayList<>();
            courses.add(course);
            studentDao.insertStudentToCourses(student, courses);
            log.info("Student with id {} added to course {}!", student.getId(), course.getName());
        }
    }

    @Override
    public void deleteStudentFromCourse(@NonNull StudentDto studentDto, @NonNull Course course) {
        Student student = mapDtoToEntity(studentDto);
        existsStudentAndCourse(student, course);
        studentDao.deleteStudentFromCourse(student.getId(), course.getId());
        log.info("Student with id {} deleted from course {}!", student.getId(), course.getName());
    }

    @Override
    public List<Student> findNumberOfUsers(int quantity, int number) {
        List<Student> students = studentDao.findAll(quantity, number);
        for (int i = 0; i < students.size(); i++) {
            int studentId = students.get(i).getId();
            List<Course> courses = courseDao.findAllByStudent(studentId);
            students.set(i, addCoursesToStudent(students.get(i), courses));
        }
        return students;        
    }
    
    @Override
    public List<Student> findAll() {        
        return studentDao.findAll();
    }

    private Student addCoursesToStudent(Student student, List<Course> courses) {
        return Student.builder()
                .withId(student.getId())
                .withSex(student.getSex())
                .withName(student.getName())
                .withEmail(student.getEmail())
                .withPhone(student.getPhone())
                .withPassword(student.getPassword())
                .withCourses(new HashSet<Course>(courses))               
                .build();
    }

    private void existsStudentAndCourse(Student student, Course course) {
        if (!existsUser(student) || courseDao.findById(course.getId()).equals(Optional.empty())) {
            throw new EntityNotExistException();
        }
    }

    private void existsStudentAndGroup(Student student, Group group) {
        if (!existsUser(student) || groupDao.findById(group.getId()).equals(Optional.empty())) {
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
                .build();
    }
}
