package org.university.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.university.entity.Teacher;

public class TeacherMapper implements RowMapper<Teacher> {

    @Override
    public Teacher mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Teacher.builder()
                .withId(resultSet.getInt("teacher_id"))
                .withSex(resultSet.getString("teacher_sex"))
                .withName(resultSet.getString("teacher_name"))
                .withEmail(resultSet.getString("teacher_email"))
                .withPhone(resultSet.getString("teacher_phone"))
                .withPassword(resultSet.getString("teacher_password"))
                .withScientificDegree(resultSet.getString("teacher_degree"))
                .build();
    }
}
