package org.university.dao.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.university.dao.ScriptExecutor;
import org.university.entity.Student;
import org.university.utils.CreatorTestEntities;
import org.university.utils.TestConfig;

@SpringJUnitConfig(TestConfig.class)
@Transactional
class StudentDaoImplTest {

    private static StudentDaoImpl studentDao;
    private static ScriptExecutor executor;

    @BeforeAll
    static void init() {
        ApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class);
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
    void saveShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> studentDao.save(null)).isInstanceOf(IllegalArgumentException.class);
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
        assertThat(studentDao.findById(id)).isEmpty();
    }
    
    @Test
    void updateShouldUpdateStudentWithInputData() {
        Student existStudent = CreatorTestEntities.createStudents().get(0);
        Student updatedStudent = Student.builder()
                .withId(existStudent.getId())
                .withSex("New sex")
                .withName("New name")
                .withEmail("New email")
                .withPhone("New phone")
                .withPassword("New password")
                .build();
        studentDao.update(updatedStudent);
        assertThat(studentDao.findById(1).get()).isEqualTo(updatedStudent);
    }
}
