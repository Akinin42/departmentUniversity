package org.university.dao.impl;

import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private static final String FIND_BY_NAME_QUERY = "SELECT  * FROM groups WHERE group_name =  ?;";
    private static final String UPDATE_QUERY = "UPDATE groups SET group_name = ? WHERE group_id = ?;";

    public GroupDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, SAVE_QUERY, FIND_BY_ID_QUERY, FIND_ALL_QUERY, FIND_ALL_PAGINATION_QUERY,
                DELETE_BY_ID_QUERY, UPDATE_QUERY);
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

    @Override
    public Optional<Group> findByName(String name) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_NAME_QUERY, getMapper(), name));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    protected Object[] updateArgs(Group group) {
        Object[] arguments = new Object[2];
        arguments[0] = group.getName();
        arguments[1] = group.getId();
        return arguments;
    }
}
