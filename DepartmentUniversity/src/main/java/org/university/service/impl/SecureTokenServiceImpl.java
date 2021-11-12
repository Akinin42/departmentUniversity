package org.university.service.impl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.university.dao.SecureTokenDao;
import org.university.entity.SecureToken;
import org.university.entity.TemporaryUser;
import org.university.entity.User;
import org.university.service.SecureTokenService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SecureTokenServiceImpl implements SecureTokenService {

    private static final BytesKeyGenerator DEFAULT_TOKEN_GENERATOR = KeyGenerators.secureRandom(15);
    private static final Long TOKEN_VALIDITY = 8L;

    SecureTokenDao secureTokenDao;

    @Override
    public SecureToken createSecureToken(User user) {
        String tokenValue = new String(Base64.encodeBase64URLSafe(DEFAULT_TOKEN_GENERATOR.generateKey()), StandardCharsets.US_ASCII);
        SecureToken secureToken = new SecureToken();
        secureToken.setToken(tokenValue);
        secureToken.setExpireAt(LocalDateTime.now().plusHours(TOKEN_VALIDITY));
        secureToken.setUser((TemporaryUser) user);
        this.saveSecureToken(secureToken);
        return secureToken;
    }

    @Override
    public void saveSecureToken(SecureToken token) {
        secureTokenDao.saveAndFlush(token);
    }

    @Override
    public SecureToken findByToken(String token) {
        return secureTokenDao.findByToken(token);
    }

    @Override
    public void removeToken(SecureToken token) {
        secureTokenDao.delete(token);
    }
}
