package org.university.service.impl;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
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

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DayTimetableServiceImpl implements DayTimetableService {

    LessonDao lessonDao;
    TeacherDao teacherDao;
    GroupDao groupDao;

    @Override
    public DayTimetable createTeacherTimetable(String date, String teacherEmail) {
        int teacherId = checkAndGetTeacherId(teacherEmail);
        return new DayTimetable(LocalDate.parse(date), lessonDao.findAllByDateAndTeacher(date, teacherId));
    }

    @Override
    public DayTimetable createGroupTimetable(String date, String groupName) {
        int groupId = checkAndGetGroupId(groupName);
        return new DayTimetable(LocalDate.parse(date), lessonDao.findAllByDateAndGroup(date, groupId));
    }

    @Override
    public List<DayTimetable> createMonthTeacherTimetable(String date, String teacherEmail) {
        int teacherId = checkAndGetTeacherId(teacherEmail);
        Month month = LocalDate.parse(date).getMonth();
        int year = LocalDate.parse(date).getYear();
        List<DayTimetable> monthTimetable = new ArrayList<>();
        List<Lesson> lessons = lessonDao.findAllByMonthAndTeacher(month.getValue(), teacherId);
        for (int i = 1; i <= month.maxLength(); i++) {
            LocalDate day = LocalDate.of(year, month, i);
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
    public List<DayTimetable> createMonthGroupTimetable(String date, String groupName) {
        int groupId = checkAndGetGroupId(groupName);
        Month month = LocalDate.parse(date).getMonth();
        int year = LocalDate.parse(date).getYear();
        List<DayTimetable> monthTimetable = new ArrayList<>();
        List<Lesson> lessons = lessonDao.findAllByMonthAndGroup(month.getValue(), groupId);
        for (int i = 1; i <= month.maxLength(); i++) {
            LocalDate day = LocalDate.of(year, month, i);
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
