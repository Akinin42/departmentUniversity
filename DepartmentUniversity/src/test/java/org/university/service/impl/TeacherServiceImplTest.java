package org.university.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.university.dao.RoleDao;
import org.university.dao.StudentDao;
import org.university.dao.TeacherDao;
import org.university.dto.TeacherDto;
import org.university.entity.Role;
import org.university.entity.Student;
import org.university.entity.Teacher;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EmailExistException;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.service.validator.UserValidator;
import org.university.utils.CreatorTestEntities;
import org.university.utils.Sex;

class TeacherServiceImplTest {

    private static TeacherServiceImpl teacherService;
    private static TeacherDao teacherDaoMock;
    private static StudentDao studentDaoMock;
    private static RoleDao roleDaoMock;

    @BeforeAll
    static void init() {
        studentDaoMock = createStudentDaoMock();
        teacherDaoMock = createTeacherDaoMock();
        roleDaoMock = createRoleDaoMock();
        teacherService = new TeacherServiceImpl(teacherDaoMock, 
                new UserValidator(studentDaoMock, teacherDaoMock,createEncoderMock()), createEncoderMock(), roleDaoMock);
    }

    @Test
    void registerShouldSaveTeacherToDatabaseWhenInputTeacherNotExistThere() {
        TeacherDto teacher = new TeacherDto();        
        teacher.setSex(Sex.MALE);
        teacher.setName("Test");
        teacher.setEmail("test@test.ru");
        teacher.setPhone("78956547475");
        teacher.setPassword("Test");
        teacher.setScientificDegree("Test");
        teacher.setPhotoName("test-photo");
        teacherService.register(teacher);
        Teacher teacherWithEncodePassword = Teacher.builder()
                .withSex(Sex.MALE)
                .withName("Test")
                .withEmail("test@test.ru")
                .withPhone("78956547475")
                .withPassword("encodePassword")
                .withScientificDegree("Test")
                .withPhoto("test-photo")
                .withRole(roleDaoMock.findByName("TEACHER").get())
                .withEnabled(true)
                .build();
        verify(teacherDaoMock).save(teacherWithEncodePassword);
    }

    @Test
    void registerShouldThrowEntityAlreadyExistExceptionWhenInputTeacherExistsInDatabase() {
        TeacherDto teacher = new TeacherDto();
        teacher.setId(1);
        teacher.setSex(Sex.MALE);
        teacher.setName("Bob Moren");
        teacher.setEmail("Bob@mail.ru");
        teacher.setPhone("79758657788");
        teacher.setPassword("test-password");
        teacher.setScientificDegree("professor");
        when(teacherDaoMock.existsById(1)).thenReturn(true);
        assertThatThrownBy(() -> teacherService.register(teacher)).isInstanceOf(EntityAlreadyExistException.class);
    }
    
    @Test
    void registerShouldThrowEmailExistExceptionWhenInputEmailRegistered() {
        TeacherDto teacher = new TeacherDto();        
        teacher.setSex(Sex.MALE);
        teacher.setName("New user");
        teacher.setEmail("existe@mail.ru");
        teacher.setPhone("79758657788");
        teacher.setPassword("test-password");
        teacher.setScientificDegree("professor");
        assertThatThrownBy(() -> teacherService.register(teacher)).isInstanceOf(EmailExistException.class);
    }
    
    @Test
    void registerShouldThrowEmailExistExceptionWhenInputEmailRegisteredHowStudent() {
        TeacherDto teacher = new TeacherDto();        
        teacher.setSex(Sex.MALE);
        teacher.setName("New user");
        teacher.setEmail("existestudent@test.ru");
        teacher.setPhone("79758657788");
        teacher.setPassword("test-password");
        teacher.setScientificDegree("professor");
        assertThatThrownBy(() -> teacherService.register(teacher)).isInstanceOf(EmailExistException.class);
    }

