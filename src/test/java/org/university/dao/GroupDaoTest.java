package org.university.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.university.entity.Group;
import org.university.entity.Student;
import org.university.utils.CreatorTestEntities;

@DataJpaTest
class GroupDaoTest {
    
    @Autowired
    private GroupDao groupDao;

    @Test    
    void saveShouldSaveGroupWhenInputValidGroup() {
        Group group = Group.builder()
                .withName("DS-45")
                .build();
        groupDao.save(group);
        assertThat(groupDao.findAll()).contains(group);
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
        groupDao.save(updatedGroup);
        assertThat(groupDao.findById(1).get()).isEqualTo(updatedGroup);
    }

    @Test    
    void updateStudentsShouldUpdateStudentsInInputGroup() {
        Group groupWithoutStudent = CreatorTestEntities.createGroups().get(1);        
        Student student = CreatorTestEntities.createStudents().get(5);
        groupWithoutStudent.removeStudent(student);
        groupDao.save(groupWithoutStudent);        
        assertThat(groupDao.findById(2).get()).isEqualTo(groupWithoutStudent);
    }
}
