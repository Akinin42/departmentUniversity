package org.university.dao.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.university.config.ApplicationContextInjector;
import org.university.dao.ScriptExecutor;
import org.university.entity.Teacher;
import org.university.utils.CreatorTestEntities;

class TeacherDaoImplTest {

    private static TeacherDaoImpl teacherDao;
    private static ScriptExecutor executor;

    @BeforeAll
    static void init() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                ApplicationContextInjector.class);
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
                .withId(3)
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
    void saveShouldThrowDataIntegrityViolationExceptionWhenInputInvalidTeacher() {
        Teacher invalidTeacher = Teacher.builder()
                .withName(null)
                .build();
        assertThatThrownBy(() -> teacherDao.save(invalidTeacher)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void saveShouldThrowNullPointerExceptionWhenInputNull() {
        assertThatThrownBy(() -> teacherDao.save(null)).isInstanceOf(NullPointerException.class);
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
    void findAllShouldReturnEmptyListWhenTeachersTableEmpty() {
        int numberRow = teacherDao.findAll().size() + 1;
        for (int i = 1; i < numberRow; i++) {
            teacherDao.deleteById(i);
        }
        assertThat(teacherDao.findAll()).isEmpty();
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
        assertThat(teacherDao.findAll()).doesNotContain(CreatorTestEntities.createTeachers().get(0));
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
