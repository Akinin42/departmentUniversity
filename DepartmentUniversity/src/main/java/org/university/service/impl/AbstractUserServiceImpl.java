package org.university.service.impl;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.university.dao.UserDao;
import org.university.dto.UserDto;
import org.university.entity.User;
import org.university.exceptions.EntityAlreadyExistException;
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

    @Override    
    public void register(@NonNull UserDto userDto) {
        E user = mapDtoToEntity(userDto);
        if (userDto.getId() != null && existsUser(user) ) {
            throw new EntityAlreadyExistException("userexist");
        }
        validator.validate(user);
        userDao.save(mapUserWithPassword(user));
        log.info("User saved in database!");
    }    

    @Override    
    public void delete(@NonNull UserDto userDto) {
        User user = mapDtoToEntity(userDto);
        userDao.deleteById(user.getId());
        log.info("User deleted from database!");
    }

    @Override    
    public List<E> findNumberOfUsers(int quantity, int pagesNumber) {
        Pageable limit = PageRequest.of(pagesNumber,quantity);        
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
}
