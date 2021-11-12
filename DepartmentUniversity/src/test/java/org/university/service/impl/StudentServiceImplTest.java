package org.university.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.university.dao.CourseDao;
import org.university.dao.RoleDao;
import org.university.dao.StudentDao;
import org.university.dao.TeacherDao;
import org.university.dao.TemporaryUserDao;
import org.university.dto.StudentDto;
import org.university.entity.Course;
import org.university.entity.Role;
import org.university.entity.Student;
import org.university.entity.Teacher;
import org.university.entity.User;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EmailExistException;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.service.EmailService;
import org.university.service.SecureTokenService;
import org.university.service.StudentService;
import org.university.service.validator.UserValidator;
import org.university.utils.CreatorTestEntities;
import org.university.utils.Sex;

class StudentServiceImplTest {

    private static StudentServiceImpl studentService;
    private static StudentDao studentDaoMock;
    private static TeacherDao teacherDaoMock;
    private static TemporaryUserDao temporaryDaoMock;
    private static RoleDao roleDaoMock;   
    private static  SecureTokenService secureTokenServiceMock;

    @BeforeAll
    static void init() {
        temporaryDaoMock = createTemporaryUserDaoMock();
        studentDaoMock = createStudentDaoMock();
        teacherDaoMock = createTeacherDaoMock();
        roleDaoMock = createRoleDaoMock();
        secureTokenServiceMock = createTokenServiceMock();        
        studentService = new StudentServiceImpl(studentDaoMock, createCourseDaoMock(), createEmailServiceMock(), secureTokenServiceMock,
                new UserValidator(studentDaoMock, teacherDaoMock, temporaryDaoMock, createEncoderMock()), createEncoderMock(), roleDaoMock);
    }

    @Test
    void registerShouldSaveStudentToDatabaseWhenInputStudentNotExistThere() {
        StudentDto student = new StudentDto(); 
        student.setId(45);
        student.setSex(Sex.MALE);
        student.setName("Test");
        student.setEmail("test@test.ru");
        student.setPhone("79236170788");
        student.setPassword("encodePassword");
        student.setPhotoName("testphoto");
        studentService.register(student);
        Student studentWithEncodePassword = Student.builder()
                .withId(45)
                .withSex(Sex.MALE)
                .withName("Test")
                .withEmail("test@test.ru")
                .withPhone("79236170788")
                .withPassword("encodePassword")
                .withPhoto("testphoto")
                .withRole(roleDaoMock.findByName("STUDENT").get())
                .withEnabled(true)
                .build();
        verify(studentDaoMock).save(studentWithEncodePassword);
    }

    @Test
    void registerShouldThrowEntityAlreadyExistExceptionWhenInputStudentExistsInDatabase() {
        StudentDto student = new StudentDto();
        student.setId(1);
        student.setSex(Sex.FEMALE);
        student.setName("Jane Wood");
        student.setEmail("Wood@email.ru");
        student.setPhone("78954756666");
        student.setPassword("test-password");
        student.setConfirmPassword("test-password");
        assertThatThrownBy(() -> studentService.register(student)).isInstanceOf(EntityAlreadyExistException.class);
    }
    
    @Test
    void registerShouldSaveStudentToDatabaseWhenInputStudentNotExistThereButHasId() {
        StudentDto student = new StudentDto();
        student.setId(2);
        student.setSex(Sex.MALE);
        student.setName("Test");
        student.setEmail("test@test.ru");
        student.setPhone("79236170788");
        student.setPassword("encodePassword");
        student.setPhotoName("testphoto");
        when(studentDaoMock.existsById(2)).thenReturn(false);        
        Student studentWithEncodePassword = Student.builder()
                .withId(2)
                .withSex(Sex.MALE)
                .withName("Test")
                .withEmail("test@test.ru")
                .withPhone("79236170788")
                .withPassword("encodePassword")
                .withPhoto("testphoto")
                .withRole(roleDaoMock.findByName("STUDENT").get())
                .withEnabled(true)
                .build();
        when(studentDaoMock.findById(2)).thenReturn(Optional.ofNullable(studentWithEncodePassword));
        when(studentDaoMock.findByEmail("test@test.ru")).thenReturn(Optional.ofNullable(studentWithEncodePassword));
        studentService.register(student);
        verify(studentDaoMock).save(studentWithEncodePassword);        
    }
    
