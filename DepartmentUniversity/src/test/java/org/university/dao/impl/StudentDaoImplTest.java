package org.university.dao.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.university.config.ApplicationContextInjector;
import org.university.dao.ScriptExecutor;
import org.university.entity.Course;
import org.university.entity.Group;
import org.university.entity.Student;
import org.university.utils.CreatorTestEntities;

class StudentDaoImplTest {

    private static StudentDaoImpl studentDao;
    private static ScriptExecutor executor;

    @BeforeAll
    static void init() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                ApplicationContextInjector.class);
        studentDao = context.getBean(StudentDaoImpl.class);
        executor = context.getBean(ScriptExecutor.class);
    }

    @BeforeEach
    void createTablesAndData() {
        executor.executeScript("inittestdb.sql");
    }

    @Test
    void saveShouldSaveStudentWhenInputValidStudent() {
        Student student = Student.builder()
                .withId(7)
                .withSex("Male")
                .withName("Test student")
                .withEmail("test")
                .withPhone("test")
                .withPassword("test password")
                .build();
        studentDao.save(student);
        assertThat(studentDao.findAll()).contains(student);
    }

    @Test
    void saveShouldThrowDataIntegrityViolationExceptionWhenInputInvalidStudent() {
        Student invalidStudent = Student.builder()
                .withName(null)
                .build();
        assertThatThrownBy(() -> studentDao.save(invalidStudent)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void saveShouldThrowNullPointerExceptionWhenInputNull() {
        assertThatThrownBy(() -> studentDao.save(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void findByIdShouldReturnEmptyOptionalWhenInputIdNotExists() {
        assertThat(studentDao.findById(10)).isEmpty();
    }

    @Test
    void findByIdShouldReturnExpectedStudentWhenInputExistentId() {
        assertThat(studentDao.findById(1).get()).isEqualTo(CreatorTestEntities.createStudents().get(0));
    }

    @Test
    void findByEmailShouldReturnExpectedStudentWhenInputExistentEmail() {
        assertThat(studentDao.findByEmail("Wood@email.ru").get())
                .isEqualTo(CreatorTestEntities.createStudents().get(0));
    }

    @Test
    void findByEmailShouldReturnEmptyOptionalWhenInputEmailNotExists() {
        assertThat(studentDao.findByEmail("notexistenemail")).isEmpty();
    }

    @Test
    void findAllShouldReturnExpectedStudentsWhenStudentsTableNotEmpty() {
        assertThat(studentDao.findAll()).isEqualTo(CreatorTestEntities.createStudents());
    }

    @Test
    void findAllShouldReturnEmptyListWhenStudentsTableEmpty() {
        int numberRow = studentDao.findAll().size() + 1;
        for (int i = 1; i < numberRow; i++) {
            studentDao.deleteById(i);
        }
        assertThat(studentDao.findAll()).isEmpty();
    }

    @Test
    void findAllShouldReturnExpectedStudentsWhenInputLimitAndOffset() {
        assertThat(studentDao.findAll(4, 2)).containsExactly(CreatorTestEntities.createStudents().get(2),
                CreatorTestEntities.createStudents().get(3), CreatorTestEntities.createStudents().get(4),
                CreatorTestEntities.createStudents().get(5));
    }

    @Test
    void findAllShouldReturnEmptyListWhenInputOffsetMoreTableSize() {
        assertThat(studentDao.findAll(2, 10)).isEmpty();
    }

    @Test
    void deleteByIdShouldDeleteStudentWithInputIdWhenThisStudentExists() {
        int id = CreatorTestEntities.createStudents().get(0).getId();
        studentDao.deleteById(id);
        assertThat(studentDao.findAll()).doesNotContain(CreatorTestEntities.createStudents().get(0));
    }

    @Test
    void findAllByCourseShouldReturnExpectedStudentsWhenInputCourseName() {
        assertThat(studentDao.findAllByCourse("Law")).containsExactly(CreatorTestEntities.createStudents().get(0),
                CreatorTestEntities.createStudents().get(1), CreatorTestEntities.createStudents().get(2),
                CreatorTestEntities.createStudents().get(3));
    }

    @Test
    void findAllByCourseShouldReturnEmptyListWhenInputNotexistentCourseName() {
        assertThat(studentDao.findAllByCourse("Notexistent")).isEmpty();
    }

    @Test
    void insertStudentToCoursesShouldAddStudentToCourseWhenInputValidStudentAndCourseid() {
        Student student = CreatorTestEntities.createStudents().get(5);
        List<Course> courses = new ArrayList<>();
        courses.add(CreatorTestEntities.createCourses().get(0));
        studentDao.insertStudentToCourses(student, courses);
        assertThat(studentDao.findAllByCourse("Law")).contains(student);
    }

    @Test
    void insertStudentToCoursesShouldThrowDuplicateKeyExceptionWhenInputStudentHasInputCourses() {
        Student student = CreatorTestEntities.createStudents().get(0);
        List<Course> courses = new ArrayList<>();
        courses.add(CreatorTestEntities.createCourses().get(0));
        assertThatThrownBy(() -> studentDao.insertStudentToCourses(student, courses))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void findAllByGroupShouldReturnExpectedStudentsWhenInputGroupName() {
        assertThat(studentDao.findAllByGroup("AB-22")).containsExactly(CreatorTestEntities.createStudents().get(0),
                CreatorTestEntities.createStudents().get(1), CreatorTestEntities.createStudents().get(2),
                CreatorTestEntities.createStudents().get(3));
    }

    @Test
    void findAllByGroupShouldReturnEmptyListWhenInputNotexistentGroupeName() {
        assertThat(studentDao.findAllByGroup("Notexistent")).isEmpty();
    }

    @Test
    void insertStudentToGroupShouldAddStudentToGroupWhenInputValidStudentAndGroup() {        
        Student student = CreatorTestEntities.createStudents().get(0);
        Group group = CreatorTestEntities.createGroups().get(1);
        studentDao.insertStudentToGroup(student, group);
        assertThat(studentDao.findAllByGroup("FR-33")).contains(student);
    }

    @Test
    void insertStudentToGroupShouldThrowDuplicateKeyExceptionWhenInputStudentHasInputGroup() {
        Student student = CreatorTestEntities.createStudents().get(0);
        Group group = CreatorTestEntities.createGroups().get(0);
        assertThatThrownBy(() -> studentDao.insertStudentToGroup(student, group))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    void deleteStudentFromGroupShouldDeleteStudentFromGroupWhenStudentInThisGroup() {
        int studentId = CreatorTestEntities.createStudents().get(5).getId();
        studentDao.deleteStudentFromGroup(studentId, 2);
        assertThat(studentDao.findAllByGroup("FR-33")).doesNotContain(CreatorTestEntities.createStudents().get(5));
    }

    @Test
    void deleteStudentFromCourseShouldDeleteStudentFromCourseWhenStudentInThisCourse() {
        int studentId = CreatorTestEntities.createStudents().get(0).getId();
        studentDao.deleteStudentFromCourse(studentId, 1);
        assertThat(studentDao.findAllByCourse("Law")).doesNotContain(CreatorTestEntities.createStudents().get(0));
    }
}
