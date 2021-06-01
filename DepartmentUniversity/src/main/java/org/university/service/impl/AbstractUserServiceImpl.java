package org.university.service.impl;

import java.util.List;
import java.util.Optional;
import org.university.dao.CrudDao;
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
    public void register(@NonNull E user) {
        validator.validate(user);
        if (existsUser(user)) {
            throw new EntityAlreadyExistException();
        }
        userDao.save(mapUserWithPassword(user));
        log.info("User saved in database!");
    }

    @Override
    public void delete(@NonNull E user) {
        userDao.deleteById(((User) user).getId());
        log.info("User deleted from database!");
    }

    @Override
    public List<E> findNumberOfUsers(int quantity, int number) {
        return userDao.findAll(quantity, number);
    }

    protected boolean existsUser(E user) {
        return !userDao.findById(((User) user).getId()).equals(Optional.empty());
    }

    protected abstract E mapUserWithPassword(E user);
}
