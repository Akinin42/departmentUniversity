package org.university.dao.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.university.dao.LessonDao;
import org.university.dao.mapper.LessonMapper;
import org.university.entity.Lesson;

@Component
public class LessonDaoImpl extends AbstractCrudImpl<Lesson> implements LessonDao {

    private static final String SAVE_QUERY = "INSERT INTO lessons (lesson_start, lesson_end, lesson_online, "
            + "lesson_link, lesson_classroom, lesson_course, lesson_teacher, lesson_group) VALUES(?,?,?,?,?,?,?,?)";
    private static final String SELECT_INNER_JOIN_OTHERS_TABLES = "SELECT  * FROM lessons "
            + "INNER JOIN classrooms ON lessons.lesson_classroom = classrooms.classroom_id "
            + "INNER JOIN courses ON lessons.lesson_course = courses.course_id "
            + "INNER JOIN teachers ON lessons.lesson_teacher = teachers.teacher_id "
            + "INNER JOIN groups ON lessons.lesson_group = groups.group_id ";
    private static final String FIND_BY_ID_QUERY = SELECT_INNER_JOIN_OTHERS_TABLES + "WHERE lesson_id = ?";
    private static final String FIND_ALL_QUERY = SELECT_INNER_JOIN_OTHERS_TABLES + "ORDER BY lesson_id";
    private static final String FIND_ALL_PAGINATION_QUERY = SELECT_INNER_JOIN_OTHERS_TABLES
            + "ORDER BY lesson_id LIMIT ? OFFSET ?;";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM lessons WHERE lesson_id = ?";
    private static final String FIND_ALL_BY_DATE_AND_TEACHER_QUERY = SELECT_INNER_JOIN_OTHERS_TABLES
            + "WHERE lesson_start::date = ? AND lesson_teacher = ? ORDER BY lesson_start";
    private static final String FIND_ALL_BY_DATE_AND_GROUP_QUERY = SELECT_INNER_JOIN_OTHERS_TABLES
            + "WHERE lesson_start::date = ? AND lesson_group = ? ORDER BY lesson_start";
    private static final String FIND_BY_DATE_AND_TEACHER_AND_GROUP_QUERY = SELECT_INNER_JOIN_OTHERS_TABLES
            + "WHERE lesson_start = ? AND teacher_email = ? AND group_name = ?";
    private static final String FIND_ALL_BY_MONTH_AND_TEACHER_QUERY = SELECT_INNER_JOIN_OTHERS_TABLES
            + "WHERE EXTRACT(MONTH FROM lesson_start) = ? AND lesson_teacher = ? ORDER BY lesson_start";
    private static final String FIND_ALL_BY_MONTH_AND_GROUP_QUERY = SELECT_INNER_JOIN_OTHERS_TABLES
            + "WHERE EXTRACT(MONTH FROM lesson_start) = ? AND lesson_group = ? ORDER BY lesson_start";
    private static final String FIND_ALL_BY_DATE_QUERY = SELECT_INNER_JOIN_OTHERS_TABLES
            + "WHERE lesson_start::date = ? ORDER BY lesson_start";

    public LessonDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, SAVE_QUERY, FIND_BY_ID_QUERY, FIND_ALL_QUERY, FIND_ALL_PAGINATION_QUERY,
                DELETE_BY_ID_QUERY);
    }

    @Override
    protected Object[] insert(Lesson lesson) {
        Object[] arguments = new Object[8];
        arguments[0] = lesson.getStartLesson();
        arguments[1] = lesson.getEndLesson();
        arguments[2] = lesson.getOnlineLesson();
        arguments[3] = lesson.getLessonLink();
        arguments[4] = lesson.getClassroom().getId();
        arguments[5] = lesson.getCourse().getId();
        arguments[6] = lesson.getTeacher().getId();
        arguments[7] = lesson.getGroup().getId();
        return arguments;
    }

    @Override
    protected RowMapper<Lesson> getMapper() {
        return new LessonMapper();
    }

    @Override
    public List<Lesson> findAllByDateAndTeacher(String date, int teacherId) {
        return jdbcTemplate.query(FIND_ALL_BY_DATE_AND_TEACHER_QUERY, getMapper(), date, teacherId);
    }

    @Override
    public List<Lesson> findAllByDateAndGroup(String date, int groupId) {
        return jdbcTemplate.query(FIND_ALL_BY_DATE_AND_GROUP_QUERY, getMapper(), date, groupId);
    }

    @Override
    public Optional<Lesson> findByDateAndTeacherAndGroup(String date, String teacherEmail, String groupName) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_DATE_AND_TEACHER_AND_GROUP_QUERY,
                    getMapper(), date, teacherEmail, groupName));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Lesson> findAllByMonthAndTeacher(int month, int teacherId) {
        return jdbcTemplate.query(FIND_ALL_BY_MONTH_AND_TEACHER_QUERY, getMapper(), month, teacherId);
    }

    @Override
    public List<Lesson> findAllByMonthAndGroup(int month, int groupId) {
        return jdbcTemplate.query(FIND_ALL_BY_MONTH_AND_GROUP_QUERY, getMapper(), month, groupId);
    }

    @Override
    public List<Lesson> findAllByDate(String date) {
        return jdbcTemplate.query(FIND_ALL_BY_DATE_QUERY, getMapper(), date);
    }
}
