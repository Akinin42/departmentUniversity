package org.university.dao.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.university.dao.StudentDao;
import org.university.dao.mapper.StudentMapper;
import org.university.entity.Course;
import org.university.entity.Group;
import org.university.entity.Student;

@Component
public class StudentDaoImpl extends AbstractCrudImpl<Student> implements StudentDao {

    private static final String SAVE_QUERY = "INSERT INTO students (student_sex, student_name, student_email, student_phone, student_password)"
            + " VALUES(?,?,?,?,?);";
    private static final String FIND_BY_ID_QUERY = "SELECT  * FROM students WHERE student_id =  ?;";
    private static final String FIND_ALL_QUERY = "SELECT * FROM students ORDER BY student_id;";
    private static final String FIND_ALL_PAGINATION_QUERY = "SELECT * FROM students ORDER BY student_id LIMIT ? OFFSET ?;";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM students WHERE student_id = ?;";
    private static final String FIND_ALL_BY_COURSE_QUERY = "SELECT * FROM students "
            + "INNER JOIN students_to_courses ON students_to_courses.student_id = students.student_id "
            + "INNER JOIN courses ON students_to_courses.course_id = courses.course_id WHERE course_name = ?;";
    private static final String FIND_ALL_BY_GROUP_QUERY = "SELECT * FROM students "
            + "INNER JOIN students_to_groups ON students_to_groups.student_id = students.student_id "
            + "INNER JOIN groups ON students_to_groups.group_id = groups.group_id WHERE group_name = ?;";
    private static final String SAVE_STUDENT_TO_COURSES_QUERY = "INSERT INTO students_to_courses (student_id, course_id) VALUES(?,?);";
    private static final String SAVE_STUDENT_TO_GROUPS_QUERY = "INSERT INTO students_to_groups (student_id, group_id) VALUES(?,?);";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT  * FROM students WHERE student_email =  ?;";
    private static final String DELETE_STUDENT_FROM_GROUP_QUERY = "DELETE FROM students_to_groups "
            + "WHERE student_id = ? AND group_id = ?";
    private static final String DELETE_STUDENT_FROM_COURSE_QUERY = "DELETE FROM students_to_courses "
            + "WHERE student_id = ? AND course_id = ?";
    private static final String UPDATE_QUERY = "UPDATE students "
            + "SET student_sex = ?, student_name = ?, student_email = ?, student_phone = ?, student_password = ? WHERE student_id = ?;";

    public StudentDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, SAVE_QUERY, FIND_BY_ID_QUERY, FIND_ALL_QUERY, FIND_ALL_PAGINATION_QUERY,
                DELETE_BY_ID_QUERY, UPDATE_QUERY);
    }

    @Override
    protected Object[] insert(Student student) {
        Object[] arguments = new Object[5];
        arguments[0] = student.getSex();
        arguments[1] = student.getName();
        arguments[2] = student.getEmail();
        arguments[3] = student.getPhone();
        arguments[4] = student.getPassword();
        return arguments;
    }

    @Override
    protected RowMapper<Student> getMapper() {
        return new StudentMapper();
    }

    @Override
    public List<Student> findAllByCourse(String courseName) {
        return jdbcTemplate.query(FIND_ALL_BY_COURSE_QUERY, getMapper(), courseName);
    }

    @Override
    public List<Student> findAllByGroup(String groupName) {
        return jdbcTemplate.query(FIND_ALL_BY_GROUP_QUERY, getMapper(), groupName);
    }

    @Override
    public void insertStudentToCourses(Student student, List<Course> courses) {
        for (Course course : courses) {
            jdbcTemplate.update(SAVE_STUDENT_TO_COURSES_QUERY, student.getId(), course.getId());
        }
    }

    @Override
    public void insertStudentToGroup(Student student, Group group) {
        jdbcTemplate.update(SAVE_STUDENT_TO_GROUPS_QUERY, student.getId(), group.getId());
    }

    @Override
    public Optional<Student> findByEmail(String email) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_EMAIL_QUERY, getMapper(), email));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteStudentFromGroup(int studentId, int groupId) {
        jdbcTemplate.update(DELETE_STUDENT_FROM_GROUP_QUERY, studentId, groupId);
    }

    @Override
    public void deleteStudentFromCourse(int studentId, int courseId) {
        jdbcTemplate.update(DELETE_STUDENT_FROM_COURSE_QUERY, studentId, courseId);
    }

    @Override
    protected Object[] updateArgs(Student student) {
        Object[] arguments = new Object[6];
        arguments[0] = student.getSex();
        arguments[1] = student.getName();
        arguments[2] = student.getEmail();
        arguments[3] = student.getPhone();
        arguments[4] = student.getPassword();
        arguments[5] = student.getId();
        return arguments;
    }
}
