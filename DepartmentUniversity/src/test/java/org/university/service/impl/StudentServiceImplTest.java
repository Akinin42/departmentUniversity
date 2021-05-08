package org.university.service.impl;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.university.config.ApplicationContextInjector;
import org.university.dao.ScriptExecutor;
import org.university.dao.impl.StudentDaoImpl;
import org.university.entity.Course;
import org.university.entity.Group;
import org.university.entity.Student;
import org.university.exceptions.AuthorisationFailException;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.exceptions.InvalidEmailException;
import org.university.utils.CreatorTestEntities;

class StudentServiceImplTest {

    private static StudentServiceImpl studentService;
    private static ScriptExecutor executor;
    private static StudentDaoImpl studentDao;

    @BeforeAll
    static void init() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                ApplicationContextInjector.class);
        studentService = context.getBean(StudentServiceImpl.class);
        executor = context.getBean(ScriptExecutor.class);
        studentDao = context.getBean(StudentDaoImpl.class);
    }

    @BeforeEach
    void createTablesAndData() {
        executor.executeScript("inittestdb.sql");
    }

    @Test
    void registerShouldSaveStudentToDatabaseWhenInputStudentNotExistThere() {
        Student student = getTestStudent();
        studentService.register(student);
        student = studentService.login("test@test.ru", "Test");
        assertThat(studentService.findNumberOfUsers(7, 0)).contains(student);
    }

    @Test
    void registerShouldThrowEntityAlreadyExistExceptionWhenInputStudentExistsInDatabase() {
        Student student = CreatorTestEntities.createStudents().get(0);
        assertThatThrownBy(() -> studentService.register(student)).isInstanceOf(EntityAlreadyExistException.class);
    }

    @Test
    void registerShouldThrowInvalidEmailExceptionWhenInputStudentHasInvalidEmail() {
        Student student = Student.builder()
                .withId(7)
                .withSex("Test")
                .withName("Test")
                .withEmail("invalidemail")
                .withPhone("Test")
                .withPassword("Test")
                .build();
        assertThatThrownBy(() -> studentService.register(student)).isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void registerShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> studentService.register(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteShouldDeleteStudentFromDatabase() {
        Student student = CreatorTestEntities.createStudents().get(0);
        studentService.delete(student);
        assertThat(studentService.findNumberOfUsers(2, 0)).doesNotContain(student);
    }

    @Test
    void deleteShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> studentService.delete(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findNumberOfUsersShouldReturnExpectedTStudentsWhenInputLimitAndOffset() {
        assertThat(studentService.findNumberOfUsers(1, 0)).containsExactly(CreatorTestEntities.createStudents().get(0));
    }

    @Test
    void findNumberOfUsersShouldReturnEmptyListWhenInputOffsetMoreTableSize() {
        assertThat(studentService.findNumberOfUsers(2, 10)).isEmpty();
    }

    @Test
    void loginShouldReturnExpectedStudentWhenStudentExistsInDatabase() {
        Student student = getTestStudent();
        studentService.register(student);
        String password = studentService.login("test@test.ru", "Test").getPassword();
        student = Student.builder()
                .withId(7)
                .withSex("Test")
                .withName("Test")
                .withEmail("test@test.ru")
                .withPhone("Test")
                .withPassword(password)
                .build();
        assertThat(studentService.login("test@test.ru", "Test")).isEqualTo(student);
    }

    @Test
    void loginShouldThrowAuthorisationFailExceptionWhenInputNotValide() {
        Student student = getTestStudent();
        studentService.register(student);
        assertThatThrownBy(() -> studentService.login("test@test.ru", "invalidpassword"))
                .isInstanceOf(AuthorisationFailException.class);
    }

    @Test
    void loginShouldThrowEntityNotExistExceptionWhenInputEmailNotExistsInDatabase() {
        assertThatThrownBy(() -> studentService.login("notExistenEmail", "test@test.ru"))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void addStudentToGroupShouldAddStudentToGroupWhenInputGroupAndStudentExists() {
        Student student = CreatorTestEntities.createStudents().get(0);
        Group group = CreatorTestEntities.createGroups().get(1);
        studentService.addStudentToGroup(student, group);
        assertThat(studentDao.findAllByGroup("FR-33")).contains(student);
    }

    @Test
    void addStudentToGroupShouldThrowEntityNotExistExceptionWhenInputStudentNotExists() {
        Student student = getTestStudent();
        Group group = CreatorTestEntities.createGroups().get(1);
        assertThatThrownBy(() -> studentService.addStudentToGroup(student, group))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void addStudentToGroupShouldThrowEntityNotExistExceptionWhenInputGroupNotExists() {
        Student student = CreatorTestEntities.createStudents().get(0);
        Group group = Group.builder()
                .withId(5)
                .withName("notExist")
                .build();
        assertThatThrownBy(() -> studentService.addStudentToGroup(student, group))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void addStudentToGroupShouldThrowIllegalArgumentExceptionWhenInputStudentNull() {
        Group group = CreatorTestEntities.createGroups().get(1);
        assertThatThrownBy(() -> studentService.addStudentToGroup(null, group))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addStudentToGroupShouldThrowIllegalArgumentExceptionWhenInputGroupNull() {
        Student student = CreatorTestEntities.createStudents().get(0);
        assertThatThrownBy(() -> studentService.addStudentToGroup(student, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteStudentFromGroupShouldDeleteStudentFromGroupWhenInputGroupAndStudentExists() {
        Student student = CreatorTestEntities.createStudents().get(0);
        Group group = CreatorTestEntities.createGroups().get(0);
        studentService.deleteStudentFromGroup(student, group);
        assertThat(studentDao.findAllByGroup("AB-22")).doesNotContain(student);
    }

    @Test
    void deleteStudentFromGroupShouldThrowIllegalArgumentExceptionWhenInputStudentNull() {
        Group group = CreatorTestEntities.createGroups().get(0);
        assertThatThrownBy(() -> studentService.deleteStudentFromGroup(null, group))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteStudentFromGroupShouldThrowIllegalArgumentExceptionWhenInputGroupNull() {
        Student student = CreatorTestEntities.createStudents().get(0);
        assertThatThrownBy(() -> studentService.deleteStudentFromGroup(student, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addStudentToCourseShouldAddStudentToCourseWhenInputCourseAndStudentExists() {
        Student student = CreatorTestEntities.createStudents().get(5);
        Course course = CreatorTestEntities.createCourses().get(0);
        studentService.addStudentToCourse(student, course);
        assertThat(studentDao.findAllByCourse("Law")).contains(student);
    }

    @Test
    void addStudentToCourseShouldThrowEntityNotExistExceptionWhenInputStudentNotExists() {
        Student student = getTestStudent();
        Course course = CreatorTestEntities.createCourses().get(0);
        assertThatThrownBy(() -> studentService.addStudentToCourse(student, course))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void addStudentToCourseShouldThrowEntityNotExistExceptionWhenInputCourseNotExists() {
        Student student = CreatorTestEntities.createStudents().get(0);
        Course course = Course.builder()
                .withId(5)
                .withName("notExist")
                .withDescription("test")
                .build();
        assertThatThrownBy(() -> studentService.addStudentToCourse(student, course))
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
        Student student = CreatorTestEntities.createStudents().get(0);
        assertThatThrownBy(() -> studentService.addStudentToCourse(student, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteStudentFromCourseShouldDeleteStudentFromCourseWhenInputGroupAndStudentExists() {
        Student student = CreatorTestEntities.createStudents().get(0);
        Course course = CreatorTestEntities.createCourses().get(0);
        studentService.deleteStudentFromCourse(student, course);
        assertThat(studentDao.findAllByCourse("Law")).doesNotContain(student);
    }

    @Test
    void deleteStudentFromCourseShouldThrowIllegalArgumentExceptionWhenInputStudentNull() {
        Course course = CreatorTestEntities.createCourses().get(0);
        assertThatThrownBy(() -> studentService.deleteStudentFromCourse(null, course))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteStudentFromCourseShouldThrowIllegalArgumentExceptionWhenInputCourseNull() {
        Student student = CreatorTestEntities.createStudents().get(0);
        assertThatThrownBy(() -> studentService.deleteStudentFromCourse(student, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Student getTestStudent() {
        return Student.builder()
                .withId(7)
                .withSex("Test")
                .withName("Test")
                .withEmail("test@test.ru")
                .withPhone("Test")
                .withPassword("Test")
                .build();
    }
}
