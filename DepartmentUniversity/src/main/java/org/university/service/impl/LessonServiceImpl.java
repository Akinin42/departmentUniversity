package org.university.service.impl;

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

    @Override
    public Lesson createLesson(LocalDateTime startLesson, int teacherId, int groupId) {
        if (!lessonDao.findByTimeAndTeacherAndGroup(startLesson, teacherId, groupId).isPresent()) {
            throw new EntityNotExistException();
        }
        return lessonDao.findByTimeAndTeacherAndGroup(startLesson, teacherId, groupId).get();
    }

    @Override
    public void addLesson(@NonNull LessonDto lessonDto) {
        Lesson lesson = mapper.mapDtoToEntity(lessonDto);
        validator.validate(lesson);
        if (lesson.getId() != null && existLesson(lesson)) {
            throw new EntityAlreadyExistException("Lesson already exist!");
        }
        LocalDate lessonDate = lesson.getStartLesson().toLocalDate();
        List<Lesson> teacherLessons = lessonDao.findAllByDateAndTeacher(lessonDate, lesson.getTeacher().getId());
        List<Lesson> groupLessons = lessonDao.findAllByDateAndGroup(lessonDate, lesson.getGroup().getId());
        if (!checkFreeTime(lesson, teacherLessons) || !checkFreeTime(lesson, groupLessons)) {
            throw new InvalidLessonTimeException("groupteacherbusy");
        }
        if (!checkFreeClassroom(lesson)) {
            throw new ClassroomBusyException("classroombusy");
        }
        lessonDao.save(lesson);
        log.info("Lesson added succesfull!");
    }

    @Override
    public void edit(@NonNull LessonDto lessonDto) {
        Lesson lesson = mapper.mapDtoToEntity(lessonDto);
        validator.validate(lesson);
        LocalDate lessonDate = lesson.getStartLesson().toLocalDate();
        List<Lesson> teacherLessons = lessonDao.findAllByDateAndTeacher(lessonDate, lesson.getTeacher().getId());
        List<Lesson> groupLessons = lessonDao.findAllByDateAndGroup(lessonDate, lesson.getGroup().getId());
        if ((!checkTimeNotChange(lessonDto) || !checkGroupAndTeacherNotChange(lessonDto))
                && (!checkFreeTime(lesson, teacherLessons) || !checkFreeTime(lesson, groupLessons))) {
            throw new InvalidLessonTimeException("groupteacherbusy");
        }
        if ((!checkTimeNotChange(lessonDto) || !checkClassroomNotChange(lessonDto)) && !checkFreeClassroom(lesson)) {
            throw new ClassroomBusyException("classroombusy");
        }
        lessonDao.update(lesson);
        log.info("Lesson edited succesfull!");
    }

    @Override
    public void delete(@NonNull LessonDto lessonDto) {        
        Lesson lesson = deleteChainedEntities(lessonDao.findById(lessonDto.getId()).get());
        lessonDao.update(lesson);
        lessonDao.deleteById(lesson.getId());        
        log.info("Lesson deleted succesfull!");
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
        List<Lesson> dayLessons = lessonDao.findAllByDate(lessonDate);
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