    @Test
    void registerShouldThrowEmailExistExceptionWhenInputEmailRegistered() {
        StudentDto student = new StudentDto();        
        student.setSex(Sex.FEMALE);
        student.setName("New user");
        student.setEmail("existmail@test.ru");
        student.setPhone("78954756666");
        student.setPassword("test-password");
        assertThatThrownBy(() -> studentService.register(student)).isInstanceOf(EmailExistException.class);
    }
    
    @Test
    void registerShouldThrowEmailExistExceptionWhenInputEmailRegistredHowTeacher() {
        StudentDto student = new StudentDto();        
        student.setSex(Sex.FEMALE);
        student.setName("New user");
        student.setEmail("existteachermail@test.ru");
        student.setPhone("78954756666");
        student.setPassword("test-password");
        assertThatThrownBy(() -> studentService.register(student)).isInstanceOf(EmailExistException.class);
    }

    @Test
    void registerShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> studentService.register(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteShouldDeleteStudentFromDatabase() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(1);
        studentService.delete(studentDto);
        verify(studentDaoMock).deleteById(1);
    }

    @Test
    void deleteShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> studentService.delete(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findNumberOfUsersShouldReturnExpectedStudentsWhenInputLimitAndOffset() {
        List<Student> students = new ArrayList<>();        
        students.add(CreatorTestEntities.createStudents().get(0));
        Pageable limit = PageRequest.of(0,1);
        when(studentDaoMock.findAll(limit)).thenReturn (new PageImpl<>(students));
        Student student = Student.builder()
                .withId(1)
                .withSex(Sex.FEMALE)
                .withName("Jane Wood")
                .withEmail("Wood@email.ru")
                .withPhone("test-phone")
                .withPassword("test-password")
                .withCourses(new HashSet<Course>(CreatorTestEntities.createCourses()))
                .withPhoto("default-female-photo")
                .withRole(roleDaoMock.findByName("STUDENT").get())
                .withEnabled(true)
                .build();
        assertThat(studentService.findNumberOfUsers(1, 0)).containsExactly(student);
    }

    @Test
    void findNumberOfUsersShouldReturnEmptyListWhenInputOffsetMoreTableSize() {
        Pageable limit = PageRequest.of(1,5);
        when(studentDaoMock.findAll(limit)).thenReturn(Page.empty());
        assertThat(studentService.findNumberOfUsers(5, 1)).isEmpty();
    }
    
    @Test
    void findAllShouldReturnExpectedStudentsWhenTheyExist() {
        List<Student> students = CreatorTestEntities.createStudents();
        when(studentDaoMock.findAll()).thenReturn(students);
        assertThat(studentService.findAll()).isEqualTo(students);
    }
    
    @Test
    void findAllShouldReturnEmptyListWhenTheyNotExist() {        
        when(studentDaoMock.findAll()).thenReturn(new ArrayList<Student>());
        assertThat(studentService.findAll()).isEmpty();
    }

    @Test
    void addStudentToCourseShouldAddStudentToCourseWhenInputCourseAndStudentExists() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(6);
        when(studentDaoMock.existsById(6)).thenReturn(true);
        Course course = CreatorTestEntities.createCourses().get(0);
        studentService.addStudentToCourse(studentDto, course);
        Student student = CreatorTestEntities.createStudents().get(5);
        student.addCourse(course);
        verify(studentDaoMock).save(student);
    }

