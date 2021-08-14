package org.university.dao.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.university.dao.ScriptExecutor;
import org.university.entity.Teacher;
import org.university.utils.CreatorTestEntities;
import org.university.utils.TestConfig;

@SpringJUnitConfig(TestConfig.class)
@Transactional
class TeacherDaoImplTest {

    private static TeacherDaoImpl teacherDao;
    private static ScriptExecutor executor;

    @BeforeAll
    static void init() {
        ApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class);
        teacherDao = context.getBean(TeacherDaoImpl.class);
        executor = context.getBean(ScriptExecutor.class);
    }

    @BeforeEach
    void createTablesAndData() {
        executor.executeScript("inittestdb.sql");
    }

    @Test
    void saveShouldSaveTeacherWhenInputValidTeacher() {        
        Teacher teacher = Teacher.builder()                
                .withSex("Test")
                .withName("Test")
                .withEmail("Test")
                .withPhone("Test")
                .withPassword("Test")
                .withScientificDegree("Test")
                .build();
        teacherDao.save(teacher);
        assertThat(teacherDao.findAll()).contains(teacher);
    }

    @Test
    void saveShouldThrowPersistenceExceptionWhenInputInvalidTeacher() {
        Teacher invalidTeacher = Teacher.builder()
                .withScientificDegree(null)
                .build();
        assertThatThrownBy(() -> teacherDao.save(invalidTeacher)).isInstanceOf(PersistenceException.class);
    }

    @Test
    void saveShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> teacherDao.save(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findByIdShouldReturnEmptyOptionalWhenInputIdNotExists() {
        assertThat(teacherDao.findById(10)).isEmpty();
    }

    @Test
    void findByIdShouldReturnExpectedTeacherWhenInputExistentId() {
        assertThat(teacherDao.findById(1).get()).isEqualTo(CreatorTestEntities.createTeachers().get(0));
    }

    @Test
    void findAllShouldReturnExpectedTeachersWhenTeachersTableNotEmpty() {
        assertThat(teacherDao.findAll()).isEqualTo(CreatorTestEntities.createTeachers());
    }

    @Test
    void findAllShouldReturnExpectedTeachersWhenInputLimitAndOffset() {
        assertThat(teacherDao.findAll(1, 0)).containsExactly(CreatorTestEntities.createTeachers().get(0));
    }

    @Test
    void findAllShouldReturnEmptyListWhenInputOffsetMoreTableSize() {
        assertThat(teacherDao.findAll(2, 10)).isEmpty();
    }

    @Test
    void deleteByIdShouldDeleteTeacherWithInputIdWhenThisTeacherExists() {
        int id = CreatorTestEntities.createTeachers().get(0).getId();
        teacherDao.deleteById(id);
        assertThat(teacherDao.findById(id)).isEmpty();        
    }

    @Test
    void findByEmailShouldReturnExpectedTeacherWhenInputExistentEmail() {
        assertThat(teacherDao.findByEmail("Bob@mail.ru").get()).isEqualTo(CreatorTestEntities.createTeachers().get(0));
    }

    @Test
    void findByEmailShouldReturnEmptyOptionalWhenInputEmailNotExists() {
        assertThat(teacherDao.findByEmail("notexistenemail")).isEmpty();
    }
    
    @Test
    void updateShouldUpdateTeacherWithInputData() {
        Teacher existTeacher = CreatorTestEntities.createTeachers().get(0);
        Teacher updatedTeacher = Teacher.builder()
                .withId(existTeacher.getId())
                .withSex("New sex")
                .withName("New name")
                .withEmail("New email")
                .withPhone("New phone")
                .withPassword("New password")
                .withScientificDegree("new degree")
                .build();
        teacherDao.update(updatedTeacher);
        assertThat(teacherDao.findById(1).get()).isEqualTo(updatedTeacher);
    }
}
