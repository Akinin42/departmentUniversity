package org.university.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.university.dao.impl.TeacherDaoImpl;
import org.university.dto.TeacherDto;
import org.university.entity.Teacher;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.exceptions.InvalidEmailException;
import org.university.service.validator.UserValidator;
import org.university.utils.CreatorTestEntities;

class TeacherServiceImplTest {

    private static TeacherServiceImpl teacherService;
    private static TeacherDaoImpl teacherDaoMock;

    @BeforeAll
    static void init() {
        teacherDaoMock = createTeacherDaoMock();
        teacherService = new TeacherServiceImpl(teacherDaoMock, new UserValidator<Teacher>(), createEncoderMock());
    }

    @Test
    void registerShouldSaveTeacherToDatabaseWhenInputTeacherNotExistThere() {
        TeacherDto teacher = getTestTeacherDto();
        teacherService.register(teacher);
        Teacher teacherWithEncodePassword = Teacher.builder()
                .withSex("Test")
                .withName("Test")
                .withEmail("test@test.ru")
                .withPhone("Test")
                .withPassword("encodePassword")
                .withScientificDegree("Test")
                .build();
        verify(teacherDaoMock).save(teacherWithEncodePassword);
    }

    @Test
    void registerShouldThrowEntityAlreadyExistExceptionWhenInputTeacherExistsInDatabase() {
        TeacherDto teacher = new TeacherDto();
        teacher.setId(1);
        teacher.setSex("Male");
        teacher.setName("Bob Moren");
        teacher.setEmail("Bob@mail.ru");
        teacher.setPhone("89758657788");
        teacher.setPassword("test-password");
        teacher.setScientificDegree("professor");
        assertThatThrownBy(() -> teacherService.register(teacher)).isInstanceOf(EntityAlreadyExistException.class);
    }

    @Test
    void registerShouldThrowInvalidEmailExceptionWhenInputTeacherHasInvalidEmail() {
        TeacherDto teacher = getTestTeacherDto();
        teacher.setEmail("invalidemail");
        assertThatThrownBy(() -> teacherService.register(teacher)).isInstanceOf(InvalidEmailException.class);
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
        when(teacherDaoMock.findAll(1, 0)).thenReturn(teachers);
        assertThat(teacherService.findNumberOfUsers(1, 0)).containsExactly(CreatorTestEntities.createTeachers().get(0));
    }

    @Test
    void findNumberOfUsersShouldReturnEmptyListWhenInputOffsetMoreTableSize() {
        List<Teacher> teachers = new ArrayList<>();
        when(teacherDaoMock.findAll(2, 10)).thenReturn(teachers);
        assertThat(teacherService.findNumberOfUsers(2, 10)).isEmpty();
    }

    @Test
    void loginShouldReturnExpectedTeacherWhenTeacherExistsInDatabase() {
        Teacher teacherWithEncodePassword = Teacher.builder()
                .withSex("Test")
                .withName("Test")
                .withEmail("test@test.ru")
                .withPhone("Test")
                .withPassword("encodePassword")
                .withScientificDegree("Test")
                .build();
        assertThat(teacherService.login("test@test.ru", "Test")).isEqualTo(teacherWithEncodePassword);
    }

    @Test
    void loginShouldThrowAuthorisationFailExceptionWhenInputNotValide() {
        assertThatThrownBy(() -> teacherService.login("test@test.ru", "invalidpassword"))
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

    private TeacherDto getTestTeacherDto() {
        TeacherDto teacher = new TeacherDto();
        teacher.setId(3);
        teacher.setSex("Test");
        teacher.setName("Test");
        teacher.setEmail("test@test.ru");
        teacher.setPhone("Test");
        teacher.setPassword("Test");
        teacher.setScientificDegree("Test");
        return teacher;
    }

    private static TeacherDaoImpl createTeacherDaoMock() {
        TeacherDaoImpl teacherDaoMock = mock(TeacherDaoImpl.class);
        when(teacherDaoMock.findById(1)).thenReturn(Optional.ofNullable(CreatorTestEntities.createTeachers().get(0)));
        when(teacherDaoMock.findById(3)).thenReturn(Optional.empty());
        Teacher teacherWithEncodePassword = Teacher.builder()
                .withSex("Test")
                .withName("Test")
                .withEmail("test@test.ru")
                .withPhone("Test")
                .withPassword("encodePassword")
                .withScientificDegree("Test")
                .build();
        when(teacherDaoMock.findByEmail("test@test.ru")).thenReturn(Optional.ofNullable(teacherWithEncodePassword));
        return teacherDaoMock;
    }

    private static PasswordEncoder createEncoderMock() {
        PasswordEncoder encoderMock = mock(PasswordEncoder.class);
        when(encoderMock.encode("Test")).thenReturn("encodePassword");
        when(encoderMock.matches("Test", "encodePassword")).thenReturn(true);
        return encoderMock;
    }
}
