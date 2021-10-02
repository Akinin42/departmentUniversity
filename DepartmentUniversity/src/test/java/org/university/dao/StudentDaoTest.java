package org.university.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.university.entity.Student;
import org.university.utils.CreatorTestEntities;

@DataJpaTest
class StudentDaoTest {
    
    @Autowired
    StudentDao studentDao;

    @Test
    void saveShouldSaveStudentWhenInputValidStudent() {
        Student student = Student.builder()                
                .withSex("Male")
                .withName("Test student")
                .withEmail("test")
                .withPhone("test")
                .withPassword("test password")
                .build();
        studentDao.save(student);
        assertThat(studentDao.findAll()).contains(student);
    }

    @Test
    void findByIdShouldReturnEmptyOptionalWhenInputIdNotExists() {
        assertThat(studentDao.findById(10)).isEmpty();
    }

    @Test
    void findByIdShouldReturnExpectedStudentWhenInputExistentId() {
        assertThat(studentDao.findById(1).get()).isEqualTo(CreatorTestEntities.createStudents().get(0));
    }

    @Test
    void findByEmailShouldReturnExpectedStudentWhenInputExistentEmail() {
        assertThat(studentDao.findByEmail("Wood@email.ru").get())
                .isEqualTo(CreatorTestEntities.createStudents().get(0));
    }

    @Test
    void findByEmailShouldReturnEmptyOptionalWhenInputEmailNotExists() {
        assertThat(studentDao.findByEmail("notexistenemail")).isEmpty();
    }

    @Test
    void findAllShouldReturnExpectedStudentsWhenStudentsTableNotEmpty() {
        assertThat(studentDao.findAll()).isEqualTo(CreatorTestEntities.createStudents());
    }

    @Test
    void findAllShouldReturnExpectedStudentsWhenInputLimitAndOffset() {
        Pageable limit = PageRequest.of(0,3);
        assertThat(studentDao.findAll(limit)).containsExactly(CreatorTestEntities.createStudents().get(0),
                CreatorTestEntities.createStudents().get(1), CreatorTestEntities.createStudents().get(2));
    }

    @Test
    void findAllShouldReturnEmptyListWhenInputOffsetMoreTableSize() {
        Pageable limit = PageRequest.of(3,5);
        assertThat(studentDao.findAll(limit)).isEmpty();
    }

    @Test
    void deleteByIdShouldDeleteStudentWithInputIdWhenThisStudentExists() {
        int id = CreatorTestEntities.createStudents().get(0).getId();
        studentDao.deleteById(id);
        assertThat(studentDao.findById(id)).isEmpty();
    }
    
    @Test
    void updateShouldUpdateStudentWithInputData() {
        Student existStudent = CreatorTestEntities.createStudents().get(0);
        Student updatedStudent = Student.builder()
                .withId(existStudent.getId())
                .withSex("New sex")
                .withName("New name")
                .withEmail("New email")
                .withPhone("New phone")
                .withPassword("New password")
                .build();
        studentDao.save(updatedStudent);
        assertThat(studentDao.findById(1).get()).isEqualTo(updatedStudent);
    }
}
