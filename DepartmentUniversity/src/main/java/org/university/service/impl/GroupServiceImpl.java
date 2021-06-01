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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class GroupServiceImpl implements GroupService {

    GroupDao groupDao;
    StudentDao studentDao;
    GroupValidator validator;

    @Override
    public Group createGroup(String name) {
        if (!groupDao.findByName(name).isPresent()) {
            throw new EntityNotExistException();
        }
        Group group = groupDao.findByName(name).get();
        return Group.builder()
                .withId(group.getId())
                .withName(group.getName())
                .withStudents(studentDao.findAllByGroup(group.getName()))
                .build();
    }

    @Override
    public void addGroup(@NonNull Group group) {
        validator.validate(group);
        if (existGroup(group)) {
            throw new EntityAlreadyExistException();
        }
        groupDao.save(group);
        for (Student student : group.getStudents()) {
            studentDao.insertStudentToGroup(student, group);
        }
        log.info("Group with name {} added succesfull!", group.getName());
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
    public void delete(@NonNull Group group) {
        groupDao.deleteById(group.getId());
    }

    private boolean existGroup(Group group) {
        return !groupDao.findById(group.getId()).equals(Optional.empty());
    }
}
