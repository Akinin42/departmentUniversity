package org.university.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.university.entity.Teacher;
import org.university.utils.CreatorTestEntities;

@DataJpaTest
class TeacherDaoTest {
    
    @Autowired
    private TeacherDao teacherDao;

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
        Pageable limit = PageRequest.of(0,1);
        assertThat(teacherDao.findAll(limit)).containsExactly(CreatorTestEntities.createTeachers().get(0));
    }

    @Test
    void findAllShouldReturnEmptyListWhenInputOffsetMoreTableSize() {
        Pageable limit = PageRequest.of(3,5);
        assertThat(teacherDao.findAll(limit)).isEmpty();
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
        teacherDao.save(updatedTeacher);
        assertThat(teacherDao.findById(1).get()).isEqualTo(updatedTeacher);
    }

}
