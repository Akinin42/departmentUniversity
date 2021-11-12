package org.university.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.university.dao.RoleDao;
import org.university.dao.TemporaryUserDao;
import org.university.dto.UserDto;
import org.university.entity.TemporaryUser;
import org.university.entity.User;
import org.university.exceptions.EntityNotExistException;
import org.university.service.EmailService;
import org.university.service.SecureTokenService;
import org.university.service.TemporaryUserService;
import org.university.service.validator.Validator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Service
@Transactional
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TemporaryUserServiceImpl extends AbstractUserServiceImpl<TemporaryUser> implements TemporaryUserService {
    
    private static final String USER = "USER";
    
    TemporaryUserDao temporaryUserDao;
    RoleDao roleDao;    
    PasswordEncoder encoder;
    
    public TemporaryUserServiceImpl(TemporaryUserDao temporaryUserDao, EmailService<User> emailService,
            SecureTokenService secureTokenService,
            Validator<User> validator, PasswordEncoder encoder, RoleDao roleDao) {
        super(temporaryUserDao, validator, emailService, secureTokenService);
        this.temporaryUserDao = temporaryUserDao;       
        this.encoder = encoder;
        this.roleDao = roleDao;
    }

    @Override
    public List<TemporaryUser> findAllConfirmUser() {
        return temporaryUserDao.findAllByConfirm(true);
    }

    @Override
    protected TemporaryUser mapUserWithPassword(TemporaryUser user) {
        return TemporaryUser.builder()
                .withId(user.getId())
                .withSex(user.getSex())
                .withName(user.getName())
                .withEmail(user.getEmail())
                .withPhone(user.getPhone())
                .withPassword(encoder.encode(user.getPassword()))
                .withPhoto(user.getPhoto())                
                .withRole(roleDao.findByName(USER).get())
                .withDesiredRole(user.getDesiredRole())
                .withDesiredDegree(user.getDesiredDegree())
                .withConfirm(user.getConfirm())
                .withConfirmDescription(user.getConfirmDescription())
                .build();
    }

    @Override
    protected TemporaryUser mapDtoToEntity(UserDto user) {
        return TemporaryUser.builder()
                .withId(user.getId())
                .withSex(user.getSex())
                .withName(user.getName())
                .withEmail(user.getEmail())
                .withPhone(user.getPhone())
                .withPassword(user.getPassword())
                .withPhoto(user.getPhotoName())
                .withRole(roleDao.findByName(USER).get())
                .withDesiredRole(roleDao.findByName(user.getDesiredRole()).get())
                .withDesiredDegree(user.getDesiredDegree())
                .withConfirm(user.getConfirm())
                .withConfirmDescription(user.getConfirmDescription())
                .build();
    }    
    
    public UserDto mapEntityToDto(TemporaryUser user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setSex(user.getSex());
        userDto.setPhone(user.getPhone());
        userDto.setPhotoName(user.getPhoto());
        userDto.setPassword(user.getPassword());
        userDto.setDesiredRole(user.getDesiredRole().getName());
        userDto.setDesiredDegree(user.getDesiredDegree());
        userDto.setConfirm(user.getConfirm());
        userDto.setConfirmDescription(user.getConfirmDescription());
        return userDto;
    }

    @Override
    public TemporaryUser getByEmail(String email) {        
        return temporaryUserDao.findByEmail(email).orElseThrow(() -> new EntityNotExistException("User with " + email+ " not found"));
    }

    @Override
    public void addConfirmDescription(UserDto inputUser) {
        TemporaryUser temporaryUser = getByEmail(inputUser.getEmail());
        UserDto user = mapEntityToDto(temporaryUser);        
        user.setConfirm(false);
        user.setConfirmDescription(inputUser.getConfirmDescription());
        temporaryUserDao.save(mapDtoToEntity(user));
    }
}
