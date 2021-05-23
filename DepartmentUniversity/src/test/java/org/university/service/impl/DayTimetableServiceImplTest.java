package org.university.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.dao.impl.GroupDaoImpl;
import org.university.dao.impl.LessonDaoImpl;
import org.university.dao.impl.TeacherDaoImpl;
import org.university.entity.DayTimetable;
import org.university.entity.Lesson;
import org.university.exceptions.EntityNotExistException;
import org.university.utils.CreatorTestEntities;

class DayTimetableServiceImplTest {

    private static DayTimetableServiceImpl dayTimetableService;

    @BeforeAll
    static void init() {
        dayTimetableService = new DayTimetableServiceImpl(createLessonDaoMock(), createTeacherDaoMock(), createGroupDaoMock());
    }

    @Test
    void createTeacherTimetableShouldReturnExpectedTimetableWhenTeacherLessonsExistOnInputDate() {
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        lessons.remove(0);
        assertThat(dayTimetableService.createTeacherTimetable("2021-10-19", "Ann@mail.ru"))
                .isEqualTo(new DayTimetable(LocalDate.of(2021, 10, 19), lessons));
    }

    @Test
    void createTeacherTimetableShouldThrowEntityNotExistExceptionWhenTeacherEmailNotExist() {
        assertThatThrownBy(() -> dayTimetableService.createTeacherTimetable("2021-10-19", "notexistedemail"))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void createGroupTimetableShouldReturnExpectedTimetableWhenGroupLessonsExistOnInputDate() {
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        lessons.remove(2);
        lessons.remove(1);
        assertThat(dayTimetableService.createGroupTimetable("2021-10-19", "AB-22"))
                .isEqualTo(new DayTimetable(LocalDate.of(2021, 10, 19), lessons));
    }

    @Test
    void createGroupTimetableShouldThrowEntityNotExistExceptionWhenGroupNameNotExist() {
        assertThatThrownBy(() -> dayTimetableService.createGroupTimetable("2021-10-19", "notexistedname"))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void createMonthTeacherTimetableShouldReturnExpectedMonthTimetablesWhenTeacherEmailExist() {
        assertThat(dayTimetableService.createMonthTeacherTimetable("2021-10-19", "Bob@mail.ru"))
                .isEqualTo(createTestMonthTimetable());
    }

    @Test
    void createMonthTeacherTimetableShouldThrowEntityNotExistExceptionWhenTeacherEmailNotExist() {
        assertThatThrownBy(() -> dayTimetableService.createMonthTeacherTimetable("2021-10-19", "notexistedemail"))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void createMonthTeacherTimetableShouldReturnEmptyListWhenTeacherLessonsNotExist() {
        assertThat(dayTimetableService.createMonthTeacherTimetable("2021-12-19", "Bob@mail.ru")).isEmpty();
    }

    @Test
    void createMonthGroupTimetableShouldReturnExpectedDayTimetablesWhenGroupNameExist() {
        assertThat(dayTimetableService.createMonthGroupTimetable("2021-10-19", "AB-22"))
                .isEqualTo(createTestMonthTimetable());
    }

    @Test
    void createMonthGroupTimetableShouldThrowEntityNotExistExceptionWhenGroupNameNotExist() {
        assertThatThrownBy(() -> dayTimetableService.createMonthGroupTimetable("2021-10-19", "notexistedname"))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void createMonthGroupTimetableShouldReturnEmptyListWhenGroupLessonsNotExist() {
        assertThat(dayTimetableService.createMonthGroupTimetable("2021-12-19", "AB-22")).isEmpty();
    }

    private List<DayTimetable> createTestMonthTimetable() {
        List<DayTimetable> monthTimetable = new ArrayList<>();
        List<Lesson> lessonsFirst = new ArrayList<>();
        Lesson lesson = createTestLessonWithDay(4,2);
        lessonsFirst.add(lesson);
        monthTimetable.add(new DayTimetable(LocalDate.of(2021, 10, 2), lessonsFirst));
        List<Lesson> lessonsSecond = new ArrayList<>();
        lessonsSecond = CreatorTestEntities.createLessons();
        lessonsSecond.remove(2);
        lessonsSecond.remove(1);
        monthTimetable.add(new DayTimetable(LocalDate.of(2021, 10, 19), lessonsSecond));
        lesson = createTestLessonWithDay(5,30);
        List<Lesson> lessonsThird = new ArrayList<>();
        lessonsThird.add(lesson);
        monthTimetable.add(new DayTimetable(LocalDate.of(2021, 10, 30), lessonsThird));
        return monthTimetable;
    }

    private static LessonDaoImpl createLessonDaoMock() {
        LessonDaoImpl lessonDaoMock = mock(LessonDaoImpl.class);
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        lessons.remove(0);
        when(lessonDaoMock.findAllByDateAndTeacher("2021-10-19", 2)).thenReturn(lessons);
        lessons = new ArrayList<>();
        lessons.add(createTestLessonWithDay(4,2));
        lessons.add(createTestLessonWithDay(1,19));
        lessons.add(createTestLessonWithDay(5,30));
        when(lessonDaoMock.findAllByMonthAndTeacher(10, 1)).thenReturn(lessons);
        when(lessonDaoMock.findAllByMonthAndGroup(10, 1)).thenReturn(lessons);
        lessons = CreatorTestEntities.createLessons();
        lessons.remove(2);
        lessons.remove(1);
        when(lessonDaoMock.findAllByDateAndGroup("2021-10-19", 1)).thenReturn(lessons);
        return lessonDaoMock;
    }

    private static TeacherDaoImpl createTeacherDaoMock() {
        TeacherDaoImpl teacherDaoMock = mock(TeacherDaoImpl.class);
        when(teacherDaoMock.findByEmail("Bob@mail.ru"))
                .thenReturn(Optional.of(CreatorTestEntities.createTeachers().get(0)));
        when(teacherDaoMock.findByEmail("Ann@mail.ru"))
                .thenReturn(Optional.of(CreatorTestEntities.createTeachers().get(1)));
        return teacherDaoMock;
    }

    private static GroupDaoImpl createGroupDaoMock() {
        GroupDaoImpl groupDaoMock = mock(GroupDaoImpl.class);
        when(groupDaoMock.findByName("FR-33"))
                .thenReturn(Optional.ofNullable(CreatorTestEntities.createGroups().get(1)));
        when(groupDaoMock.findByName("AB-22"))
                .thenReturn(Optional.ofNullable(CreatorTestEntities.createGroups().get(0)));
        return groupDaoMock;
    }
    
    private static Lesson createTestLessonWithDay(int id, int day) {
        return Lesson.builder()
                .withId(id)
                .withStartLesson(LocalDateTime.of(2021, Month.OCTOBER, day, 10, 00, 00))
                .withEndLesson(LocalDateTime.of(2021, Month.OCTOBER, day, 12, 00, 00))
                .withOnlineLesson(false)
                .withLessonLink(null)
                .withClassroom(CreatorTestEntities.createClassrooms().get(0))
                .withCourse(CreatorTestEntities.createCourses().get(0))
                .withTeacher(CreatorTestEntities.createTeachers().get(0))
                .withGroup(CreatorTestEntities.createGroups().get(0))
                .build();
    }
}
