package org.university.dao;

import java.util.List;
import java.util.Optional;
import org.university.entity.Lesson;

public interface LessonDao extends CrudDao<Lesson, Integer> {

    List<Lesson> findAllByDateAndTeacher(String date, int teacherId);

    List<Lesson> findAllByDateAndGroup(String date, int groupId);
    
    List<Lesson> findAllByMonthAndTeacher(int month, int teacherId);
    
    List<Lesson> findAllByMonthAndGroup(int month, int groupId);

    Optional<Lesson> findByDateAndTeacherAndGroup(String date, String teacherEmail, String groupName);
    
    List<Lesson> findAllByDate(String date);
}
