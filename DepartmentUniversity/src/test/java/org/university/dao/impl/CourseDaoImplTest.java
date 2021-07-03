package org.university.dao.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.university.config.ApplicationContextInjector;
import org.university.dao.ScriptExecutor;
import org.university.entity.Course;
import org.university.utils.CreatorTestEntities;

class CourseDaoImplTest {

    private static CourseDaoImpl courseDao;
    private static ScriptExecutor executor;

    @BeforeAll
    static void init() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                ApplicationContextInjector.class);
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
                .withId(4)
                .withName("Test course")
                .withDescription("Test description")
                .build();
        courseDao.save(course);
        assertThat(courseDao.findAll()).contains(course);
    }

    @Test
    void saveShouldThrowDataIntegrityViolationExceptionWhenInputInvalidCourse() {
        Course invalidCourse = Course.builder()
                .withName(null)
                .build();
        assertThatThrownBy(() -> courseDao.save(invalidCourse)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void saveShouldThrowNullPointerExceptionWhenInputNull() {
        assertThatThrownBy(() -> courseDao.save(null)).isInstanceOf(NullPointerException.class);
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
    void findAllShouldReturnEmptyListWhenCoursesTableEmpty() {
        int numberRow = courseDao.findAll().size() + 1;
        for (int i = 1; i < numberRow; i++) {
            courseDao.deleteById(i);
        }
        assertThat(courseDao.findAll()).isEmpty();
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
        assertThat(courseDao.findAll()).doesNotContain(CreatorTestEntities.createCourses().get(0));
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
    void findAllByStudentShouldReturnExpectedCoursesWhenStudentHasIt() {
        assertThat(courseDao.findAllByStudent(2)).containsExactly(CreatorTestEntities.createCourses().get(0));
    }
    
    @Test
    void findAllByStudentShouldReturnEmptyListWhenStudentHasNotIt() {
        assertThat(courseDao.findAllByStudent(5)).isEmpty();
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
