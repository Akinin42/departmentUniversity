package org.university.dao.impl;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.university.dao.CrudDao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public abstract class AbstractCrudImpl<E> implements CrudDao<E, Integer> {

    @PersistenceContext
    protected EntityManager entityManager;    
   
    private Class<E> type;

    @Override
    public void save(E entity) {
        entityManager.persist(entity);
    }

    @Override
    public Optional<E> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(type, id));
    }

    @Override
    public List<E> findAll() {        
        return entityManager.createQuery("from " + type.getSimpleName() + " order by id", type)
                .getResultList();
    }

    @Override
    public List<E> findAll(int limit, int offset) {
        return entityManager.createQuery("from " + type.getSimpleName() + " order by id", type)
                .setFirstResult(offset).setMaxResults(limit).getResultList();
    }

    @Override
    public void deleteById(Integer id) {
        Object persistentInstance = entityManager.find(type, id);
        if (persistentInstance != null) {            
            entityManager.remove(persistentInstance);
        }
    }

    @Override
    public void update(E entity) {
        entityManager.merge(entity);
    }
}
