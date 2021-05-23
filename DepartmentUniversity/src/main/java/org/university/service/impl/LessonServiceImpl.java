package org.university.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.university.dao.LessonDao;
import org.university.entity.Lesson;
import org.university.exceptions.ClassroomBusyException;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.exceptions.InvalidLessonTimeException;
import org.university.service.LessonService;
import org.university.service.validator.LessonValidator;

@Component
public class LessonServiceImpl implements LessonService {

    private final LessonDao lessonDao;
    private final LessonValidator validator;

    public LessonServiceImpl(LessonDao lessonDao, LessonValidator validator) {
        this.lessonDao = lessonDao;
        this.validator = validator;
    }

    @Override
    public Lesson createLesson(String startLesson, String teacherEmail, String groupName) {
        if (!lessonDao.findByDateAndTeacherAndGroup(startLesson, teacherEmail, groupName).isPresent()) {
            throw new EntityNotExistException();
        }
        return lessonDao.findByDateAndTeacherAndGroup(startLesson, teacherEmail, groupName).get();
    }

    @Override
    public void addLesson(Lesson lesson) {
        validator.validate(lesson);
        if (existLesson(lesson)) {
            throw new EntityAlreadyExistException();
        }
        String lessonDate = lesson.getStartLesson().toLocalDate().toString();
        List<Lesson> teacherLessons = lessonDao.findAllByDateAndTeacher(lessonDate, lesson.getTeacher().getId());
        List<Lesson> groupLessons = lessonDao.findAllByDateAndGroup(lessonDate, lesson.getGroup().getId());
        if (!checkFreeTime(lesson, teacherLessons) || !checkFreeTime(lesson, groupLessons)) {
            throw new InvalidLessonTimeException();
        }
        if (!checkFreeClassroom(lesson)) {
            throw new ClassroomBusyException();
        }
        lessonDao.save(lesson);
    }

    @Override
    public void delete(Lesson lesson) {
        validator.validate(lesson);
        lessonDao.deleteById(lesson.getId());
    }

    private boolean existLesson(Lesson lesson) {
        return !lessonDao.findById(lesson.getId()).equals(Optional.empty());
    }

    private boolean checkFreeTime(Lesson lesson, List<Lesson> lessons) {
        LocalDateTime inputLessonStart = lesson.getStartLesson();
        LocalDateTime inputLessonEnd = lesson.getEndLesson();
        for (int i = 0; i < lessons.size(); i++) {
            if (inputLessonEnd.isBefore(lessons.get(i).getStartLesson()) && i == 0) {
                return true;
            }
            if (inputLessonStart.isAfter(lessons.get(i).getEndLesson()) && (i + 1) == lessons.size()) {
                return true;
            }
            if (inputLessonStart.isAfter(lessons.get(i).getEndLesson())
                    && inputLessonEnd.isBefore(lessons.get(i + 1).getStartLesson())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkFreeClassroom(Lesson inputLesson) {
        String lessonDate = inputLesson.getStartLesson().toLocalDate().toString();
        List<Lesson> dayLessons = lessonDao.findAllByDate(lessonDate);
        List<Lesson> classroomLessons = new ArrayList<>();
        for (Lesson lesson : dayLessons) {
            if (inputLesson.getClassroom().equals(lesson.getClassroom())) {
                classroomLessons.add(lesson);
            }
        }
        return checkFreeTime(inputLesson, classroomLessons);
    }
}