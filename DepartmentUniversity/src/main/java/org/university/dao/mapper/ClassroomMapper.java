package org.university.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.university.entity.Classroom;

public class ClassroomMapper implements RowMapper<Classroom> {

    @Override
    public Classroom mapRow(ResultSet resultSet, int rowNum) throws SQLException {        
        return Classroom.builder()
                .withId(resultSet.getInt("classroom_id"))
                .withNumber(resultSet.getInt("classroom_number"))
                .withAddress(resultSet.getString("classroom_address"))
                .withCapacity(resultSet.getInt("classroom_capacity"))
                .build();
    }
}
