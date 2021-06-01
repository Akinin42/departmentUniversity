package org.university.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.university.entity.Course;

public class CourseMapper implements RowMapper<Course> {

    @Override
    public Course mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Course.builder()
                .id(resultSet.getInt("course_id"))
                .name(resultSet.getString("course_name"))
                .description(resultSet.getString("course_description"))
                .build();
    }
}
