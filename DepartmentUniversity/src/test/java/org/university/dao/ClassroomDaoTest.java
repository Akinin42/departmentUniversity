package org.university.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.university.entity.Classroom;
import org.university.utils.CreatorTestEntities;

@DataJpaTest
class ClassroomDaoTest {
    
    @Autowired
    private ClassroomDao classroomDao;

    @Test    
    void saveShouldSaveClassroomWhenInputValidClassroom() {
        Classroom classroom = Classroom.builder()
                .withNumber(3)
                .withAddress("test")
                .withCapacity(20)
                .build();
        classroomDao.save(classroom);
        assertThat(classroomDao.findAll()).contains(classroom);
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
    void updateShouldUpdateClassroomWithInputData() {
        Classroom existClassroom = CreatorTestEntities.createClassrooms().get(0);
        Classroom updatedClassroom = Classroom.builder()
                .withId(existClassroom.getId())
                .withNumber(17)
                .withAddress("new address")
                .withCapacity(25)
                .build();
        classroomDao.save(updatedClassroom);
        assertThat(classroomDao.findById(1).get()).isEqualTo(updatedClassroom);
    }
}
