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
import org.university.entity.Group;
import org.university.io.FileReader;
import org.university.utils.CreatorDataSource;
import org.university.utils.CreatorTestEntities;

class GroupDaoImplTest {

    private static GroupDaoImpl groupDao;
    private static ScriptExecutor executor;

    @BeforeAll
    static void init() {
        DataSource dataSource = CreatorDataSource.createTestDataSource();
        groupDao = new GroupDaoImpl(new JdbcTemplate(dataSource));
        executor = new ScriptExecutor(dataSource, new FileReader());
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
        Group updatedGroup = Group.builder()
                .withId(existGroup.getId())
                .withName("new")
                .build();
        groupDao.update(updatedGroup);
        assertThat(groupDao.findById(1).get()).isEqualTo(updatedGroup);
    }
}
