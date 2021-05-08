package org.university.service.impl;

import java.util.List;
import java.util.Optional;
import org.university.dao.CrudDao;
import org.university.entity.User;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.service.UserService;
import org.university.service.validator.Validator;

public abstract class AbstractUserServiceImpl<E> implements UserService<E> {

    private final CrudDao<E, Integer> userDao;
    private final Validator<E> validator;

    protected AbstractUserServiceImpl(CrudDao<E, Integer> userDao, Validator<E> validator) {
        this.userDao = userDao;
        this.validator = validator;
    }

    @Override
    public void register(E user) {
        validator.validate(user);
        if (existsUser(user)) {
            throw new EntityAlreadyExistException();
        }
        userDao.save(mapUserWithPassword(user));
    }

    @Override
    public void delete(E user) {
        validator.validate(user);
        if (existsUser(user)) {
            userDao.deleteById(((User) user).getId());
        }
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
