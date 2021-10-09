package org.university.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.university.entity.Role;

@Repository
public interface RoleDao extends CrudRepository<Role, Integer> {
    
    Optional<Role> findByName(String name);
}
