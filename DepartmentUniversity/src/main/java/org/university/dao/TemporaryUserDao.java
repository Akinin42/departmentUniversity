package org.university.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.university.entity.TemporaryUser;

@Repository
public interface TemporaryUserDao extends UserDao<TemporaryUser> {
    
    Optional<TemporaryUser> findByEmail(String email);
    
    List<TemporaryUser> findAllByConfirm(Boolean confirm);
}
