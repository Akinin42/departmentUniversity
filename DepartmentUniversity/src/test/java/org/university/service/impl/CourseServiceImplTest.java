package org.university.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.dao.impl.CourseDaoImpl;
import org.university.entity.Course;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.utils.CreatorTestEntities;

class CourseServiceImplTest {

    private static CourseServiceImpl courseService;
    private static CourseDaoImpl courseDaoMock;

    @BeforeAll
    static void init() {
        courseDaoMock = createCourseDaoMock();
        courseService = new CourseServiceImpl(courseDaoMock);
    }

    @Test
    void createCourseShouldThrowEntityNotExistExceptionWhenGroupWithInputNameNotExists() {
        assertThatThrownBy(() -> courseService.createCourse("notexistedname"))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void createCourseShouldReturnExpectedCourseWhenInputNameExists() {
        Course course = CreatorTestEntities.createCourses().get(0);
        assertThat(courseService.createCourse("Law")).isEqualTo(course);
    }

    @Test
    void addCourseShouldThrowEntityAlreadyExistExceptionWhenInputCourseExistInDatabase() {
        Course course = CreatorTestEntities.createCourses().get(0);
        assertThatThrownBy(() -> courseService.addCourse(course)).isInstanceOf(EntityAlreadyExistException.class);
    }

    @Test
    void addCourseShouldSaveCourseInDatabaseWhenInputValidCourse() {
        Course course = createTestCourse();
        courseService.addCourse(course);
        verify(courseDaoMock).save(course);
    }

    @Test
    void findAllCoursesShouldReturnExpectedCoursesWhenTheyExist() {
        assertThat(courseService.findAllCourses()).isEqualTo(CreatorTestEntities.createCourses());
    }

    @Test
    void findAllCoursesShouldReturnEmptyListWhenCoursesTableEmpty() {
        when(courseDaoMock.findAll()).thenReturn(new ArrayList<>());
        assertThat(courseService.findAllCourses()).isEmpty();
    }

    @Test
    void deleteShouldDeleteCourseFromDatabaseWhenCourseExist() {
        Course course = CreatorTestEntities.createCourses().get(0);
        courseService.delete(course);
        verify(courseDaoMock).deleteById(course.getId());
    }

    @Test
    void deleteShouldNotDeleteCourseFromDatabaseWhenCourseNotExist() {
        Course course = createTestCourse();
        courseService.delete(course);
        verify(courseDaoMock, never()).deleteById(course.getId());
    }

    @Test
    void deleteShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> courseService.delete(null)).isInstanceOf(IllegalArgumentException.class);
    }

    private static CourseDaoImpl createCourseDaoMock() {
        CourseDaoImpl courseDaoMock = mock(CourseDaoImpl.class);
        when(courseDaoMock.findByName("Law"))
                .thenReturn(Optional.ofNullable(CreatorTestEntities.createCourses().get(0)));
        when(courseDaoMock.findByName("notexistedname"))
        .thenReturn(Optional.empty());
        when(courseDaoMock.findAll()).thenReturn(CreatorTestEntities.createCourses());
        when(courseDaoMock.findById(1)).thenReturn(Optional.ofNullable(CreatorTestEntities.createCourses().get(0)));
        return courseDaoMock;
    }

    private Course createTestCourse() {
        return Course.builder()
                .withId(4)
                .withName("test")
                .withDescription("test")
                .build();
    }
}
