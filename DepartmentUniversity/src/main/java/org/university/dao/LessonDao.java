package org.university.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.university.entity.Lesson;

public interface LessonDao extends CrudDao<Lesson, Integer> {

    List<Lesson> findAllByDateAndTeacher(LocalDate date, int teacherId);

    List<Lesson> findAllByDateAndGroup(LocalDate date, int groupId);

    List<Lesson> findAllByMonthAndTeacher(int month, int teacherId);

    List<Lesson> findAllByMonthAndGroup(int month, int groupId);

    Optional<Lesson> findByTimeAndTeacherAndGroup(LocalDateTime date, int teacherId, int groupId);

    List<Lesson> findAllByDate(LocalDate date);

    List<Lesson> findAllByWeekAndTeacher(LocalDate startWeek, LocalDate endWeek, int teacherId);

    List<Lesson> findAllByWeekAndGroup(LocalDate startWeek, LocalDate endWeek, int groupId);
}
