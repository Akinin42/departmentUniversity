package org.university.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.university.dao.GroupDao;
import org.university.dao.StudentDao;
import org.university.dto.GroupDto;
import org.university.dto.StudentDto;
import org.university.entity.Group;
import org.university.entity.Student;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.service.GroupService;
import org.university.service.validator.GroupValidator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Service
@Transactional
public class GroupServiceImpl implements GroupService {

    GroupDao groupDao;
    StudentDao studentDao;
    GroupValidator validator;

    @Override
    public Group createGroup(String name) {
        if (!groupDao.findByName(name).isPresent()) {
            throw new EntityNotExistException();
        }
        return groupDao.findByName(name).get();
    }

    @Override
    public void addGroup(@NonNull GroupDto groupDto) {
        Group group = mapDtoToEntity(groupDto);
        validator.validate(group);
        if (existGroup(group)) {
            throw new EntityAlreadyExistException("groupexist");
        }
        groupDao.save(group);
        log.info("Group with name {} added succesfull!", group.getName());
    }

    @Override
    public List<Group> findAllGroups() {
        return groupDao.findAll();
    }

    @Override
    public void delete(@NonNull GroupDto groupDto) {
        Group group = groupDao.findById(groupDto.getId()).get();
        group.getStudents().clear();
        groupDao.updateStudents(group);
        groupDao.deleteById(group.getId());
    }

    @Override
    public void edit(@NonNull GroupDto groupDto) {
        Group group = mapDtoToEntity(groupDto);
        if (!groupDao.findById(group.getId()).get().getName().equals(group.getName()) && existGroup(group)) {
            throw new EntityAlreadyExistException("groupexist");
        }
        validator.validate(group);
        groupDao.update(group);
        log.info("Group with name {} edited succesfull!", group.getName());
    }

    @Override
    public void addStudentToGroup(StudentDto studentDto) {
        existsStudentAndGroup(studentDto);
        Student student = studentDao.findById(studentDto.getId()).get();
        for (Group group : findAllGroups()) {
            if (group.getStudents().contains(student)) {
                group.removeStudent(student);
                groupDao.updateStudents(group);
            }
        }
        Group group = groupDao.findByName(studentDto.getGroupName()).get();
        group.addStudent(student);
        groupDao.updateStudents(group);
        log.info("Student with id {} added to group {}!", student.getId(), group.getName());
    }

    @Override
    public void deleteStudentFromGroup(@NonNull StudentDto studentDto) {
        existsStudentAndGroup(studentDto);
        Student student = studentDao.findById(studentDto.getId()).get();
        Group group = groupDao.findByName(studentDto.getGroupName()).get();
        group.removeStudent(student);
        groupDao.updateStudents(group);
        log.info("Student with id {} deleted from group {}!", student.getId(), group.getName());
    }

    private boolean existGroup(Group group) {
        return !groupDao.findByName(group.getName()).equals(Optional.empty());
    }

    private void existsStudentAndGroup(StudentDto studentDto) {
        if (studentDao.findById(studentDto.getId()).equals(Optional.empty())
                || groupDao.findByName(studentDto.getGroupName()).equals(Optional.empty())) {
            throw new EntityNotExistException();
        }
    }

    private Group mapDtoToEntity(GroupDto group) {
        return Group.builder()
                .withId(group.getId())
                .withName(group.getName())
                .withStudents(group.getStudents())
                .build();
    }
}
