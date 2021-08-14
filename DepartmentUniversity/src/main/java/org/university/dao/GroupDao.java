package org.university.dao;

import java.util.Optional;

import org.university.entity.Group;

public interface GroupDao extends CrudDao<Group, Integer> {
    
    Optional<Group> findByName(String name);
    
    void updateStudents(Group group);
}
