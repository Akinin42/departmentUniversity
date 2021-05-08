package org.university.dao.impl;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.university.config.ApplicationContextInjector;
import org.university.dao.ScriptExecutor;
import org.university.entity.Group;
import org.university.utils.CreatorTestEntities;

class GroupDaoImplTest {

    private static GroupDaoImpl groupDao;
    private static ScriptExecutor executor;

    @BeforeAll
    static void init() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                ApplicationContextInjector.class);
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
                .withId(3)
                .withName("DS-45")
                .build();
        groupDao.save(group);
        assertThat(groupDao.findAll()).contains(group);
    }

    @Test
    void saveShouldThrowDataIntegrityViolationExceptionWhenInputInvalidGroup() {
        Group invalidGroup = Group.builder()
                .withName(null)
                .build();
        assertThatThrownBy(() -> groupDao.save(invalidGroup)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void saveShouldThrowNullPointerExceptionWhenInputNull() {
        assertThatThrownBy(() -> groupDao.save(null)).isInstanceOf(NullPointerException.class);
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
    void findAllShouldReturnEmptyListWhenCoursesTableEmpty() {
        int numberRow = groupDao.findAll().size() + 1;
        for (int i = 1; i < numberRow; i++) {
            groupDao.deleteById(i);
        }
        assertThat(groupDao.findAll()).isEmpty();
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
    void deleteByIdShouldDeleteGroupWithInputIdWhenThisCourseExists() {
        int id = CreatorTestEntities.createGroups().get(0).getId();
        groupDao.deleteById(id);
        assertThat(groupDao.findAll()).doesNotContain(CreatorTestEntities.createGroups().get(0));
    }
}
