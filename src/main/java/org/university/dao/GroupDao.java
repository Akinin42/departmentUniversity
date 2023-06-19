package org.university.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.university.entity.Group;

@Repository
public interface GroupDao extends JpaRepository<Group, Integer> {

    Optional<Group> findByName(String name);
}
