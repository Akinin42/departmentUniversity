package org.university.dao.impl;

import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.university.dao.CourseDao;
import org.university.dao.mapper.CourseMapper;
import org.university.entity.Course;

@Component
public class CourseDaoImpl extends AbstractCrudImpl<Course> implements CourseDao {

    private static final String SAVE_QUERY = "INSERT INTO courses (course_name, course_description) VALUES(?,?)";
    private static final String FIND_BY_ID_QUERY = "SELECT  * FROM courses WHERE course_id =  ?;";
    private static final String FIND_ALL_QUERY = "SELECT * FROM courses ORDER BY course_id;";
    private static final String FIND_ALL_PAGINATION_QUERY = "SELECT * FROM courses ORDER BY course_id LIMIT ? OFFSET ?;";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM courses WHERE course_id = ?;";
    private static final String FIND_BY_NAME_QUERY = "SELECT  * FROM courses WHERE course_name =  ?;";

    public CourseDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, SAVE_QUERY, FIND_BY_ID_QUERY, FIND_ALL_QUERY, FIND_ALL_PAGINATION_QUERY,
                DELETE_BY_ID_QUERY);
    }

    @Override
    protected Object[] insert(Course course) {
        Object[] arguments = new Object[2];
        arguments[0] = course.getName();
        arguments[1] = course.getDescription();
        return arguments;
    }

    @Override
    protected RowMapper<Course> getMapper() {
        return new CourseMapper();
    }

    @Override
    public Optional<Course> findByName(String name) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_NAME_QUERY, getMapper(), name));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
