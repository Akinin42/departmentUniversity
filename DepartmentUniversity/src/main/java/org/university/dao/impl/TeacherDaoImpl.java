package org.university.dao.impl;

import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.university.dao.TeacherDao;
import org.university.dao.mapper.TeacherMapper;
import org.university.entity.Teacher;

@Component
public class TeacherDaoImpl extends AbstractCrudImpl<Teacher> implements TeacherDao{

    private static final String SAVE_QUERY = "INSERT INTO teachers "
            + "(teacher_sex, teacher_name, teacher_email, teacher_phone, teacher_password, teacher_degree)"
            + " VALUES(?,?,?,?,?,?);";
    private static final String FIND_BY_ID_QUERY = "SELECT  * FROM teachers WHERE teacher_id =  ?;";
    private static final String FIND_ALL_QUERY = "SELECT * FROM teachers ORDER BY teacher_id;";
    private static final String FIND_ALL_PAGINATION_QUERY = "SELECT * FROM teachers ORDER BY teacher_id LIMIT ? OFFSET ?;";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM teachers WHERE teacher_id = ?;";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT  * FROM teachers WHERE teacher_email =  ?;";

    public TeacherDaoImpl(DataSource dataSource) {
        super(dataSource, SAVE_QUERY, FIND_BY_ID_QUERY, FIND_ALL_QUERY, FIND_ALL_PAGINATION_QUERY, DELETE_BY_ID_QUERY);
    }

    @Override
    protected Object[] insert(Teacher teacher) {
        Object[] arguments = new Object[6];
        arguments[0] = teacher.getSex();
        arguments[1] = teacher.getName();
        arguments[2] = teacher.getEmail();
        arguments[3] = teacher.getPhone();
        arguments[4] = teacher.getPassword();
        arguments[5] = teacher.getScientificDegree();
        return arguments;
    }

    @Override
    protected RowMapper<Teacher> getMapper() {
        return new TeacherMapper();
    }

    @Override
    public Optional<Teacher> findByEmail(String email) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_EMAIL_QUERY, getMapper(), email));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