    @Test
    void registerShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> teacherService.register(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteShouldDeleteTeacherFromDatabase() {
        TeacherDto teacher = new TeacherDto();
        teacher.setId(1);
        teacherService.delete(teacher);
        verify(teacherDaoMock).deleteById(1);
    }

    @Test
    void deleteShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> teacherService.delete(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findNumberOfUsersShouldReturnExpectedTeachersWhenInputLimitAndOffset() {
        List<Teacher> teachers = new ArrayList<>();
        teachers.add(CreatorTestEntities.createTeachers().get(0));
        Pageable limit = PageRequest.of(0,1);
        when(teacherDaoMock.findAll(limit)).thenReturn(new PageImpl<>(teachers));
        assertThat(teacherService.findNumberOfUsers(1, 0)).containsExactly(CreatorTestEntities.createTeachers().get(0));
    }

    @Test
    void findNumberOfUsersShouldReturnEmptyListWhenInputOffsetMoreTableSize() {
        Pageable limit = PageRequest.of(1,5);
        when(teacherDaoMock.findAll(limit)).thenReturn(Page.empty());
        assertThat(teacherService.findNumberOfUsers(5, 1)).isEmpty();
    }

    @Test
    void loginShouldReturnExpectedTeacherWhenTeacherExistsInDatabase() {
        Teacher teacherWithEncodePassword = Teacher.builder()
                .withSex(Sex.MALE)
                .withName("Test")
                .withEmail("test2@test.ru")
                .withPhone("Test")
                .withPassword("encodePassword")
                .withScientificDegree("Test")
                .build();
        assertThat(teacherService.login("test2@test.ru", "Test")).isEqualTo(teacherWithEncodePassword);
    }

    @Test
    void loginShouldThrowAuthorisationFailExceptionWhenInputNotValide() {
        assertThatThrownBy(() -> teacherService.login("test2@test.ru", "invalidpassword"))
                .isInstanceOf(AuthorisationFailException.class);
    }

    @Test
    void loginShouldThrowEntityNotExistExceptionWhenInputEmailNotExistsInDatabase() {
        when(teacherDaoMock.findByEmail("notExistenEmail")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> teacherService.login("notExistenEmail", "test@test.ru"))
                .isInstanceOf(EntityNotExistException.class);
    }
    
    @Test
    void findAllShouldReturnExpectedTeachersWhenTheyExist() {
        List<Teacher> teachers = CreatorTestEntities.createTeachers();
        when(teacherDaoMock.findAll()).thenReturn(teachers);
        assertThat(teacherService.findAll()).isEqualTo(teachers);
    }
    
    @Test
    void findAllShouldReturnEmptyListWhenTheyNotExist() {        
        when(teacherDaoMock.findAll()).thenReturn(new ArrayList<Teacher>());
        assertThat(teacherService.findAll()).isEmpty();
    }

    @Test
    void editShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> teacherService.edit(null)).isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void editShouldUpdateTeacherInDatabaseWhenInputValidTeacher() {
        TeacherDao teacherDaoMock = createTeacherDaoMock();
        TeacherServiceImpl teacherService = new TeacherServiceImpl(teacherDaoMock, 
                new UserValidator(studentDaoMock, teacherDaoMock,createEncoderMock()), createEncoderMock(), roleDaoMock);
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(1);
        teacherDto.setSex(Sex.MALE);
        teacherDto.setName("Bob Moren");
        teacherDto.setEmail("test@test.ru");
        teacherDto.setPhone("79758657788");
        teacherDto.setPassword("Test");
        teacherDto.setScientificDegree("professor");
        teacherDto.setPhotoName("test-photo");
        Teacher teacher = Teacher.builder()
                .withId(1)
                .withSex(Sex.MALE)
                .withName("Bob Moren")
                .withEmail("test@test.ru")
                .withPhone("79758657788")
                .withPassword("encodePassword")
                .withScientificDegree("professor")
                .withPhoto("test-photo")
                .withRole(roleDaoMock.findByName("TEACHER").get())
                .withEnabled(true)
                .build();
        when(teacherDaoMock.findById(1)).thenReturn(Optional.ofNullable(teacher));
        teacherService.edit(teacherDto);
        verify(teacherDaoMock).save(teacher);
    }
    
    @Test
    void editShouldThrowEmailExistExceptionWhenEmailChangeAndExistsYet() {
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(5);
        teacherDto.setSex(Sex.MALE);
        teacherDto.setName("Bob Moren");
        teacherDto.setEmail("test2@test.ru");
        teacherDto.setPhone("79758657788");
        teacherDto.setPassword("Test");
        teacherDto.setScientificDegree("professor");
        teacherDto.setPhotoName("test-photo");
        assertThatThrownBy(() -> teacherService.edit(teacherDto))
                .isInstanceOf(EmailExistException.class);
    }
    
    @Test
    void editShouldThrowEmailExistExceptionWhenEmailChangeAndExistsHowStudentYet() {
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(5);
        teacherDto.setSex(Sex.MALE);
        teacherDto.setName("Bob Moren");
        teacherDto.setEmail("existestudent@test.ru");
        teacherDto.setPhone("79758657788");
        teacherDto.setPassword("Test");
        teacherDto.setScientificDegree("professor");
        teacherDto.setPhotoName("test-photo");
        assertThatThrownBy(() -> teacherService.edit(teacherDto))
        .isInstanceOf(EmailExistException.class);
    }
    
    @Test
    void editShouldThrowAuthorisationFailExceptionWhenPasswordNotCorrect() {
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(5);
        teacherDto.setSex(Sex.MALE);
        teacherDto.setName("Bob Moren");
        teacherDto.setEmail("test2@test.ru");
        teacherDto.setPhone("79758657788");
        teacherDto.setPassword("incorrect password");
        teacherDto.setScientificDegree("professor");
        teacherDto.setPhotoName("test-photo");
        assertThatThrownBy(() -> teacherService.edit(teacherDto))
        .isInstanceOf(AuthorisationFailException.class);
    }
    
    @Test
    void editShouldUpdateTeacherInDatabaseWhenInputValidTeacherAndEmailNotChange() {
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(5);
        teacherDto.setSex(Sex.MALE);
        teacherDto.setName("Bob Moren");
        teacherDto.setEmail("existe@mail.ru");
        teacherDto.setPhone("79758657788");
        teacherDto.setPassword("Test");
        teacherDto.setScientificDegree("professor");
        teacherDto.setPhotoName("test-photo");
        Teacher teacher = Teacher.builder()
                .withId(5)
                .withSex(Sex.MALE)
                .withName("Bob Moren")
                .withEmail("existe@mail.ru")
                .withPhone("79758657788")
                .withPassword("encodePassword")
                .withScientificDegree("professor")
                .withPhoto("test-photo")
                .withRole(roleDaoMock.findByName("TEACHER").get())
                .withEnabled(true)
                .build();
        teacherService.edit(teacherDto);
        verify(teacherDaoMock).save(teacher);
    }

    private static TeacherDao createTeacherDaoMock() {
        TeacherDao teacherDaoMock = mock(TeacherDao.class);
        when(teacherDaoMock.findById(1)).thenReturn(Optional.ofNullable(CreatorTestEntities.createTeachers().get(0)));
        when(teacherDaoMock.findById(3)).thenReturn(Optional.empty());
        Teacher teacherWithEncodePassword = Teacher.builder()
                .withSex(Sex.MALE)
                .withName("Test")
                .withEmail("test2@test.ru")
                .withPhone("Test")
                .withPassword("encodePassword")
                .withScientificDegree("Test")
                .build();
        when(teacherDaoMock.findByEmail("test@test.ru")).thenReturn(Optional.empty());
        when(teacherDaoMock.findByEmail("test2@test.ru")).thenReturn(Optional.ofNullable(teacherWithEncodePassword));
        Teacher teacher = Teacher.builder()
                .withId(5)
                .withSex(Sex.MALE)
                .withName("Bob Moren")
                .withEmail("existe@mail.ru")
                .withPhone("79758657788")
                .withPassword("encodePassword")
                .withScientificDegree("professor")
                .build();
        when(teacherDaoMock.findByEmail("existe@mail.ru")).thenReturn(Optional.ofNullable(teacher));
        when(teacherDaoMock.findById(5)).thenReturn(Optional.ofNullable(teacher));
        return teacherDaoMock;
    }

    private static PasswordEncoder createEncoderMock() {
        PasswordEncoder encoderMock = mock(PasswordEncoder.class);
        when(encoderMock.encode("Test")).thenReturn("encodePassword");
        when(encoderMock.matches("Test", "encodePassword")).thenReturn(true);
        return encoderMock;
    }
    
    private static RoleDao createRoleDaoMock() {
        RoleDao roleDaoMock = mock(RoleDao.class);
        Role role = Role.builder()
                .withId(2)
                .withName("TEACHER")
                .build();
        when(roleDaoMock.findByName("TEACHER")).thenReturn(Optional.ofNullable(role));        
        return roleDaoMock;
    }
    
    private static StudentDao createStudentDaoMock() {
        StudentDao studentDaoMock = mock(StudentDao.class);
        when(studentDaoMock.findByEmail("test@test.ru")).thenReturn(Optional.empty());
        Student existStudent = Student.builder()
                .withId(15)
                .withEmail("existestudent@test.ru")                
                .build();
        when(studentDaoMock.findByEmail("existestudent@test.ru")).thenReturn(Optional.ofNullable(existStudent));
        return studentDaoMock;
    }
}
