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
import org.university.dao.ClassroomDao;
import org.university.dto.ClassroomDto;
import org.university.entity.Classroom;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.utils.CreatorTestEntities;

class ClassroomServiceImplTest {

    private static ClassroomServiceImpl classroomService;
    private static ClassroomDao classroomDaoMock;

    @BeforeAll
    static void init() {
        classroomDaoMock = createClassroomDaoMock();
        classroomService = new ClassroomServiceImpl(classroomDaoMock);
    }

    @Test
    void createClassroomShouldThrowEntityNotExistExceptionWhenClassroomWithInputNumberNotExists() {
        assertThatThrownBy(() -> classroomService.createClassroom(152)).isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void createClassroomShouldReturnExpectedClassroomWhenInputNumberExists() {
        Classroom classroom = CreatorTestEntities.createClassrooms().get(0);
        assertThat(classroomService.createClassroom(1)).isEqualTo(classroom);
    }

    @Test
    void addClassroomShouldThrowEntityAlreadyExistExceptionWhenInputClassroomExistInDatabase() {
        ClassroomDto classroom = new ClassroomDto();
        classroom.setNumber(1);
        assertThatThrownBy(() -> classroomService.addClassroom(classroom))
                .isInstanceOf(EntityAlreadyExistException.class);
    }

    @Test
    void addClassroomShouldSaveClassroomInDatabaseWhenInputValidClassroom() {
        ClassroomDto classroomDto = new ClassroomDto();
        classroomDto.setId(3);
        classroomDto.setNumber(3);
        classroomDto.setAddress("test address");
        classroomDto.setCapacity(30);
        classroomService.addClassroom(classroomDto);
        Classroom classroom = createTestClassroomWithCapacity(30);
        verify(classroomDaoMock).save(classroom);
    }

    @Test
    void addClassroomShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> classroomService.addClassroom(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findAllClassroomsShouldReturnExpectedClassroomsWhenTheyExist() {
        assertThat(classroomService.findAllClassrooms()).isEqualTo(CreatorTestEntities.createClassrooms());
    }

    @Test
    void findAllClassroomsShouldReturnEmptyListWhenClassroomsTableEmpty() {
        when(classroomDaoMock.findAll()).thenReturn(new ArrayList<>());
        assertThat(classroomService.findAllClassrooms()).isEmpty();
    }

    @Test
    void deleteShouldDeleteClassroomFromDatabaseWhenClassroomExist() {
        ClassroomDto classroomDto = new ClassroomDto();
        classroomDto.setId(1);
        classroomService.delete(classroomDto);
        verify(classroomDaoMock).deleteById(classroomDto.getId());
    }

    @Test
    void deleteShouldNotDeleteClassroomFromDatabaseWhenClassroomNotExist() {
        ClassroomDto classroomDto = new ClassroomDto();
        classroomDto.setId(10);
        classroomService.delete(classroomDto);
        verify(classroomDaoMock, never()).deleteById(classroomDto.getId());
    }

    @Test
    void deleteShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> classroomService.delete(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void editShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> classroomService.edit(null)).isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void editShouldUpdateClassroomInDatabaseWhenInputValidClassroom() {
        ClassroomDao classroomDaoMock = mock(ClassroomDao.class);
        ClassroomServiceImpl classroomService = new ClassroomServiceImpl(classroomDaoMock);
        ClassroomDto classroomDto = new ClassroomDto();
        classroomDto.setId(3);
        classroomDto.setNumber(3);
        classroomDto.setAddress("test address");
        classroomDto.setCapacity(30);
        classroomService.edit(classroomDto);
        Classroom classroom = createTestClassroomWithCapacity(30);
        verify(classroomDaoMock).save(classroom);
    }

    private static ClassroomDao createClassroomDaoMock() {
        ClassroomDao classroomDaoMock = mock(ClassroomDao.class);
        when(classroomDaoMock.findByNumber(1))
                .thenReturn(Optional.ofNullable(CreatorTestEntities.createClassrooms().get(0)));
        when(classroomDaoMock.findByNumber(152)).thenReturn(Optional.empty());
        when(classroomDaoMock.findById(1))
                .thenReturn(Optional.ofNullable(CreatorTestEntities.createClassrooms().get(0)));
        when(classroomDaoMock.findAll()).thenReturn(CreatorTestEntities.createClassrooms());
        when(classroomDaoMock.existsById(1)).thenReturn(true);
        return classroomDaoMock;
    }

    private Classroom createTestClassroomWithCapacity(int capacity) {
        return Classroom.builder()
                .withId(3)
                .withNumber(3)
                .withAddress("test address")
                .withCapacity(capacity)
                .build();
    }
}
