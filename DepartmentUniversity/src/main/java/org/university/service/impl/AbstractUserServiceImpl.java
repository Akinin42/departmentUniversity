package org.university.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.university.dao.UserDao;
import org.university.dto.UserDto;
import org.university.email.AbstractEmailContext;
import org.university.entity.SecureToken;
import org.university.entity.User;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.InvalidTokenException;
import org.university.service.EmailService;
import org.university.service.SecureTokenService;
import org.university.service.UserService;
import org.university.service.validator.Validator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Transactional
public abstract class AbstractUserServiceImpl<E extends User> implements UserService<E> {

    UserDao<E> userDao;
    Validator<User> validator;
    EmailService<User> emailService;
    SecureTokenService secureTokenService;

    @Override
    public void register(@NonNull UserDto userDto) {        
        E user = mapDtoToEntity(userDto);
        if (userDto.getId() != null && existsUser(user)) {
            throw new EntityAlreadyExistException("userexist");
        }
        if (userDto.getId() == null) {
            validator.validate(user);
            user = mapUserWithPassword(user);
            userDao.save(user);
            sendRegistrationConfirmationEmail(user, userDto.getLocale());
        } else {
            userDao.save(user);
        }
        log.info("User saved in database!");
    }

    @Override
    public void delete(@NonNull UserDto userDto) {
        userDao.deleteById(userDto.getId());
        log.info("User deleted from database!");
    }

    @Override
    public List<E> findNumberOfUsers(int quantity, int pagesNumber) {
        Pageable limit = PageRequest.of(pagesNumber, quantity);
        return userDao.findAll(limit).toList();
    }

    @Override
    public void edit(@NonNull UserDto userDto) {
        E user = mapDtoToEntity(userDto);
        validator.validate(user);
        userDao.save(mapUserWithPassword(user));
        log.info("User edited!");
    }

    protected boolean existsUser(E user) {
        return userDao.existsById(user.getId());
    }

    protected abstract E mapUserWithPassword(E user);

    protected abstract E mapDtoToEntity(UserDto user);

    @Override
    public void sendRegistrationConfirmationEmail(E user, Locale locale) {
        SecureToken secureToken = secureTokenService.createSecureToken(user);
        AbstractEmailContext<User> emailContext = emailService.createEmailContext(user, locale, secureToken);
        try {
            emailService.sendMail(emailContext);
        } catch (MessagingException e) {
            log.error("Send email is fail!");
        }
    }

    @Override
    public boolean verifyUser(String token) throws InvalidTokenException {
        SecureToken secureToken = secureTokenService.findByToken(token);
        if (checkSecureToken(secureToken, token)) {
            throw new InvalidTokenException("Token is not valid");
        }
        E user = userDao.getById(secureToken.getUser().getId());
        user.setEnabled(true);
        userDao.save(user);
        secureTokenService.removeToken(secureToken);
        return true;
    }

    private boolean checkSecureToken(SecureToken secureToken, String inputToken) {
        return Objects.isNull(secureToken) || !StringUtils.equals(inputToken, secureToken.getToken())
                || secureToken.isExpired();
    }
}
