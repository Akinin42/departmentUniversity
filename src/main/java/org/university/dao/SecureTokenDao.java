package org.university.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.university.entity.SecureToken;

@Repository
public interface SecureTokenDao extends JpaRepository<SecureToken, Integer> {

    SecureToken findByToken(final String token);

    void removeByToken(String token);
}
