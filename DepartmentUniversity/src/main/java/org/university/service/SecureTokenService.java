package org.university.service;

import org.university.entity.SecureToken;
import org.university.entity.User;

public interface SecureTokenService {
    
    SecureToken createSecureToken(User user);

    void saveSecureToken(final SecureToken token);

    SecureToken findByToken(final String token);

    void removeToken(final SecureToken token);
}
