package org.university.dao.impl;

import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.university.dao.GroupDao;
import org.university.entity.Group;

@Repository
public class GroupDaoImpl extends AbstractCrudImpl<Group> implements GroupDao {

    public GroupDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory, Group.class);
    }

    @Override
    public Optional<Group> findByName(String name) {
        Group group = (Group) sessionFactory.getCurrentSession().createQuery("from Group where name = :name")
                .setParameter("name", name).uniqueResult();
        return Optional.ofNullable(group);
    }

    @Override    
    public void updateStudents(Group group) {
        sessionFactory.getCurrentSession().merge(group);
        sessionFactory.getCurrentSession().flush();
    }
}
