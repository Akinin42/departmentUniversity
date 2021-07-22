package org.university.dao.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.university.dao.ScriptExecutor;
import org.university.entity.Classroom;
import org.university.io.FileReader;
import org.university.utils.CreatorDataSource;
import org.university.utils.CreatorTestEntities;

class ClassroomDaoImplTest {    
    private static ClassroomDaoImpl classroomDao;   
    private static ScriptExecutor executor;

    @BeforeAll
    static void init(){
        DataSource dataSource = CreatorDataSource.createTestDataSource();
        classroomDao = new ClassroomDaoImpl(new JdbcTemplate(dataSource));
        executor = new ScriptExecutor(dataSource, new FileReader());
    }

    @BeforeEach
    void createTablesAndData() {
        executor.executeScript("inittestdb.sql");
    }

    @Test
    void saveShouldSaveClassroomWhenInputValidClassroom() {
        Classroom classroom = Classroom.builder()
                .withId(3)
                .withNumber(3)
                .withAddress("test")
                .withCapacity(20)
                .build();
        classroomDao.save(classroom);        
        assertThat(classroomDao.findAll()).contains(classroom);        
    }

    @Test
    void saveShouldThrowDataIntegrityViolationExceptionWhenInputInvalidClassroom() {
        Classroom invalidClassroom = Classroom.builder()
                .withAddress(null)
                .build();
        assertThatThrownBy(() -> classroomDao.save(invalidClassroom))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void saveShouldThrowNullPointerExceptionWhenInputNull() {
        assertThatThrownBy(() -> classroomDao.save(null)).isInstanceOf(NullPointerException.class);
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
    void findAllShouldReturnEmptyListWhenClassroomsTableEmpty() {
        int numberRow = classroomDao.findAll().size() + 1;
        for (int i = 1; i < numberRow; i++) {
            classroomDao.deleteById(i);
        }
        assertThat(classroomDao.findAll()).isEmpty();
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
    void deleteByIdShouldDeleteClassroomWithInputIdWhenThisClassroomExists() {
        int id = CreatorTestEntities.createClassrooms().get(0).getId();
        classroomDao.deleteById(id);
        assertThat(classroomDao.findAll()).doesNotContain(CreatorTestEntities.createClassrooms().get(0));
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
    void updateShouldUpdateClassroomWithInputData() {
        Classroom existClassroom = CreatorTestEntities.createClassrooms().get(0);
        Classroom updatedClassroom = Classroom.builder()
                .withId(existClassroom.getId())
                .withNumber(17)
                .withAddress("new address")
                .withCapacity(25)
                .build();
        classroomDao.update(updatedClassroom);
        assertThat(classroomDao.findById(1).get()).isEqualTo(updatedClassroom);
    }
}
