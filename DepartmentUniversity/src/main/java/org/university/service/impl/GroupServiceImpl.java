package org.university.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.university.dao.GroupDao;
import org.university.dao.StudentDao;
import org.university.entity.Group;
import org.university.entity.Student;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.service.GroupService;
import org.university.service.validator.GroupValidator;

@Component
public class GroupServiceImpl implements GroupService {

    private final GroupDao groupDao;
    private final StudentDao studentDao;
    private final GroupValidator validator;

    public GroupServiceImpl(GroupDao groupDao, StudentDao studentDao, GroupValidator validator) {
        this.groupDao = groupDao;
        this.studentDao = studentDao;
        this.validator = validator;
    }

    @Override
    public Group createGroup(String name) {
        if (!groupDao.findByName(name).isPresent()) {
            throw new EntityNotExistException();
        }
        Group group = groupDao.findByName(name).get();
        return Group.builder()
                .id(group.getId())
                .name(group.getName())
                .students(studentDao.findAllByGroup(group.getName()))
                .build();
    }

    @Override
    public void addGroup(Group group) {
        validator.validate(group);
        if (existGroup(group)) {
            throw new EntityAlreadyExistException();
        }
        groupDao.save(group);
        for (Student student : group.getStudents()) {
            studentDao.insertStudentToGroup(student, group);
        }
    }

    @Override
    public List<Group> findAllGroups() {
        List<Group> groups = new ArrayList<>();
        for (Group group : groupDao.findAll()) {
            groups.add(createGroup(group.getName()));
        }
        return groups;
    }

    @Override
    public void delete(Group group) {
        validator.validate(group);
        groupDao.deleteById(group.getId());
    }

    private boolean existGroup(Group group) {
        return !groupDao.findById(group.getId()).equals(Optional.empty());
    }
}
