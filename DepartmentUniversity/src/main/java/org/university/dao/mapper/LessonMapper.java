package org.university.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.university.entity.Classroom;
import org.university.entity.Course;
import org.university.entity.Group;
import org.university.entity.Lesson;
import org.university.entity.Teacher;

public class LessonMapper implements RowMapper<Lesson> {

    @Override
    public Lesson mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Group group = Group.builder()
                .id(resultSet.getInt("group_id"))
                .name(resultSet.getString("group_name"))
                .build();
        Teacher teacher = Teacher.builder()
                .withId(resultSet.getInt("teacher_id"))
                .withSex(resultSet.getString("teacher_sex"))
                .withName(resultSet.getString("teacher_name"))
                .withEmail(resultSet.getString("teacher_email"))
                .withPhone(resultSet.getString("teacher_phone"))
                .withPassword(resultSet.getString("teacher_password"))
                .withDegree(resultSet.getString("teacher_degree"))
                .build();
        Classroom classroom = Classroom.builder()
                .withId(resultSet.getInt("classroom_id"))
                .withNumber(resultSet.getInt("classroom_number"))
                .withAddress(resultSet.getString("classroom_address"))
                .withCapacity(resultSet.getInt("classroom_capacity"))
                .build();
        Course course = Course.builder()
                .id(resultSet.getInt("course_id"))
                .name(resultSet.getString("course_name"))
                .description(resultSet.getString("course_description"))
                .build();
        return Lesson.builder()
                .withId(resultSet.getInt("lesson_id"))
                .withCourse(course)
                .withTeacher(teacher)
                .withClassroom(classroom)
                .withGroup(group)
                .withStartLesson(resultSet.getTimestamp("lesson_start").toLocalDateTime())
                .withEndLesson(resultSet.getTimestamp("lesson_end").toLocalDateTime())
                .withOnlineLesson(resultSet.getBoolean("lesson_online"))
                .withLessonLink(resultSet.getString("lesson_link"))
                .build();
    }
}
