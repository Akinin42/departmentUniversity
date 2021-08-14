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
import org.university.entity.Group;
import org.university.entity.Student;
import org.university.utils.CreatorTestEntities;
import org.university.utils.TestConfig;

@SpringJUnitConfig(TestConfig.class)
@Transactional
class GroupDaoImplTest {

    private static GroupDaoImpl groupDao;
    private static ScriptExecutor executor;

    @BeforeAll
    static void init() {
        ApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class);
        groupDao = context.getBean(GroupDaoImpl.class);
        executor = context.getBean(ScriptExecutor.class);        
    }

    @BeforeEach
    void createTablesAndData() {
        executor.executeScript("inittestdb.sql");
    }

    @Test    
    void saveShouldSaveGroupWhenInputValidGroup() {
        Group group = Group.builder()
                .withName("DS-45")
                .build();
        groupDao.save(group);
        assertThat(groupDao.findAll()).contains(group);
    }

    @Test
    void saveShouldThrowPersistenceExceptionWhenInputInvalidGroup() {
        Group invalidGroup = Group.builder().withName(null).build();
        assertThatThrownBy(() -> groupDao.save(invalidGroup)).isInstanceOf(PersistenceException.class);
    }

    @Test
    void saveShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> groupDao.save(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findByIdShouldReturnEmptyOptionalWhenInputIdNotExists() {
        assertThat(groupDao.findById(10)).isEmpty();
    }

    @Test
    void findByIdShouldReturnExpectedGroupWhenInputExistentId() {
        assertThat(groupDao.findById(1).get()).isEqualTo(CreatorTestEntities.createGroups().get(0));
    }

    @Test
    void findAllShouldReturnExpectedGroupsWhenGroupsTableNotEmpty() {
        assertThat(groupDao.findAll()).isEqualTo(CreatorTestEntities.createGroups());
    }

    @Test
    void findAllShouldReturnExpectedGroupsWhenInputLimitAndOffset() {
        assertThat(groupDao.findAll(1, 0)).containsExactly(CreatorTestEntities.createGroups().get(0));
    }

    @Test
    void findAllShouldReturnEmptyListWhenInputOffsetMoreTableSize() {
        assertThat(groupDao.findAll(2, 10)).isEmpty();
    }

    @Test
    void deleteByIdShouldDeleteGroupWithInputIdWhenThisExists() {
        int id = CreatorTestEntities.createGroups().get(0).getId();
        groupDao.deleteById(id);
        assertThat(groupDao.findById(id)).isEmpty();
    }

    @Test
    void findByNameShouldReturnEmptyOptionalWhenInputNameNotExists() {
        assertThat(groupDao.findByName("notexistname")).isEmpty();
    }

    @Test
    void findByNameShouldReturnExpectedGroupWhenInputExistentName() {
        assertThat(groupDao.findByName("AB-22").get()).isEqualTo(CreatorTestEntities.createGroups().get(0));
    }

    @Test
    void updateShouldUpdateGroupWithInputData() {
        Group existGroup = CreatorTestEntities.createGroups().get(0);
        Group updatedGroup = Group.builder().withId(existGroup.getId()).withName("new").build();
        groupDao.update(updatedGroup);
        assertThat(groupDao.findById(1).get()).isEqualTo(updatedGroup);
    }

    @Test    
    void updateStudentsShouldUpdateStudentsInInputGroup() {
        Group groupWithoutStudent = CreatorTestEntities.createGroups().get(1);        
        Student student = CreatorTestEntities.createStudents().get(5);
        groupWithoutStudent.removeStudent(student);
        groupDao.updateStudents(groupWithoutStudent);        
        assertThat(groupDao.findById(2).get()).isEqualTo(groupWithoutStudent);
    }
}
