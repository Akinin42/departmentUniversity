package org.university.dao.impl;

import javax.sql.DataSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.university.dao.GroupDao;
import org.university.dao.mapper.GroupMapper;
import org.university.entity.Group;

@Component
public class GroupDaoImpl extends AbstractCrudImpl<Group> implements GroupDao {

    private static final String SAVE_QUERY = "INSERT INTO groups (group_name) VALUES(?)";
    private static final String FIND_BY_ID_QUERY = "SELECT  * FROM groups WHERE group_id =  ?;";
    private static final String FIND_ALL_QUERY = "SELECT * FROM groups ORDER BY group_id;";
    private static final String FIND_ALL_PAGINATION_QUERY = "SELECT * FROM groups ORDER BY group_id LIMIT ? OFFSET ?;";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM groups WHERE group_id = ?;";

    public GroupDaoImpl(DataSource dataSource) {
        super(dataSource, SAVE_QUERY, FIND_BY_ID_QUERY, FIND_ALL_QUERY, FIND_ALL_PAGINATION_QUERY, DELETE_BY_ID_QUERY);
    }

    @Override
    protected Object[] insert(Group group) {
        Object[] arguments = new Object[1];
        arguments[0] = group.getName();
        return arguments;
    }

    @Override
    protected RowMapper<Group> getMapper() {
        return new GroupMapper();
    }
}
