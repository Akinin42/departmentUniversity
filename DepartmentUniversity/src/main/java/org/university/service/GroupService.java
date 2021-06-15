package org.university.service;

import java.util.List;
import org.university.dto.GroupDto;
import org.university.entity.Group;

public interface GroupService {

    Group createGroup(String name);

    void addGroup(GroupDto groupDto);
    
    List<Group> findAllGroups();
    
    void delete(GroupDto groupDto);
}
