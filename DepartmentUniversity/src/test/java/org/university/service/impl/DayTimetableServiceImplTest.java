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
import org.university.dao.GroupDao;
import org.university.dao.LessonDao;
import org.university.dao.TeacherDao;
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
        assertThat(dayTimetableService.createTeacherTimetable(LocalDate.of(2021, Month.OCTOBER, 19), "Ann@mail.ru"))
                .isEqualTo(new DayTimetable(LocalDate.of(2021, 10, 19), lessons));
    }

    @Test
    void createTeacherTimetableShouldThrowEntityNotExistExceptionWhenTeacherEmailNotExist() {
        assertThatThrownBy(() -> dayTimetableService.createTeacherTimetable(LocalDate.of(2021, Month.OCTOBER, 19), "notexistedemail"))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void createGroupTimetableShouldReturnExpectedTimetableWhenGroupLessonsExistOnInputDate() {
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        lessons.remove(2);
        lessons.remove(1);
        assertThat(dayTimetableService.createGroupTimetable(LocalDate.of(2021, Month.OCTOBER, 19), "AB-22"))
                .isEqualTo(new DayTimetable(LocalDate.of(2021, 10, 19), lessons));
    }

    @Test
    void createGroupTimetableShouldThrowEntityNotExistExceptionWhenGroupNameNotExist() {
        assertThatThrownBy(() -> dayTimetableService.createGroupTimetable(LocalDate.of(2021, Month.OCTOBER, 19), "notexistedname"))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void createMonthTeacherTimetableShouldReturnExpectedMonthTimetablesWhenTeacherEmailExist() {
        assertThat(dayTimetableService.createMonthTeacherTimetable(LocalDate.of(2021, Month.OCTOBER, 19), "Bob@mail.ru"))
                .isEqualTo(createTestMonthTimetable());
    }

    @Test
    void createMonthTeacherTimetableShouldThrowEntityNotExistExceptionWhenTeacherEmailNotExist() {
        assertThatThrownBy(() -> dayTimetableService.createMonthTeacherTimetable(LocalDate.of(2021, Month.OCTOBER, 19), "notexistedemail"))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void createMonthTeacherTimetableShouldReturnEmptyListWhenTeacherLessonsNotExist() {
        assertThat(dayTimetableService.createMonthTeacherTimetable(LocalDate.of(2021, Month.DECEMBER, 19), "Bob@mail.ru")).isEmpty();
    }

    @Test
    void createMonthGroupTimetableShouldReturnExpectedDayTimetablesWhenGroupNameExist() {
        assertThat(dayTimetableService.createMonthGroupTimetable(LocalDate.of(2021, Month.OCTOBER, 19), "AB-22"))
                .isEqualTo(createTestMonthTimetable());
    }

    @Test
    void createMonthGroupTimetableShouldThrowEntityNotExistExceptionWhenGroupNameNotExist() {
        assertThatThrownBy(() -> dayTimetableService.createMonthGroupTimetable(LocalDate.of(2021, Month.OCTOBER, 19), "notexistedname"))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void createMonthGroupTimetableShouldReturnEmptyListWhenGroupLessonsNotExist() {
        assertThat(dayTimetableService.createMonthGroupTimetable(LocalDate.of(2021, Month.DECEMBER, 19), "AB-22")).isEmpty();
    }
    
    @Test
    void createDayTimetableShouldReturnExpectedDayTimetableWhenLessonsExistInInputDate() {
        List<Lesson> lessons = CreatorTestEntities.createLessons();        
        assertThat(dayTimetableService.createDayTimetable(LocalDate.of(2021, 10, 19)))
        .isEqualTo(new DayTimetable(LocalDate.of(2021, 10, 19), lessons));
    }
    
    @Test
    void createDayTimetableShouldReturnExpectedDayTimetableWhenLessonsNotExistInInputDate() {
        List<Lesson> lessons = new ArrayList<>();        
        assertThat(dayTimetableService.createDayTimetable(LocalDate.of(2021, 10, 23)))
        .isEqualTo(new DayTimetable(LocalDate.of(2021, 10, 23), lessons));
    }
    
    @Test
    void createWeekTeacherTimetableShouldReturnExpectedWeekTimetablesWhenTeacherEmailExist() {               
        assertThat(dayTimetableService.createWeekTeacherTimetable(LocalDate.of(2021, Month.OCTOBER, 19), "Bob@mail.ru"))
                .isEqualTo(createTestWeekTimetable());
    }
    
    @Test
    void createWeekTeacherTimetableShouldThrowEntityNotExistExceptionWhenTeacherEmailNotExist() {
        assertThatThrownBy(() -> dayTimetableService.createWeekTeacherTimetable(LocalDate.of(2021, Month.OCTOBER, 19), "notexistedemail"))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void createWeekTeacherTimetableShouldReturnEmptyListWhenTeacherLessonsNotExist() {
        assertThat(dayTimetableService.createWeekTeacherTimetable(LocalDate.of(2021, Month.DECEMBER, 19), "Bob@mail.ru")).isEmpty();
    }
    
    @Test
    void createWeekGroupTimetableShouldReturnExpectedWeekTimetablesWhenGroupNameExist() {               
        assertThat(dayTimetableService.createWeekGroupTimetable(LocalDate.of(2021, Month.OCTOBER, 19), "AB-22"))
                .isEqualTo(createTestWeekTimetable());
    }
    
    @Test
    void createWeekGroupTimetableShouldThrowEntityNotExistExceptionWhenGroupNameNotExist() {
        assertThatThrownBy(() -> dayTimetableService.createWeekGroupTimetable(LocalDate.of(2021, Month.OCTOBER, 19), "notexistedname"))
                .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void createWeekGroupTimetableShouldReturnEmptyListWhenGroupLessonsNotExist() {
        assertThat(dayTimetableService.createWeekGroupTimetable(LocalDate.of(2021, Month.DECEMBER, 19), "AB-22")).isEmpty();
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
    
    private List<DayTimetable> createTestWeekTimetable() {
        List<DayTimetable> weekTimetable = new ArrayList<>();
        List<Lesson> lessonsFirst = new ArrayList<>();
        Lesson lesson = createTestLessonWithDay(1,18);
        lessonsFirst.add(lesson);
        weekTimetable.add(new DayTimetable(LocalDate.of(2021, 10, 18), lessonsFirst));
        List<Lesson> lessonsSecond = new ArrayList<>();
        lesson = createTestLessonWithDay(2,20);
        lessonsSecond.add(lesson);
        weekTimetable.add(new DayTimetable(LocalDate.of(2021, 10, 20), lessonsSecond));
        lesson = createTestLessonWithDay(3,22);
        List<Lesson> lessonsThird = new ArrayList<>();
        lessonsThird.add(lesson);
        weekTimetable.add(new DayTimetable(LocalDate.of(2021, 10, 22), lessonsThird));
        return weekTimetable;
    }

    private static LessonDao createLessonDaoMock() {
        LessonDao lessonDaoMock = mock(LessonDao.class);
        List<Lesson> lessons = CreatorTestEntities.createLessons();        
        lessons.remove(0);
        when(lessonDaoMock.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19,0,0),LocalDateTime.of(2021, Month.OCTOBER, 19,23,0), 2)).thenReturn(lessons);
        lessons = new ArrayList<>();
        lessons.add(createTestLessonWithDay(4,2));
        lessons.add(createTestLessonWithDay(1,19));
        lessons.add(createTestLessonWithDay(5,30));
        when(lessonDaoMock.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 1,0,0),LocalDateTime.of(2021, Month.OCTOBER, 31,23,0), 1)).thenReturn(lessons);
        when(lessonDaoMock.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 1,0,0),LocalDateTime.of(2021, Month.OCTOBER, 31,23,0), 1)).thenReturn(lessons);
        lessons = CreatorTestEntities.createLessons();
        lessons.remove(2);
        lessons.remove(1);
        when(lessonDaoMock.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19,0,0),LocalDateTime.of(2021, Month.OCTOBER, 19,23,0), 1)).thenReturn(lessons);
        lessons = CreatorTestEntities.createLessons();
        when(lessonDaoMock.findAllByStartLessonBetweenOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19,0,0),LocalDateTime.of(2021, Month.OCTOBER, 19,23,0))).thenReturn(lessons);
        when(lessonDaoMock.findAllByStartLessonBetweenOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 21,0,0),LocalDateTime.of(2021, Month.OCTOBER, 21,23,0))).thenReturn(new ArrayList<Lesson>());
        List<Lesson> weekLessons = new ArrayList<>();
        weekLessons.add(createTestLessonWithDay(1,18));
        weekLessons.add(createTestLessonWithDay(2,20));
        weekLessons.add(createTestLessonWithDay(3,22));
        when(lessonDaoMock.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 18,0,0), LocalDateTime.of(2021, Month.OCTOBER, 24,23,0), 1)).thenReturn(weekLessons);
        when(lessonDaoMock.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 18,0,0), LocalDateTime.of(2021, Month.OCTOBER, 24,23,0), 1)).thenReturn(weekLessons);
        return lessonDaoMock;
    }

    private static TeacherDao createTeacherDaoMock() {
        TeacherDao teacherDaoMock = mock(TeacherDao.class);
        when(teacherDaoMock.findByEmail("Bob@mail.ru"))
                .thenReturn(Optional.of(CreatorTestEntities.createTeachers().get(0)));
        when(teacherDaoMock.findByEmail("Ann@mail.ru"))
                .thenReturn(Optional.of(CreatorTestEntities.createTeachers().get(1)));
        return teacherDaoMock;
    }

    private static GroupDao createGroupDaoMock() {
        GroupDao groupDaoMock = mock(GroupDao.class);
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
