package org.university.dao.impl;

import javax.sql.DataSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.university.dao.ClassroomDao;
import org.university.dao.mapper.ClassroomMapper;
import org.university.entity.Classroom;

@Component
public class ClassroomDaoImpl extends AbstractCrudImpl<Classroom> implements ClassroomDao {

    private static final String SAVE_QUERY = "INSERT INTO classrooms (classroom_number, classroom_address, classroom_capacity) VALUES(?,?,?)";
    private static final String FIND_BY_ID_QUERY = "SELECT  * FROM classrooms WHERE classroom_id =  ?;";
    private static final String FIND_ALL_QUERY = "SELECT * FROM classrooms ORDER BY classroom_id;";
    private static final String FIND_ALL_PAGINATION_QUERY = "SELECT * FROM classrooms ORDER BY classroom_id LIMIT ? OFFSET ?;";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM classrooms WHERE classroom_id = ?;";

    public ClassroomDaoImpl(DataSource dataSource) {
        super(dataSource, SAVE_QUERY, FIND_BY_ID_QUERY, FIND_ALL_QUERY, FIND_ALL_PAGINATION_QUERY, DELETE_BY_ID_QUERY);
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
}
