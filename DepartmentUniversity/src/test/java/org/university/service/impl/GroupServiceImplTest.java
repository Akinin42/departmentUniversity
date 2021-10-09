package org.university.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.dao.GroupDao;
import org.university.dao.StudentDao;
import org.university.dto.GroupDto;
import org.university.dto.StudentDto;
import org.university.entity.Group;
import org.university.entity.Student;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.utils.CreatorTestEntities;

class GroupServiceImplTest {

    private static GroupServiceImpl groupService;
    private static GroupDao groupDaoMock;
    private static StudentDao studentDaoMock;

    @BeforeAll
    static void init() {
        groupDaoMock = createGroupDaoMock();
        studentDaoMock = mock(StudentDao.class);
        groupService = new GroupServiceImpl(groupDaoMock, studentDaoMock);
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
    void addGroupShouldThrowEntityAlreadyExistExceptionWhenInputGroupExistInDatabase() {
        GroupDto group = new GroupDto();
        group.setId(1);
        group.setName("AB-22");
        assertThatThrownBy(() -> groupService.addGroup(group)).isInstanceOf(EntityAlreadyExistException.class);
    }

    @Test
    void addGroupShouldSaveGroupAndStudentsInDatabasesWhenInputValidGroup() {
        Set<Student> students = new HashSet<>();
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
        List<Group> expected = createGroupsWithStudents();
        List<Group> actual = groupService.findAllGroups();
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
        Set<Student> students = new HashSet<>();
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
        students = new HashSet<>();
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
    
    @Test
    void editShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> groupService.edit(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void editShouldThrowEntityAlreadyExistExceptionWhenInputGroupNameExistInDatabase() {
        GroupDto group = new GroupDto();
        group.setId(2);
        group.setName("AB-22");
        assertThatThrownBy(() -> groupService.edit(group)).isInstanceOf(EntityAlreadyExistException.class);
    }
    
    @Test
    void editShouldUpdateGroupWhenInputValidGroup() {
        GroupDto group = new GroupDto();        
        group.setId(1);
        group.setName("NN-55");
        Group groupEntity = Group.builder()
                .withId(1)
                .withName("NN-55")
                .withStudents(null)
                .build();
        groupService.edit(group);
        verify(groupDaoMock).save(groupEntity);
    }
    
    @Test
    void editShouldUpdateGroupWhenInputNotChange() {
        GroupDto group = new GroupDto();        
        group.setId(1);
        group.setName("AB-22");
        Set<Student> students = new HashSet<>();
        students.add(CreatorTestEntities.createStudents().get(0));
        students.add(CreatorTestEntities.createStudents().get(1));
        students.add(CreatorTestEntities.createStudents().get(2));
        students.add(CreatorTestEntities.createStudents().get(3));
        group.setStudents(students);
        Group groupEntity = CreatorTestEntities.createGroups().get(0);
        groupService.edit(group);
        verify(groupDaoMock).save(groupEntity);
    }
    
    @Test
    void addStudentToGroupShouldInsertStudentToGroupAndDeleteFromOld() {
        GroupDao groupDaoMock = createGroupDaoMock();
        StudentDao studentDaoMock = mock(StudentDao.class);
        GroupServiceImpl groupService = new GroupServiceImpl(groupDaoMock, studentDaoMock);
        StudentDto studentDto = new StudentDto();
        studentDto.setId(6);
        studentDto.setGroupName("AB-22");
        when(studentDaoMock.findById(6)).thenReturn(Optional.ofNullable(CreatorTestEntities.createStudents().get(5)));
        Group group = CreatorTestEntities.createGroups().get(0);
        group.addStudent(CreatorTestEntities.createStudents().get(5));
        groupService.addStudentToGroup(studentDto);
        verify(groupDaoMock).save(group);
    }
    
    @Test
    void addStudentToGroupShouldThrowEntityNotExistExceptionWhenStudentNotExist() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(42);
        studentDto.setGroupName("AB-22");
        when(studentDaoMock.findById(42)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> groupService.addStudentToGroup(studentDto)).isInstanceOf(EntityNotExistException.class);
    }
    
    @Test
    void addStudentToGroupShouldThrowEntityNotExistExceptionWhenGroupNotExist() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(6);
        studentDto.setGroupName("notexistedgroup");
        when(studentDaoMock.findById(6)).thenReturn(Optional.ofNullable(CreatorTestEntities.createStudents().get(5)));
        assertThatThrownBy(() -> groupService.addStudentToGroup(studentDto)).isInstanceOf(EntityNotExistException.class);
    }
    
    @Test
    void deleteStudentFromGroupShouldThrowEntityNotExistExceptionWhenStudentNotExist() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(42);
        studentDto.setGroupName("AB-22");
        when(studentDaoMock.findById(42)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> groupService.deleteStudentFromGroup(studentDto)).isInstanceOf(EntityNotExistException.class);
    }
    
    @Test
    void deleteStudentFromGroupThrowEntityNotExistExceptionWhenGroupNotExist() {
        StudentDto studentDto = new StudentDto();
        studentDto.setId(6);
        studentDto.setGroupName("notexistedgroup");
        when(studentDaoMock.findById(6)).thenReturn(Optional.ofNullable(CreatorTestEntities.createStudents().get(5)));
        assertThatThrownBy(() -> groupService.deleteStudentFromGroup(studentDto)).isInstanceOf(EntityNotExistException.class);
    }
    
    @Test
    void deleteStudentFromGroupShouldDeleteStudentFromGroup() {
        GroupDao groupDaoMock = createGroupDaoMock();
        StudentDao studentDaoMock = mock(StudentDao.class);
        GroupServiceImpl groupService = new GroupServiceImpl(groupDaoMock, studentDaoMock);
        StudentDto studentDto = new StudentDto();
        studentDto.setId(1);
        studentDto.setGroupName("AB-22");
        when(studentDaoMock.findById(1)).thenReturn(Optional.ofNullable(CreatorTestEntities.createStudents().get(0)));
        Group group = CreatorTestEntities.createGroups().get(0);
        group.removeStudent(CreatorTestEntities.createStudents().get(0));
        groupService.deleteStudentFromGroup(studentDto);
        verify(groupDaoMock).save(group);
    }
    
    @Test
    void deleteStudentFromGroupShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> groupService.deleteStudentFromGroup(null)).isInstanceOf(IllegalArgumentException.class);
    }

    private static GroupDao createGroupDaoMock() {
        GroupDao groupDaoMock = mock(GroupDao.class);
        when(groupDaoMock.findByName("FR-33"))
            .thenReturn(Optional.ofNullable(CreatorTestEntities.createGroups().get(1)));
        when(groupDaoMock.findByName("AB-22"))
            .thenReturn(Optional.ofNullable(CreatorTestEntities.createGroups().get(0)));
        when(groupDaoMock.findById(1)).thenReturn(Optional.ofNullable(CreatorTestEntities.createGroups().get(0)));
        when(groupDaoMock.findById(2)).thenReturn(Optional.ofNullable(CreatorTestEntities.createGroups().get(1)));
        when(groupDaoMock.findAll()).thenReturn(CreatorTestEntities.createGroups());
        when(groupDaoMock.findByName("notexistedgroup")).thenReturn(Optional.empty());
        return groupDaoMock;
    }
}
