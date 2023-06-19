package org.university.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.dao.SecureTokenDao;
import org.university.entity.SecureToken;
import org.university.entity.TemporaryUser;

class SecureTokenServiceImplTest {

    private static SecureTokenServiceImpl tokenService;
    private static SecureTokenDao tokenDaoMock;

    @BeforeAll
    static void init() {
        tokenDaoMock = mock(SecureTokenDao.class);
        tokenService = new SecureTokenServiceImpl(tokenDaoMock);
    }

    @Test
    void createSecureTokenShouldCreateTokenWithInputUserAndSaveIt() {
        TemporaryUser user = TemporaryUser.builder().withId(1).build();
        tokenService.createSecureToken(user);
        verify(tokenDaoMock).saveAndFlush(any());
        assertThat(tokenService.createSecureToken(user).getUser()).isEqualTo(user);
    }

    @Test
    void findByTokenShouldReturnSecureTokenFromDb() {
        SecureToken secureToken = new SecureToken();
        secureToken.setToken("token");
        when(tokenDaoMock.findByToken("token")).thenReturn(secureToken);
        assertThat(tokenService.findByToken("token")).isEqualTo(secureToken);
    }

    @Test
    void removeTokenShouldDeleteTokenFromDb() {
        SecureToken secureToken = new SecureToken();
        secureToken.setToken("token");
        tokenService.removeToken(secureToken);
        verify(tokenDaoMock).delete(secureToken);
    }

}
