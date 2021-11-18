package org.university.service.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.university.dao.LessonDao;
import org.university.dto.LessonDto;
import org.university.entity.Lesson;
import org.university.exceptions.ClassroomBusyException;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.exceptions.InvalidLessonTimeException;
import org.university.service.CalendarService;
import org.university.service.LessonService;
import org.university.service.mapper.LessonDtoMapper;
import org.university.service.validator.LessonValidator;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Transactional
public class LessonServiceImpl implements LessonService {

    LessonDao lessonDao;
    LessonValidator validator;
    LessonDtoMapper mapper;
    CalendarService calendarService;

    @Override
    public Lesson createLesson(LocalDateTime startLesson, int teacherId, int groupId) {
        if (!lessonDao.findByStartLessonAndTeacherIdAndGroupId(startLesson, teacherId, groupId).isPresent()) {
            throw new EntityNotExistException();
        }
        return lessonDao.findByStartLessonAndTeacherIdAndGroupId(startLesson, teacherId, groupId).get();
    }

    @Override
    public void addLesson(@NonNull LessonDto lessonDto) {
        Lesson lesson = mapper.mapDtoToEntity(lessonDto);
        validator.validate(lesson);
        checkLessonExist(lesson);
        checkLessonTime(lesson, lessonDto);
        if (!checkFreeClassroom(lesson)) {
            throw new ClassroomBusyException("classroombusy");
        }
        lessonDao.save(lesson);
        try {
            calendarService.createLesson(lesson);
        } catch (IOException | GeneralSecurityException e) {
            log.error("Add in calendar failed");
        }
        log.info("Lesson added succesfull!");
    }

    @Override
    public void edit(@NonNull LessonDto lessonDto) {
        Lesson lesson = mapper.mapDtoToEntity(lessonDto);
        validator.validate(lesson);
        checkLessonTime(lesson, lessonDto);
        if ((!checkTimeNotChange(lessonDto) || !checkClassroomNotChange(lessonDto)) && !checkFreeClassroom(lesson)) {
            throw new ClassroomBusyException("classroombusy");
        }
        lessonDao.save(lesson);
        try {
            calendarService.updateLesson(lesson);
        } catch (IOException | GeneralSecurityException e) {
            log.error("Edit in calendar failed");
        }
        log.info("Lesson edited succesfull!");
    }

    @Override
    public void delete(@NonNull LessonDto lessonDto) {
        Lesson lesson = deleteChainedEntities(lessonDao.findById(lessonDto.getId()).get());
        lessonDao.save(lesson);
        lessonDao.deleteById(lesson.getId());
        try {
            calendarService.deleteLesson(Integer.toString(lesson.getId()));
        } catch (IOException | GeneralSecurityException e) {
            log.error("Delete from calendar failed");
        }
        log.info("Lesson deleted succesfull!");
    }

    private void checkLessonExist(Lesson lesson) {
        if (lesson.getId() != null && existLesson(lesson)) {
            throw new EntityAlreadyExistException("Lesson already exist!");
        }
    }

    private void checkLessonTime(Lesson lesson, LessonDto lessonDto) {
        LocalDateTime startLessons = lesson.getStartLesson().toLocalDate().atStartOfDay();
        LocalDateTime endLessons = startLessons.plusHours(23);
        List<Lesson> teacherLessons = lessonDao.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(startLessons,
                endLessons, lesson.getTeacher().getId());
        List<Lesson> groupLessons = lessonDao.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(startLessons,
                endLessons, lesson.getGroup().getId());
        if (lessonDto.getId() == null
                && (!checkFreeTime(lesson, teacherLessons) || !checkFreeTime(lesson, groupLessons))) {
            throw new InvalidLessonTimeException("groupteacherbusy");
        }
        if (lessonDto.getId() != null && chechLessonChange(lessonDto)
                && (!checkFreeTime(lesson, teacherLessons) || !checkFreeTime(lesson, groupLessons))) {
            throw new InvalidLessonTimeException("groupteacherbusy");
        }
    }
    
    private boolean chechLessonChange(LessonDto lessonDto) {
        return !checkTimeNotChange(lessonDto) || !checkGroupAndTeacherNotChange(lessonDto);
    }

    private boolean checkTimeNotChange(LessonDto lessonDto) {
        return (lessonDao.findById(lessonDto.getId()).get().getStartLesson()
                .isEqual(LocalDateTime.parse(lessonDto.getStartLesson()))
                && lessonDao.findById(lessonDto.getId()).get().getEndLesson()
                        .isEqual(LocalDateTime.parse(lessonDto.getEndLesson())));
    }

    private boolean checkGroupAndTeacherNotChange(LessonDto lessonDto) {
        return (lessonDao.findById(lessonDto.getId()).get().getGroup().getName().equals(lessonDto.getGroupName())
                && lessonDao.findById(lessonDto.getId()).get().getTeacher().getEmail()
                        .equals(lessonDto.getTeacherEmail()));
    }

    private boolean checkClassroomNotChange(LessonDto lessonDto) {
        return lessonDao.findById(lessonDto.getId()).get().getClassroom().getNumber()
                .equals(lessonDto.getClassroomNumber());
    }

    private boolean existLesson(Lesson lesson) {
        return !lessonDao.findById(lesson.getId()).equals(Optional.empty());
    }

    private boolean checkFreeTime(Lesson lesson, List<Lesson> lessons) {
        if (lessons.isEmpty()) {
            return true;
        }
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
        LocalDate lessonDate = inputLesson.getStartLesson().toLocalDate();
        List<Lesson> dayLessons = lessonDao.findAllByStartLessonBetweenOrderByStartLesson(lessonDate.atStartOfDay(),
                lessonDate.atStartOfDay().plusHours(23));
        List<Lesson> classroomLessons = new ArrayList<>();
        for (Lesson lesson : dayLessons) {
            if (inputLesson.getClassroom().equals(lesson.getClassroom())) {
                classroomLessons.add(lesson);
            }
        }
        return checkFreeTime(inputLesson, classroomLessons);
    }

    private Lesson deleteChainedEntities(Lesson lesson) {
        return Lesson.builder()
                .withId(lesson.getId())
                .withCourse(null)
                .withGroup(null)
                .withTeacher(null)
                .withClassroom(null)
                .withStartLesson(lesson.getStartLesson())
                .withEndLesson(lesson.getEndLesson())
                .withOnlineLesson(lesson.getOnlineLesson())
                .withLessonLink(lesson.getLessonLink())
                .build();
    }
}
