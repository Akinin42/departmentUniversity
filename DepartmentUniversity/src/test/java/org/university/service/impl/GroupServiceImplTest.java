package org.university.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.dao.impl.GroupDaoImpl;
import org.university.dao.impl.StudentDaoImpl;
import org.university.dto.GroupDto;
import org.university.entity.Group;
import org.university.entity.Student;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.exceptions.InvalidGroupNameException;
import org.university.service.validator.GroupValidator;
import org.university.utils.CreatorTestEntities;

class GroupServiceImplTest {

    private static GroupServiceImpl groupService;
    private static GroupDaoImpl groupDaoMock;

    @BeforeAll
    static void init() {
        groupDaoMock = createGroupDaoMock();
        groupService = new GroupServiceImpl(groupDaoMock, createStudentDaoMock(), new GroupValidator());
    }

    @Test
    void createGroupShouldReturnExpectedGroupWithStudentsWhenGroupWithInputNameExists() {
        Group group = createGroupsWithStudents().get(1);
        assertThat(groupService.createGroup("FR-33")).isEqualTo(group);
    }

    @Test
    void createGroupShouldThrowEntityNotExistExceptionWhenGroupWithInputNameNotExists() {
        assertThatThrownBy(() -> groupService.createGroup("notexistedname"))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void createGroupShouldThrowEntityNotExistExceptionWhenGroupWithInputNull() {
        assertThatThrownBy(() -> groupService.createGroup(null)).isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void addGroupShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> groupService.addGroup(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addGroupShouldThrowInvalidGroupNameExceptionWhenInputGroupWithInvalidName() {
        GroupDto group = new GroupDto();
        group.setName("invalid name");
        assertThatThrownBy(() -> groupService.addGroup(group)).isInstanceOf(InvalidGroupNameException.class);
    }

    @Test
    void addGroupShouldThrowEntityAlreadyExistExceptionWhenInputGroupExistInDatabase() {
        GroupDto group = new GroupDto();
        group.setId(1);
        group.setName("AB-22");
        assertThatThrownBy(() -> groupService.addGroup(group)).isInstanceOf(EntityAlreadyExistException.class);
    }

    @Test
    void addGroupShouldSaveGroupAndStudentsInDatabasesWhenInputValidGroup() {
        List<Student> students = CreatorTestEntities.createStudents();
        GroupDto group = new GroupDto();
        group.setId(3);
        group.setName("FF-55");
        group.setStudents(students);
        Group groupEntity = Group.builder()
                .withId(3)
                .withName("FF-55")
                .withStudents(students)
                .build();
        groupService.addGroup(group);
        verify(groupDaoMock).save(groupEntity);
    }
    
    @Test
    void addGroupShouldSaveGroupWithoutIdAndStudentsInDatabasesWhenInputValidGroup() {        
        GroupDto group = new GroupDto();        
        group.setName("FF-55");        
        Group groupEntity = Group.builder()                
                .withName("FF-55")                
                .build();
        groupService.addGroup(group);
        verify(groupDaoMock).save(groupEntity);
    }

    @Test
    void findAllGroupsShouldReturnGroupsWithStudentsWhenTheyExist() {
        assertThat(groupService.findAllGroups()).isEqualTo(createGroupsWithStudents());
    }

    @Test
    void findAllGroupsShouldReturnEmptyListWhenTableEmpty() {
        when(groupDaoMock.findAll()).thenReturn(new ArrayList<>());
        assertThat(groupService.findAllGroups()).isEmpty();
    }

    @Test
    void deleteShouldDeleteGroupFromDatabaseWhenGroupExist() {
        GroupDto group = new GroupDto();
        group.setId(1);
        groupService.delete(group);
        verify(groupDaoMock).deleteById(group.getId());
    }

    @Test
    void deleteShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> groupService.delete(null)).isInstanceOf(IllegalArgumentException.class);
    }

    private List<Group> createGroupsWithStudents() {
        List<Group> groups = new ArrayList<>();
        List<Student> students = new ArrayList<>();
        students.add(CreatorTestEntities.createStudents().get(0));
        students.add(CreatorTestEntities.createStudents().get(1));
        students.add(CreatorTestEntities.createStudents().get(2));
        students.add(CreatorTestEntities.createStudents().get(3));
        Group group = Group.builder()
                .withId(1)
                .withName("AB-22")
                .withStudents(students)
                .build();
        groups.add(group);
        students = new ArrayList<>();
        students.add(CreatorTestEntities.createStudents().get(4));
        students.add(CreatorTestEntities.createStudents().get(5));
        group = Group.builder()
                .withId(2)
                .withName("FR-33")
                .withStudents(students)
                .build();
        groups.add(group);
        return groups;
    }

    private static GroupDaoImpl createGroupDaoMock() {
        GroupDaoImpl groupDaoMock = mock(GroupDaoImpl.class);
        when(groupDaoMock.findByName("FR-33"))
            .thenReturn(Optional.ofNullable(CreatorTestEntities.createGroups().get(1)));
        when(groupDaoMock.findByName("AB-22"))
            .thenReturn(Optional.ofNullable(CreatorTestEntities.createGroups().get(0)));
        when(groupDaoMock.findById(1)).thenReturn(Optional.ofNullable(CreatorTestEntities.createGroups().get(0)));
        when(groupDaoMock.findById(2)).thenReturn(Optional.ofNullable(CreatorTestEntities.createGroups().get(1)));
        when(groupDaoMock.findAll()).thenReturn(CreatorTestEntities.createGroups());
        return groupDaoMock;
    }
    
    private static StudentDaoImpl createStudentDaoMock() {
        StudentDaoImpl studentDaoMock = mock(StudentDaoImpl.class);
        List<Student> students = CreatorTestEntities.createStudents();
        students.remove(0);
        students.remove(0);
        students.remove(0);
        students.remove(0);
        when(studentDaoMock.findAllByGroup("FR-33")).thenReturn(students);
        students = CreatorTestEntities.createStudents();
        students.remove(5);
        students.remove(4);
        when(studentDaoMock.findAllByGroup("AB-22")).thenReturn(students);
        return studentDaoMock;
    }
}
