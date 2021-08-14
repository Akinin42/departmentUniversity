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
import org.university.entity.Course;
import org.university.utils.CreatorTestEntities;
import org.university.utils.TestConfig;

@SpringJUnitConfig(TestConfig.class)
@Transactional
class CourseDaoImplTest {

    private static CourseDaoImpl courseDao;
    private static ScriptExecutor executor;

    @BeforeAll
    static void init() {
        ApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class);
        courseDao = context.getBean(CourseDaoImpl.class);
        executor = context.getBean(ScriptExecutor.class);
    }

    @BeforeEach
    void createTablesAndData() {
        executor.executeScript("inittestdb.sql");
    }

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
    void saveShouldThrowPersistenceExceptionWhenInputInvalidCourse() {
        Course invalidCourse = Course.builder()
                .withName(null)
                .build();
        assertThatThrownBy(() -> courseDao.save(invalidCourse)).isInstanceOf(PersistenceException.class);
    }

    @Test
    void saveShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> courseDao.save(null)).isInstanceOf(IllegalArgumentException.class);
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
    void findAllShouldReturnExpectedCoursesWhenInputLimitAndOffset() {
        assertThat(courseDao.findAll(2, 0)).containsExactly(CreatorTestEntities.createCourses().get(0),
                CreatorTestEntities.createCourses().get(1));
    }

    @Test
    void findAllShouldReturnEmptyListWhenInputOffsetMoreTableSize() {
        assertThat(courseDao.findAll(2, 10)).isEmpty();
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
        courseDao.update(updatedCourse);
        assertThat(courseDao.findById(1).get()).isEqualTo(updatedCourse);
    }
}
