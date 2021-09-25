package org.university.dao.impl;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;
import org.university.dao.GroupDao;
import org.university.entity.Group;

@Repository
public class GroupDaoImpl extends AbstractCrudImpl<Group> implements GroupDao {

    public GroupDaoImpl(EntityManager entityManager) {
        super(entityManager, Group.class);
    }

    @Override
    public Optional<Group> findByName(String name) {
        try {
            Group group = (Group) entityManager.createQuery("from Group where name = :name")
                    .setParameter("name", name)
                    .getSingleResult();
            return Optional.ofNullable(group);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void updateStudents(Group group) {
        entityManager.merge(group);
        entityManager.flush();
    }
}
