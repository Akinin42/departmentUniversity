package org.university.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.dao.impl.LessonDaoImpl;
import org.university.entity.Classroom;
import org.university.entity.Group;
import org.university.entity.Lesson;
import org.university.entity.Student;
import org.university.entity.Teacher;
import org.university.exceptions.ClassroomBusyException;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.exceptions.InvalidClassroomCapacityException;
import org.university.exceptions.InvalidLessonTimeException;
import org.university.service.validator.LessonValidator;
import org.university.utils.CreatorTestEntities;

class LessonServiceImplTest {

    private static LessonServiceImpl lessonService;
    private static LessonDaoImpl lessonDaoMock;
    private static Lesson lessonMock;

    @BeforeAll
    static void init() {
        lessonDaoMock = createLessonDaoMock();
        lessonService = new LessonServiceImpl(lessonDaoMock, new LessonValidator());
        lessonMock = createLessonMock();
    }

    @Test
    void createLessonShouldReturnExpectedLessonWhenItExistsInDatabase() {
        Lesson lesson = CreatorTestEntities.createLessons().get(0);
        assertThat(lessonService.createLesson("2021-10-19 10:00:00", "Bob@mail.ru", "AB-22")).isEqualTo(lesson);
    }

    @Test
    void createLessonShouldThrowEntityNotExistExceptionWhenLessonWithInputDataNotExist() {
        assertThatThrownBy(() -> lessonService.createLesson("2021-10-30 10:00:00", "notexistemail", "notexistgroup"))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void addLessonShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> lessonService.addLesson(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addLessonShouldThrowInvalidClassroomCapacityExceptionWhenInputLessonHasInvalidClassroomCapacityForGroup() {
        Lesson lessonMock = mock(Lesson.class);
        Group groupMock = mock(Group.class);
        Classroom classroomMock = mock(Classroom.class);
        List students = mock(List.class);
        when(lessonMock.getGroup()).thenReturn(groupMock);
        when(groupMock.getStudents()).thenReturn(students);
        when(students.size()).thenReturn(10);
        when(lessonMock.getClassroom()).thenReturn(classroomMock);
        when(classroomMock.getCapacity()).thenReturn(5);
        assertThatThrownBy(() -> lessonService.addLesson(lessonMock))
                .isInstanceOf(InvalidClassroomCapacityException.class);
    }

    @Test
    void addLessonShouldThrowInvalidLessonTimeExceptionWhenInputLessonStartTimeIsSunday() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.MAY, 30, 10, 00, 00));
        assertThatThrownBy(() -> lessonService.addLesson(lessonMock)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void addLessonShouldThrowInvalidLessonTimeExceptionWhenInputLessonStartTimeBeforeNineAM() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.MAY, 28, 8, 00, 00));
        assertThatThrownBy(() -> lessonService.addLesson(lessonMock)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void addLessonShouldThrowInvalidLessonTimeExceptionWhenInputLessonStartTimeAfterSixPM() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.MAY, 28, 20, 00, 00));
        assertThatThrownBy(() -> lessonService.addLesson(lessonMock)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void addLessonShouldThrowInvalidLessonTimeExceptionWhenInputLessonStartLaterLessonEnd() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.MAY, 28, 15, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.MAY, 28, 13, 00, 00));
        assertThatThrownBy(() -> lessonService.addLesson(lessonMock)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void addLessonShouldThrowInvalidLessonTimeExceptionWhenInputLessonStartEqualLessonEnd() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.MAY, 28, 15, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.MAY, 28, 15, 00, 00));
        assertThatThrownBy(() -> lessonService.addLesson(lessonMock)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void addLessonShouldThrowEntityAlreadyExistExceptionWhenInputLessonExist() {
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getId()).thenReturn(1);
        assertThatThrownBy(() -> lessonService.addLesson(lessonMock)).isInstanceOf(EntityAlreadyExistException.class);
    }

    @Test
    void addLessonShouldThrowInvalidLessonTimeExceptionWhenTeacherBusyThisTime() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 15, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 16, 00, 00));
        assertThatThrownBy(() -> lessonService.addLesson(lessonMock)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void addLessonShouldThrowInvalidLessonTimeExceptionWhenGroupBusyThisTime() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 12, 00, 00));
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        lessons.remove(2);
        lessons.remove(1);
        when(lessonDaoMock.findAllByDateAndGroup("2021-10-19", 2)).thenReturn(lessons);
        assertThatThrownBy(() -> lessonService.addLesson(lessonMock)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void addLessonShouldSaveLessonWhenLessonValidOrTeqcherAndGroupFree() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 18, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 19, 00, 00));
        lessonService.addLesson(lessonMock);
        verify(lessonDaoMock).save(lessonMock);
    }

    @Test
    void addLessonShouldThrowInvalidLessonTimeExceptionWhenLessonAfterLastButNotBeforeNext() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 18, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 21, 00, 00));
        assertThatThrownBy(() -> lessonService.addLesson(lessonMock)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void addLessonShouldSaveLessonWhenLessonValidAndBeforeOutherLessonOnDay() {
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 12, 00, 00));
        lessonService.addLesson(lessonMock);
        verify(lessonDaoMock).save(lessonMock);
    }

    @Test
    void addLessonShouldSaveLessonWhenLessonValidAndAfterOutherLessonOnDay() {
        Lesson lessonMock = createLessonMock();
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        lessons.remove(2);
        lessons.remove(1);
        when(lessonDaoMock.findAllByDateAndTeacher("2021-10-19", 2)).thenReturn(lessons);
        when(lessonDaoMock.findAllByDateAndGroup("2021-10-19", 2)).thenReturn(lessons);
        when(lessonMock.getClassroom()).thenReturn(CreatorTestEntities.createClassrooms().get(0));
        lessonService.addLesson(lessonMock);
        verify(lessonDaoMock).save(lessonMock);
    }

    @Test
    void addLessonShouldThrowClassroomBusyExceptionWhenForTeacherClassroomBusy() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 12, 00, 00));
        when(lessonMock.getClassroom()).thenReturn(CreatorTestEntities.createClassrooms().get(0));
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        lessons.remove(0);
        when(lessonDaoMock.findAllByDateAndTeacher("2021-10-19", 2)).thenReturn(lessons);
        when(lessonDaoMock.findAllByDateAndGroup("2021-10-19", 2)).thenReturn(lessons);
        assertThatThrownBy(() -> lessonService.addLesson(lessonMock)).isInstanceOf(ClassroomBusyException.class);
    }

    @Test
    void deleteShouldDeleteLessonWhenLessonExists() {
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getId()).thenReturn(1);
        lessonService.delete(lessonMock);
        verify(lessonDaoMock).deleteById(1);
    }

    private static Lesson createLessonMock() {
        Lesson lessonMock = mock(Lesson.class);
        Group groupMock = mock(Group.class);
        Teacher teacherMock = mock(Teacher.class);
        List students = mock(List.class);
        when(lessonMock.getGroup()).thenReturn(groupMock);
        when(lessonMock.getTeacher()).thenReturn(teacherMock);
        when(lessonMock.getClassroom()).thenReturn(CreatorTestEntities.createClassrooms().get(1));
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 13, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 14, 00, 00));
        when(groupMock.getStudents()).thenReturn(students);
        when(groupMock.getId()).thenReturn(2);
        when(teacherMock.getId()).thenReturn(2);
        when(students.size()).thenReturn(2);
        return lessonMock;
    }

    private static LessonDaoImpl createLessonDaoMock() {
        LessonDaoImpl lessonDaoMock = mock(LessonDaoImpl.class);
        when(lessonDaoMock.findByDateAndTeacherAndGroup("2021-10-19 10:00:00", "Bob@mail.ru", "AB-22"))
                .thenReturn(Optional.ofNullable(CreatorTestEntities.createLessons().get(0)));
        when(lessonDaoMock.findById(1)).thenReturn(Optional.ofNullable(CreatorTestEntities.createLessons().get(0)));
        when(lessonDaoMock.findById(10)).thenReturn(Optional.empty());
        when(lessonDaoMock.findAllByDate("2021-10-19")).thenReturn(CreatorTestEntities.createLessons());
        return lessonDaoMock;
    }
}
