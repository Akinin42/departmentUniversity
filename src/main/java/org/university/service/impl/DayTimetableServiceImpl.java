package org.university.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.university.dao.GroupDao;
import org.university.dao.LessonDao;
import org.university.dao.TeacherDao;
import org.university.entity.DayTimetable;
import org.university.entity.Group;
import org.university.entity.Lesson;
import org.university.entity.Teacher;
import org.university.exceptions.EntityNotExistException;
import org.university.service.DayTimetableService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional
public class DayTimetableServiceImpl implements DayTimetableService {

    LessonDao lessonDao;
    TeacherDao teacherDao;
    GroupDao groupDao;

    @Override
    public DayTimetable createTeacherTimetable(LocalDate date, String teacherEmail) {
        int teacherId = checkAndGetTeacherId(teacherEmail);
        return new DayTimetable(date, lessonDao.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(
                date.atStartOfDay(), date.atStartOfDay().plusHours(23), teacherId));
    }

    @Override
    public DayTimetable createGroupTimetable(LocalDate date, String groupName) {
        int groupId = checkAndGetGroupId(groupName);
        return new DayTimetable(date, lessonDao.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(
                date.atStartOfDay(), date.atStartOfDay().plusHours(23), groupId));
    }

    @Override
    public List<DayTimetable> createMonthTeacherTimetable(LocalDate date, String teacherEmail) {
        int teacherId = checkAndGetTeacherId(teacherEmail);
        Month month = date.getMonth();
        LocalDateTime monthStart = LocalDateTime.of(date.getYear(), month, 1, 0, 0);
        LocalDateTime monthEnd = LocalDateTime.of(date.getYear(), month, month.maxLength(), 23, 0);
        List<Lesson> lessons = lessonDao.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(monthStart, monthEnd,
                teacherId);
        return fillMonthTimetable(lessons, date);
    }

    @Override
    public List<DayTimetable> createMonthGroupTimetable(LocalDate date, String groupName) {
        int groupId = checkAndGetGroupId(groupName);
        Month month = date.getMonth();        
        LocalDateTime monthStart = LocalDateTime.of(date.getYear(), month, 1, 0, 0);
        LocalDateTime monthEnd = LocalDateTime.of(date.getYear(), month, month.maxLength(), 23, 0);
        List<Lesson> lessons = lessonDao.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(monthStart, monthEnd,
                groupId);
        return fillMonthTimetable(lessons, date);
    }
    
    private List<DayTimetable> fillMonthTimetable(List<Lesson> lessons, LocalDate date) {
        List<DayTimetable> monthTimetable = new ArrayList<>();
        Month month = date.getMonth();        
        for (int i = 1; i <= month.maxLength(); i++) {
            LocalDate day = LocalDate.of(date.getYear(), month, i);
            List<Lesson> dayLessons = new ArrayList<>();
            for (Lesson lesson : lessons) {
                if (lesson.getStartLesson().getDayOfMonth() == i) {
                    dayLessons.add(lesson);
                }
            }
            if (!dayLessons.isEmpty()) {
                DayTimetable dayTimetable = new DayTimetable(day, dayLessons);
                monthTimetable.add(dayTimetable);
            }
        }
        return monthTimetable;
    }

    @Override
    public DayTimetable createDayTimetable(LocalDate date) {
        return new DayTimetable(date, lessonDao.findAllByStartLessonBetweenOrderByStartLesson(date.atStartOfDay(),
                date.atStartOfDay().plusHours(23)));
    }

    @Override
    public List<DayTimetable> createWeekTeacherTimetable(LocalDate date, String teacherEmail) {
        int teacherId = checkAndGetTeacherId(teacherEmail);
        LocalDate monday = date.minusDays(date.getDayOfWeek().getValue() - 1);
        List<Lesson> lessons = lessonDao.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(
                monday.atStartOfDay(), monday.plusDays(6).atStartOfDay().plusHours(23), teacherId);
        return fillWeekTimetable(lessons, monday);
    }

    @Override
    public List<DayTimetable> createWeekGroupTimetable(LocalDate date, String groupName) {
        int groupId = checkAndGetGroupId(groupName);
        LocalDate monday = date.minusDays(date.getDayOfWeek().getValue() - 1);
        List<Lesson> lessons = lessonDao.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(monday.atStartOfDay(),
                monday.plusDays(6).atStartOfDay().plusHours(23), groupId);
        return fillWeekTimetable(lessons, monday);
    }

    private List<DayTimetable> fillWeekTimetable(List<Lesson> lessons, LocalDate monday) {
        List<DayTimetable> weekTimetable = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate day = monday.plusDays(i);
            List<Lesson> dayLessons = new ArrayList<>();
            for (Lesson lesson : lessons) {
                if (lesson.getStartLesson().getDayOfMonth() == day.getDayOfMonth()) {
                    dayLessons.add(lesson);
                }
            }
            if (!dayLessons.isEmpty()) {
                DayTimetable dayTimetable = new DayTimetable(day, dayLessons);
                weekTimetable.add(dayTimetable);
            }
        }
        return weekTimetable;
    }

    private int checkAndGetTeacherId(String teacherEmail) {
        if (!teacherDao.findByEmail(teacherEmail).isPresent()) {
            throw new EntityNotExistException();
        }
        Teacher teacher = teacherDao.findByEmail(teacherEmail).get();
        return teacher.getId();
    }

    private int checkAndGetGroupId(String groupName) {
        if (!groupDao.findByName(groupName).isPresent()) {
            throw new EntityNotExistException();
        }
        Group group = groupDao.findByName(groupName).get();
        return group.getId();
    }
}
