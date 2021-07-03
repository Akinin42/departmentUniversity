package org.university.dao.impl;

import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.university.dao.ClassroomDao;
import org.university.dao.mapper.ClassroomMapper;
import org.university.entity.Classroom;

@Component
public class ClassroomDaoImpl extends AbstractCrudImpl<Classroom> implements ClassroomDao {

    private static final String SAVE_QUERY = "INSERT INTO classrooms (classroom_number, classroom_address, classroom_capacity) VALUES(?,?,?)";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM classrooms WHERE classroom_id = ?;";
    private static final String FIND_ALL_QUERY = "SELECT * FROM classrooms ORDER BY classroom_id;";
    private static final String FIND_ALL_PAGINATION_QUERY = "SELECT * FROM classrooms ORDER BY classroom_id LIMIT ? OFFSET ?;";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM classrooms WHERE classroom_id = ?;";
    private static final String FIND_BY_NUMBER_QUERY = "SELECT * FROM classrooms WHERE classroom_number = ?;";
    private static final String UPDATE_QUERY = "UPDATE classrooms "
            + "SET classroom_number = ?, classroom_address = ?, classroom_capacity = ? WHERE classroom_id = ?;";

    public ClassroomDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, SAVE_QUERY, FIND_BY_ID_QUERY, FIND_ALL_QUERY, FIND_ALL_PAGINATION_QUERY,
                DELETE_BY_ID_QUERY, UPDATE_QUERY);
    }

    @Override
    protected Object[] insert(Classroom classroom) {
        Object[] arguments = new Object[3];
        arguments[0] = classroom.getNumber();
        arguments[1] = classroom.getAddress();
        arguments[2] = classroom.getCapacity();
        return arguments;
    }

    @Override
    protected RowMapper<Classroom> getMapper() {
        return new ClassroomMapper();
    }

    @Override
    public Optional<Classroom> findByNumber(int number) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_NUMBER_QUERY, getMapper(), number));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    protected Object[] updateArgs(Classroom classroom) {
        Object[] arguments = new Object[4];
        arguments[0] = classroom.getNumber();
        arguments[1] = classroom.getAddress();
        arguments[2] = classroom.getCapacity();
        arguments[3] = classroom.getId();
        return arguments;
    }
}
