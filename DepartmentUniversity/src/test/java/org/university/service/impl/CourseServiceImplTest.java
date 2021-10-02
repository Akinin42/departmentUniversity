package org.university.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.dao.CourseDao;
import org.university.dto.CourseDto;
import org.university.entity.Course;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.exceptions.InvalidCourseNameException;
import org.university.exceptions.InvalidDescriptionException;
import org.university.service.validator.CourseValidator;
import org.university.utils.CreatorTestEntities;

class CourseServiceImplTest {

    private static CourseServiceImpl courseService;
    private static CourseDao courseDaoMock;

    @BeforeAll
    static void init() {
        courseDaoMock = createCourseDaoMock();
        courseService = new CourseServiceImpl(courseDaoMock, new CourseValidator());
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
        CourseDto course = new CourseDto();
        course.setName("Law");
        assertThatThrownBy(() -> courseService.addCourse(course)).isInstanceOf(EntityAlreadyExistException.class);
    }
    
    @Test
    void addCourseShouldThrowInvalidCourseNameExceptionWhenInputInvalidName() {
        CourseDto course = new CourseDto();
        course.setId(5);
        course.setName("");
        assertThatThrownBy(() -> courseService.addCourse(course)).isInstanceOf(InvalidCourseNameException.class);
    }
    
    @Test
    void addCourseShouldThrowInvalidDescriptionExceptionWhenInputInvalidDescription() {
        CourseDto course = new CourseDto();
        course.setId(5);
        course.setName("valid");
        course.setDescription("");
        assertThatThrownBy(() -> courseService.addCourse(course)).isInstanceOf(InvalidDescriptionException.class);
    }
    
    @Test
    void addCourseShouldThrowIllegalArgumentExceptionWhenInputNull() {        
        assertThatThrownBy(() -> courseService.addCourse(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addCourseShouldSaveCourseInDatabaseWhenInputValidCourse() {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(4);
        courseDto.setName("test");
        courseDto.setDescription("test description");
        courseService.addCourse(courseDto);
        Course course = createTestCourse();
        verify(courseDaoMock).save(course);
    }
    
    @Test
    void addCourseShouldSaveCourseWithoutIdInDatabaseWhenInputValidCourse() {
        CourseDto courseDto = new CourseDto();        
        courseDto.setName("test");
        courseDto.setDescription("test description");
        courseService.addCourse(courseDto);
        Course course = Course.builder()                
                .withName("test")
                .withDescription("test description")
                .build();
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
        CourseDto courseDto = new CourseDto();
        courseDto.setId(1);
        courseDto.setName("Law");
        courseService.delete(courseDto);
        verify(courseDaoMock).deleteById(courseDto.getId());
    }

    @Test
    void deleteShouldNotDeleteCourseFromDatabaseWhenCourseNotExist() {
        CourseDto courseDto = new CourseDto();
        courseDto.setName("notexisten");
        courseService.delete(courseDto);
        verify(courseDaoMock, never()).deleteById(courseDto.getId());
    }

    @Test
    void deleteShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> courseService.delete(null)).isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void editShouldThrowEntityAlreadyExistExceptionWhenInputCourseExistInDatabase() {
        CourseDto course = new CourseDto();
        course.setId(2);
        course.setName("Law");
        assertThatThrownBy(() -> courseService.edit(course)).isInstanceOf(EntityAlreadyExistException.class);
    }
    
    @Test
    void editShouldThrowInvalidCourseNameExceptionWhenInputInvalidName() {
        CourseDto course = new CourseDto();
        course.setId(1);
        course.setName("");
        assertThatThrownBy(() -> courseService.edit(course)).isInstanceOf(InvalidCourseNameException.class);
    }
    
    @Test
    void editShouldThrowInvalidDescriptionExceptionWhenInputInvalidDescription() {
        CourseDto course = new CourseDto();
        course.setId(1);
        course.setName("Law");
        course.setDescription("");
        assertThatThrownBy(() -> courseService.edit(course)).isInstanceOf(InvalidDescriptionException.class);
    }
    
    @Test
    void editShouldThrowIllegalArgumentExceptionWhenInputNull() {        
        assertThatThrownBy(() -> courseService.edit(null)).isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void editShouldUpdateCourseInDatabaseWhenInputValidCourse() {
        CourseDto courseDto = new CourseDto();
        courseDto.setId(1);
        courseDto.setName("Law");
        courseDto.setDescription("test-courses");
        courseService.edit(courseDto);
        Course course = CreatorTestEntities.createCourses().get(0);
        verify(courseDaoMock).save(course);
    }

    private static CourseDao createCourseDaoMock() {
        CourseDao courseDaoMock = mock(CourseDao.class);
        when(courseDaoMock.findByName("Law"))
                .thenReturn(Optional.ofNullable(CreatorTestEntities.createCourses().get(0)));
        when(courseDaoMock.findByName("notexistedname"))
        .thenReturn(Optional.empty());
        when(courseDaoMock.findAll()).thenReturn(CreatorTestEntities.createCourses());
        when(courseDaoMock.findById(1)).thenReturn(Optional.ofNullable(CreatorTestEntities.createCourses().get(0)));
        when(courseDaoMock.findById(2)).thenReturn(Optional.ofNullable(CreatorTestEntities.createCourses().get(1)));
        return courseDaoMock;
    }

    private Course createTestCourse() {
        return Course.builder()
                .withId(4)
                .withName("test")
                .withDescription("test description")
                .build();
    }
}
