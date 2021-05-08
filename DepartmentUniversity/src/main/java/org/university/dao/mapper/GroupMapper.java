package org.university.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.university.entity.Group;

public class GroupMapper implements RowMapper<Group> {
    
    @Override
    public Group mapRow(ResultSet resultSet, int rowNum) throws SQLException {        
        return Group.builder()
                .withId(resultSet.getInt("group_id"))
                .withName(resultSet.getString("group_name"))
                .build();
    }
}
