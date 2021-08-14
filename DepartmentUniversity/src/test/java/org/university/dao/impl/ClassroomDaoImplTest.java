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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.university.dao.ScriptExecutor;
import org.university.entity.Classroom;
import org.university.utils.CreatorTestEntities;
import org.university.utils.TestConfig;

@SpringJUnitConfig(TestConfig.class)
@Transactional
class ClassroomDaoImplTest {

    private static ClassroomDaoImpl classroomDao;
    private static ScriptExecutor executor;

    @BeforeAll
    static void init() {
        ApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class);
        classroomDao = context.getBean(ClassroomDaoImpl.class);
        executor = context.getBean(ScriptExecutor.class);
    }

    @BeforeEach
    void createTablesAndData() {
        executor.executeScript("inittestdb.sql");
    }    
    
    @Test
    @Rollback(true)
    void saveShouldSaveClassroomWhenInputValidClassroom() {
        Classroom classroom = Classroom.builder().withNumber(3).withAddress("test").withCapacity(20).build();
        classroomDao.save(classroom);
        assertThat(classroomDao.findAll()).contains(classroom);
    }

    @Test
    void saveShouldThrowPersistenceExceptionWhenInputInvalidClassroom() {
        Classroom invalidClassroom = Classroom.builder().withAddress(null).build();
        assertThatThrownBy(() -> classroomDao.save(invalidClassroom))
                .isInstanceOf(PersistenceException.class);
    }

    @Test
    void saveShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> classroomDao.save(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findByIdShouldReturnEmptyOptionalWhenInputIdNotExists() {
        assertThat(classroomDao.findById(10)).isEmpty();
    }

    @Test
    void findByIdShouldReturnExpectedClassroomWhenInputExistentId() {
        assertThat(classroomDao.findById(1).get()).isEqualTo(CreatorTestEntities.createClassrooms().get(0));
    }

    @Test
    void findAllShouldReturnExpectedClassroomsWhenClassroomsTableNotEmpty() {
        assertThat(classroomDao.findAll()).isEqualTo(CreatorTestEntities.createClassrooms());
    }

    @Test
    void findAllShouldReturnExpectedClassroomsWhenInputLimitAndOffset() {
        assertThat(classroomDao.findAll(1, 0)).containsExactly(CreatorTestEntities.createClassrooms().get(0));
    }

    @Test
    void findAllShouldReturnEmptyListWhenInputOffsetMoreTableSize() {
        assertThat(classroomDao.findAll(2, 10)).isEmpty();
    }

    @Test
    @Rollback(true)
    void deleteByIdShouldDeleteClassroomWithInputIdWhenThisClassroomExists() {
        int id = CreatorTestEntities.createClassrooms().get(0).getId();
        classroomDao.deleteById(id);
        assertThat(classroomDao.findById(id)).isEmpty();
    }

    @Test
    void findByNumberShouldReturnEmptyOptionalWhenInputNumberNotExists() {
        assertThat(classroomDao.findByNumber(10)).isEmpty();
    }

    @Test
    void findByNumberShouldReturnExpectedClassroomWhenInputExistentNumber() {
        assertThat(classroomDao.findByNumber(1).get()).isEqualTo(CreatorTestEntities.createClassrooms().get(0));
    }

    @Test
    @Rollback(true)
    void updateShouldUpdateClassroomWithInputData() {
        Classroom existClassroom = CreatorTestEntities.createClassrooms().get(0);
        Classroom updatedClassroom = Classroom.builder().withId(existClassroom.getId()).withNumber(17)
                .withAddress("new address").withCapacity(25).build();
        classroomDao.update(updatedClassroom);
        assertThat(classroomDao.findById(1).get()).isEqualTo(updatedClassroom);
    }
}