    @Test
    void addStudentToCourseShouldNotAddStudentToCourseWhenStudentHasThisCourseYet() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(1);
        when(studentDaoMock.existsById(1)).thenReturn(true);
        Course course = CreatorTestEntities.createCourses().get(0);
        List<Course> courses = new ArrayList<>();
        courses.add(course);
        studentService.addStudentToCourse(studentDto, course);
        Student student = CreatorTestEntities.createStudents().get(0);
        verify(studentDaoMock, never()).save(student);
    }

    @Test
    void addStudentToCourseShouldThrowEntityNotExistExceptionWhenInputStudentNotExists() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(10);
        Course course = CreatorTestEntities.createCourses().get(0);
        assertThatThrownBy(() -> studentService.addStudentToCourse(studentDto, course))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void addStudentToCourseShouldThrowEntityNotExistExceptionWhenInputCourseNotExists() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(1);
        Course course = Course.builder()
                .withId(5)
                .withName("notExist")
                .withDescription("test")
                .build();
        assertThatThrownBy(() -> studentService.addStudentToCourse(studentDto, course))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void addStudentToCourseShouldThrowIllegalArgumentExceptionWhenInputStudentNull() {
        Course course = CreatorTestEntities.createCourses().get(0);
        assertThatThrownBy(() -> studentService.addStudentToCourse(null, course))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addStudentToCourseShouldThrowIllegalArgumentExceptionWhenInputCourseNull() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(1);
        assertThatThrownBy(() -> studentService.addStudentToCourse(studentDto, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteStudentFromCourseShouldDeleteStudentFromCourseWhenInputGroupAndStudentExists() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(1);
        Course course = CreatorTestEntities.createCourses().get(0);
        Student student = CreatorTestEntities.createStudents().get(0);
        student.removeCourse(course);
        studentService.deleteStudentFromCourse(studentDto, course);
        verify(studentDaoMock).save(student);
    }
    
    @Test
    void deleteStudentFromCourseShouldNotDeleteStudentWhenStudentWithoutCourse() {
        StudentDao studentDaoMock = createStudentDaoMock();        
        StudentService studentService = new StudentServiceImpl(studentDaoMock, createCourseDaoMock(), createEmailServiceMock(), secureTokenServiceMock,
                new UserValidator(studentDaoMock, null, temporaryDaoMock, createEncoderMock()), createEncoderMock(), roleDaoMock);
        Student studentWithotCourse = CreatorTestEntities.createStudents().get(0);
        Course course = CreatorTestEntities.createCourses().get(0);
        studentWithotCourse.removeCourse(course);
        when(studentDaoMock.findById(1)).thenReturn(Optional.ofNullable(studentWithotCourse));
        when(studentDaoMock.existsById(1)).thenReturn(true);
        StudentDto studentDto = new StudentDto();
        studentDto.setId(1);       
        studentService.deleteStudentFromCourse(studentDto, course);
        verify(studentDaoMock, never()).save(studentWithotCourse);
    }

    @Test
    void deleteStudentFromCourseShouldThrowIllegalArgumentExceptionWhenInputStudentNull() {
        Course course = CreatorTestEntities.createCourses().get(0);
        assertThatThrownBy(() -> studentService.deleteStudentFromCourse(null, course))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteStudentFromCourseShouldThrowIllegalArgumentExceptionWhenInputCourseNull() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(1);
        assertThatThrownBy(() -> studentService.deleteStudentFromCourse(studentDto, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void editShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> studentService.edit(null)).isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void editShouldUpdateStudentInDatabaseWhenInputValidStudent() {
        StudentDao studentDaoMock = createStudentDaoMock();
        StudentServiceImpl studentService = new StudentServiceImpl(studentDaoMock, createCourseDaoMock(), createEmailServiceMock(), secureTokenServiceMock,
                new UserValidator(studentDaoMock, teacherDaoMock, temporaryDaoMock, createEncoderMock()), createEncoderMock(), roleDaoMock);
        StudentDto studentDto = new StudentDto();
        studentDto.setId(1);
        studentDto.setSex(Sex.FEMALE);
        studentDto.setName("Jane Wood");
        studentDto.setEmail("test@test.ru");
        studentDto.setPhone("78954756666");
        studentDto.setPassword("Test");
        Student student = Student.builder()
                .withId(1)
                .withSex(Sex.FEMALE)
                .withName("Jane Wood")
                .withEmail("test@test.ru")
                .withPhone("78954756666")
                .withPassword("encodePassword")
                .withRole(roleDaoMock.findByName("STUDENT").get())
                .withEnabled(true)
                .build();
        when(studentDaoMock.findById(1)).thenReturn(Optional.ofNullable(student));
        studentService.edit(studentDto);
        verify(studentDaoMock).save(student);
    }
    
    @Test
    void editShouldShouldThrowEmailExistExceptionWhenEmailChangeAndExistsYet() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(8);
        studentDto.setSex(Sex.MALE);
        studentDto.setName("Test");
        studentDto.setEmail("test2@test.ru");
        studentDto.setPhone("79236170788");
        studentDto.setPassword("Test");
        studentDto.setPhotoName("test-photo");
        assertThatThrownBy(() -> studentService.edit(studentDto))
            .isInstanceOf(EmailExistException.class);
    }
    
    @Test
    void editShouldShouldThrowEmailExistExceptionWhenEmailChangeAndExistsHowTeacherYet() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(8);
        studentDto.setSex(Sex.MALE);
        studentDto.setName("Test");
        studentDto.setEmail("existteachermail@test.ru");
        studentDto.setPhone("79236170788");
        studentDto.setPassword("Test");
        studentDto.setPhotoName("test-photo");
        assertThatThrownBy(() -> studentService.edit(studentDto))
        .isInstanceOf(EmailExistException.class);
    }
    
    @Test
    void editShouldShouldThrowAuthorisationFailExceptionWhenPasswordNotCorrect() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(8);
        studentDto.setSex(Sex.MALE);
        studentDto.setName("Test");
        studentDto.setEmail("test2@test.ru");
        studentDto.setPhone("79236170788");
        studentDto.setPassword("Incorrect password");
        studentDto.setPhotoName("test-photo");
        assertThatThrownBy(() -> studentService.edit(studentDto))
            .isInstanceOf(AuthorisationFailException.class);
    }
    
    @Test
    void editShouldUpdateStudentInDatabaseWhenInputValidStudentAndEmailNotChange() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(8);
        studentDto.setSex(Sex.MALE);
        studentDto.setName("Test");
        studentDto.setEmail("existmail@test.ru");
        studentDto.setPhone("79236170788");
        studentDto.setPassword("Test");
        studentDto.setPhotoName("test-photo");
        Student student = Student.builder()
                .withId(8)
                .withSex(Sex.MALE)
                .withName("Test")
                .withEmail("existmail@test.ru")
                .withPhone("79236170788")
                .withPassword("encodePassword")
                .withPhoto("test-photo")
                .withRole(roleDaoMock.findByName("STUDENT").get())
                .withEnabled(true)
                .build();
        studentService.edit(studentDto);
        verify(studentDaoMock).save(student);
    }
    
    @Test
    void getByEmailShouldReturnStudentWithInputEmail() {
        Student expectedStudent = Student.builder()
                .withId(8)
                .withSex(Sex.MALE)
                .withName("Test")
                .withEmail("existmail@test.ru")
                .withPhone("79236170788")
                .withPassword("encodePassword")                
                .build();
        assertThat(studentService.getByEmail("existmail@test.ru")).isEqualTo(expectedStudent);
    }
    
    @Test
    void getByEmailShouldThrowEntityNotExistExceptionWhenStudentNotExists() {        
        assertThatThrownBy(() -> studentService.getByEmail("usernotexiststodb@mail.ru"))
            .isInstanceOf(EntityNotExistException.class).hasMessage("Student with usernotexiststodb@mail.ru not found");
    }

    private static CourseDao createCourseDaoMock() {
        CourseDao courseDaoMock = mock(CourseDao.class);
        when(courseDaoMock.findById(1)).thenReturn(Optional.ofNullable(CreatorTestEntities.createCourses().get(0)));
        return courseDaoMock;
    }

    private static PasswordEncoder createEncoderMock() {
        PasswordEncoder encoderMock = mock(PasswordEncoder.class);
        when(encoderMock.encode("Test")).thenReturn("encodePassword");
        when(encoderMock.matches("Test", "encodePassword")).thenReturn(true);        
        when(encoderMock.matches("test-password", "test-password")).thenReturn(true);        
        return encoderMock;
    }

    private static StudentDao createStudentDaoMock() {
        StudentDao studentDaoMock = mock(StudentDao.class);
        Student student = Student.builder()
                .withId(7)
                .withSex(Sex.MALE)
                .withName("Test")
                .withEmail("test2@test.ru")
                .withPhone("Test")
                .withPassword("encodePassword")
                .withCourses(new HashSet<Course>())
                .build();
        when(studentDaoMock.findByEmail("test@test.ru")).thenReturn(Optional.empty());
        when(studentDaoMock.findByEmail("test2@test.ru")).thenReturn(Optional.ofNullable(student));
        Student newStudent = Student.builder()
                .withId(8)
                .withSex(Sex.MALE)
                .withName("Test")
                .withEmail("existmail@test.ru")
                .withPhone("79236170788")
                .withPassword("encodePassword")                
                .build();
        when(studentDaoMock.findByEmail("existmail@test.ru")).thenReturn(Optional.ofNullable(newStudent));
        when(studentDaoMock.findById(8)).thenReturn(Optional.ofNullable(newStudent));
        when(studentDaoMock.findById(1)).thenReturn(Optional.ofNullable(CreatorTestEntities.createStudents().get(0)));
        when(studentDaoMock.existsById(1)).thenReturn(true);        
        when(studentDaoMock.findById(6)).thenReturn(Optional.ofNullable(CreatorTestEntities.createStudents().get(5)));       
        List<Student> students = CreatorTestEntities.createStudents();
        students.remove(5);
        students.remove(4);
        students = new ArrayList<>();
        students.add(CreatorTestEntities.createStudents().get(4));
        students.add(CreatorTestEntities.createStudents().get(5));
        return studentDaoMock;
    }
    
    private static RoleDao createRoleDaoMock() {
        RoleDao roleDaoMock = mock(RoleDao.class);
        Role role = Role.builder()
                .withId(1)
                .withName("STUDENT")
                .build();
        when(roleDaoMock.findByName("STUDENT")).thenReturn(Optional.ofNullable(role));        
        return roleDaoMock;
    }
    
    private static TeacherDao createTeacherDaoMock() {
        TeacherDao teacherDaoMock = mock(TeacherDao.class);
        when(teacherDaoMock.findByEmail("test@test.ru")).thenReturn(Optional.empty());
        Teacher existTeacher = Teacher.builder()
                .withId(15)
                .withEmail("existteachermail@test.ru")
                .build();
        when(teacherDaoMock.findByEmail("existteachermail@test.ru")).thenReturn(Optional.ofNullable(existTeacher));
        return teacherDaoMock;
    }
    
    private static TemporaryUserDao createTemporaryUserDaoMock() {
        TemporaryUserDao temporaryDaoMock = mock(TemporaryUserDao.class);
        when(temporaryDaoMock.findByEmail("existteachermail@test.ru")).thenReturn(Optional.empty());
        return temporaryDaoMock;
    }
    
    private static SecureTokenService createTokenServiceMock() {
        SecureTokenService secureTokenServiceMock = mock(SecureTokenService.class);        
        return secureTokenServiceMock;
    }
    
    @SuppressWarnings("unchecked")
    private static EmailService<User> createEmailServiceMock() {
        EmailService<User> emailServiceMock = (EmailService<User>)mock(EmailService.class);        
        return emailServiceMock;
    }
}
