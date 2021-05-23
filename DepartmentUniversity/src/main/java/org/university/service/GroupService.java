package org.university.service;

import java.util.List;

import org.university.entity.Group;

public interface GroupService {

    Group createGroup(String name);

    void addGroup(Group group);
    
    List<Group> findAllGroups();
    
    void delete(Group group);
}
