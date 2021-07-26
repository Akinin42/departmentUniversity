package org.university.service.impl;

import java.util.List;
import java.util.Optional;
import org.university.dao.CrudDao;
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
public abstract class AbstractUserServiceImpl<E> implements UserService<E> {

    CrudDao<E, Integer> userDao;
    Validator<E> validator;

    @Override
    public void register(@NonNull UserDto userDto) {
        E user = mapDtoToEntity(userDto);
        if (existsUser(user)) {
            throw new EntityAlreadyExistException("userexist");
        }
        validator.validate(user);
        userDao.save(mapUserWithPassword(user));
        log.info("User saved in database!");
    }

    @Override
    public void delete(@NonNull UserDto userDto) {
        User user = (User) mapDtoToEntity(userDto);
        userDao.deleteById(user.getId());
        log.info("User deleted from database!");
    }

    @Override
    public List<E> findNumberOfUsers(int quantity, int number) {
        return userDao.findAll(quantity, number);
    }
    @Override
    public void edit(@NonNull UserDto userDto) {
        E user = mapDtoToEntity(userDto);
        validator.validate(user);
        userDao.update(mapUserWithPassword(user));
        log.info("User edited!");
    }

    protected boolean existsUser(E user) {
        return !userDao.findById(((User) user).getId()).equals(Optional.empty());
    }

    protected abstract E mapUserWithPassword(E user);
    
    protected abstract E mapDtoToEntity(UserDto user);
}
