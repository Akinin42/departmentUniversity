package org.university.dao.impl;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.university.dao.CrudDao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public abstract class AbstractCrudImpl<E> implements CrudDao<E, Integer> {

    protected SessionFactory sessionFactory;
    private Class<E> type;

    @Override
    public void save(E entity) {
        sessionFactory.getCurrentSession().persist(entity);
    }

    @Override
    public Optional<E> findById(Integer id) {
        return Optional.ofNullable(sessionFactory.getCurrentSession().get(type, id));
    }

    @Override
    public List<E> findAll() {
        return sessionFactory.getCurrentSession().createQuery("from " + type.getSimpleName() + " order by id", type)
                .getResultList();
    }

    @Override
    public List<E> findAll(int limit, int offset) {
        return sessionFactory.getCurrentSession().createQuery("from " + type.getSimpleName() + " order by id", type)
                .setFirstResult(offset).setMaxResults(limit).getResultList();
    }

    @Override
    public void deleteById(Integer id) {
        Object persistentInstance = sessionFactory.getCurrentSession().load(type, id);
        if (persistentInstance != null) {
            sessionFactory.getCurrentSession().delete(persistentInstance);
        }
    }

    @Override
    public void update(E entity) {
        sessionFactory.getCurrentSession().merge(entity);
    }
}
