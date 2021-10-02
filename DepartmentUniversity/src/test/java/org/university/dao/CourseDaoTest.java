package org.university.dao;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.university.entity.Course;
import org.university.utils.CreatorTestEntities;

@DataJpaTest
class CourseDaoTest {
    
    @Autowired
    private CourseDao courseDao;

    @Test
    void saveShouldSaveCourseWhenInputValidCourse() {
        Course course = Course.builder()
                .withName("Test course")
                .withDescription("Test description")
                .build();
        courseDao.save(course);
        assertThat(courseDao.findAll()).contains(course);
    }

    @Test
    void findByIdShouldReturnEmptyOptionalWhenInputIdNotExists() {
        assertThat(courseDao.findById(10)).isEmpty();
    }

    @Test
    void findByIdShouldReturnExpectedCourseWhenInputExistentId() {
        assertThat(courseDao.findById(1).get()).isEqualTo(CreatorTestEntities.createCourses().get(0));
    }

    @Test
    void findAllShouldReturnExpectedCoursesWhenCoursesTableNotEmpty() {
        assertThat(courseDao.findAll()).isEqualTo(CreatorTestEntities.createCourses());
    }

    @Test
    void deleteByIdShouldDeleteCourseWithInputIdWhenThisCourseExists() {
        int id = CreatorTestEntities.createCourses().get(0).getId();
        courseDao.deleteById(id);
        assertThat(courseDao.findById(id)).isEmpty();
    }
    
    @Test
    void findByNameShouldReturnEmptyOptionalWhenInputNameNotExists() {
        assertThat(courseDao.findByName("notexistname")).isEmpty();
    }

    @Test
    void findByNameShouldReturnExpectedCourseWhenInputExistentName() {
        assertThat(courseDao.findByName("Law").get()).isEqualTo(CreatorTestEntities.createCourses().get(0));
    }
    
    @Test
    void updateShouldUpdateCourseWithInputData() {
        Course existCourse = CreatorTestEntities.createCourses().get(0);
        Course updatedCourse = Course.builder()
                .withId(existCourse.getId())
                .withName("new name")
                .withDescription("new")
                .build();
        courseDao.save(updatedCourse);
        assertThat(courseDao.findById(1).get()).isEqualTo(updatedCourse);
    }

}
