package org.university.service.impl;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.university.config.ApplicationContextInjector;
import org.university.dao.ScriptExecutor;
import org.university.entity.Teacher;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.exceptions.InvalidEmailException;
import org.university.utils.CreatorTestEntities;

class TeacherServiceImplTest {

    private static TeacherServiceImpl teacherService;
    private static ScriptExecutor executor;

    @BeforeAll
    static void init() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                ApplicationContextInjector.class);
        teacherService = context.getBean(TeacherServiceImpl.class);
        executor = context.getBean(ScriptExecutor.class);
    }

    @BeforeEach
    void createTablesAndData() {
        executor.executeScript("inittestdb.sql");
    }

    @Test
    void registerShouldSaveTeacherToDatabaseWhenInputTeacherNotExistThere() {
        Teacher teacher = getTestTeacher();
        teacherService.register(teacher);
        teacher = teacherService.login("test@test.ru", "Test");
        assertThat(teacherService.findNumberOfUsers(3, 0)).contains(teacher);
    }

    @Test
    void registerShouldThrowEntityAlreadyExistExceptionWhenInputTeacherExistsInDatabase() {
        Teacher teacher = CreatorTestEntities.createTeachers().get(0);
        assertThatThrownBy(() -> teacherService.register(teacher)).isInstanceOf(EntityAlreadyExistException.class);
    }

    @Test
    void registerShouldThrowInvalidEmailExceptionWhenInputTeacherHasInvalidEmail() {
        Teacher teacher = Teacher.builder()
                .withId(3)
                .withSex("Test")
                .withName("Test")
                .withEmail("invalidemail")
                .withPhone("Test")
                .withPassword("Test")
                .withDegree("Test")
                .build();
        assertThatThrownBy(() -> teacherService.register(teacher)).isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void registerShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> teacherService.register(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteShouldDeleteTeacherFromDatabase() {
        Teacher teacher = CreatorTestEntities.createTeachers().get(0);
        teacherService.delete(teacher);
        assertThat(teacherService.findNumberOfUsers(2, 0)).doesNotContain(teacher);
    }

    @Test
    void deleteShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> teacherService.delete(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findNumberOfUsersShouldReturnExpectedTeachersWhenInputLimitAndOffset() {
        assertThat(teacherService.findNumberOfUsers(1, 0)).containsExactly(CreatorTestEntities.createTeachers().get(0));
    }

    @Test
    void findNumberOfUsersShouldReturnEmptyListWhenInputOffsetMoreTableSize() {
        assertThat(teacherService.findNumberOfUsers(2, 10)).isEmpty();
    }

    @Test
    void loginShouldReturnExpectedTeacherWhenTeacherExistsInDatabase() {
        Teacher teacher = getTestTeacher();
        teacherService.register(teacher);
        String password = teacherService.login("test@test.ru", "Test").getPassword();
        teacher = Teacher.builder()
                .withId(3)
                .withSex("Test")
                .withName("Test")
                .withEmail("test@test.ru")
                .withPhone("Test")
                .withPassword(password)
                .withDegree("Test")
                .build();
        assertThat(teacherService.login("test@test.ru", "Test")).isEqualTo(teacher);
    }

    @Test
    void loginShouldThrowAuthorisationFailExceptionWhenInputNotValide() {
        Teacher teacher = getTestTeacher();
        teacherService.register(teacher);
        assertThatThrownBy(() -> teacherService.login("test@test.ru", "invalidpassword"))
                .isInstanceOf(AuthorisationFailException.class);
    }

    @Test
    void loginShouldThrowEntityNotExistExceptionWhenInputEmailNotExistsInDatabase() {
        assertThatThrownBy(() -> teacherService.login("notExistenEmail", "test@test.ru"))
                .isInstanceOf(EntityNotExistException.class);
    }

    private Teacher getTestTeacher() {
        return Teacher.builder()
                .withId(3)
                .withSex("Test")
                .withName("Test")
                .withEmail("test@test.ru")
                .withPhone("Test")
                .withPassword("Test")
                .withDegree("Test")
                .build();
    }
}
