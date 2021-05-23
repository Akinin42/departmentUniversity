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
import org.university.dao.impl.ClassroomDaoImpl;
import org.university.entity.Classroom;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.exceptions.InvalidClassroomCapacityException;
import org.university.utils.CreatorTestEntities;

class ClassroomServiceImplTest {

    private static ClassroomServiceImpl classroomService;
    private static ClassroomDaoImpl classroomDaoMock;

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
        Classroom classroom = CreatorTestEntities.createClassrooms().get(0);
        assertThatThrownBy(() -> classroomService.addClassroom(classroom))
                .isInstanceOf(EntityAlreadyExistException.class);
    }

    @Test
    void addClassroomShouldSaveClassroomInDatabaseWhenInputValidClassroom() {
        Classroom classroom = createTestClassroomWithCapacity(40);
        classroomService.addClassroom(classroom);
        verify(classroomDaoMock).save(classroom);
    }

    @Test
    void addClassroomShouldThrowInvalidClassroomCapacityExceptionWhenInputClassroomCapacityNegative() {
        Classroom classroom = createTestClassroomWithCapacity(-5);
        assertThatThrownBy(() -> classroomService.addClassroom(classroom))
                .isInstanceOf(InvalidClassroomCapacityException.class);
    }

    @Test
    void addClassroomShouldThrowInvalidClassroomCapacityExceptionWhenInputClassroomCapacityZero() {
        Classroom classroom = createTestClassroomWithCapacity(0);
        assertThatThrownBy(() -> classroomService.addClassroom(classroom))
                .isInstanceOf(InvalidClassroomCapacityException.class);
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
        Classroom classroom = CreatorTestEntities.createClassrooms().get(0);
        classroomService.delete(classroom);
        verify(classroomDaoMock).deleteById(classroom.getId());
    }

    @Test
    void deleteShouldNotDeleteClassroomFromDatabaseWhenClassroomNotExist() {
        Classroom classroom = createTestClassroomWithCapacity(10);
        classroomService.delete(classroom);
        verify(classroomDaoMock, never()).deleteById(classroom.getId());
    }

    @Test
    void deleteShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> classroomService.delete(null)).isInstanceOf(IllegalArgumentException.class);
    }

    private static ClassroomDaoImpl createClassroomDaoMock() {
        ClassroomDaoImpl classroomDaoMock = mock(ClassroomDaoImpl.class);
        when(classroomDaoMock.findByNumber(1))
                .thenReturn(Optional.ofNullable(CreatorTestEntities.createClassrooms().get(0)));
        when(classroomDaoMock.findByNumber(152)).thenReturn(Optional.empty());
        when(classroomDaoMock.findById(1))
                .thenReturn(Optional.ofNullable(CreatorTestEntities.createClassrooms().get(0)));
        when(classroomDaoMock.findAll()).thenReturn(CreatorTestEntities.createClassrooms());
        return classroomDaoMock;
    }

    private Classroom createTestClassroomWithCapacity(int capacity) {
        return Classroom.builder()
                .withId(3)
                .withNumber(3)
                .withAddress("test")
                .withCapacity(capacity)
                .build();
    }
}
