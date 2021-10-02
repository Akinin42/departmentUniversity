package org.university.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.university.entity.User;

@Repository
public interface UserDao<E extends User> extends JpaRepository<E, Integer> {    
}
