package org.university.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.university.entity.Student;

public class StudentMapper implements RowMapper<Student> {

    @Override
    public Student mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Student.builder()
                .withId(resultSet.getInt("student_id"))
                .withSex(resultSet.getString("student_sex"))
                .withName(resultSet.getString("student_name"))
                .withEmail(resultSet.getString("student_email"))
                .withPhone(resultSet.getString("student_phone"))
                .withPassword(resultSet.getString("student_password"))
                .withPhoto(resultSet.getString("student_photo"))
                .build();
    }
}
