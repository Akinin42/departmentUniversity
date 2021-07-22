package org.university.dao.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.university.dao.ScriptExecutor;
import org.university.entity.Lesson;
import org.university.io.FileReader;
import org.university.utils.CreatorDataSource;
import org.university.utils.CreatorTestEntities;

class LessonDaoImplTest {

    private static LessonDaoImpl lessonDao;
    private static ScriptExecutor executor;

    @BeforeAll
    static void init() {
        DataSource dataSource = CreatorDataSource.createTestDataSource();
        lessonDao = new LessonDaoImpl(new JdbcTemplate(dataSource));
        executor = new ScriptExecutor(dataSource, new FileReader());
    }

    @BeforeEach
    void createTablesAndData() {
        executor.executeScript("inittestdb.sql");
    }

    @Test
    void saveShouldSaveLessonWhenInputValidLesson() {
        Lesson lesson = Lesson.builder().withId(4).withStartLesson(LocalDateTime.of(2021, Month.JULY, 9, 11, 00, 00))
                .withEndLesson(LocalDateTime.of(2021, Month.JULY, 9, 13, 00, 00)).withOnlineLesson(false)
                .withLessonLink(null).withCourse(CreatorTestEntities.createCourses().get(0))
                .withGroup(CreatorTestEntities.createGroups().get(0))
                .withClassroom(CreatorTestEntities.createClassrooms().get(0))
                .withTeacher(CreatorTestEntities.createTeachers().get(0)).build();
        lessonDao.save(lesson);
        assertThat(lessonDao.findAll()).contains(lesson);
    }

    @Test
    void saveShouldThrowNullPointerExceptionWhenInputNull() {
        assertThatThrownBy(() -> lessonDao.save(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void findByIdShouldReturnEmptyOptionalWhenInputIdNotExists() {
        assertThat(lessonDao.findById(10)).isEmpty();
    }

    @Test
    void findByIdShouldReturnExpectedLessonWhenInputExistentId() {
        assertThat(lessonDao.findById(1).get()).isEqualTo(CreatorTestEntities.createLessons().get(0));
    }

    @Test
    void findAllShouldReturnExpectedLessonsWhenLessonsTableNotEmpty() {
        assertThat(lessonDao.findAll()).isEqualTo(CreatorTestEntities.createLessons());
    }

    @Test
    void findAllShouldReturnEmptyListWhenLessonsTableEmpty() {
        int numberRow = lessonDao.findAll().size() + 1;
        for (int i = 1; i < numberRow; i++) {
            lessonDao.deleteById(i);
        }
        assertThat(lessonDao.findAll()).isEmpty();
    }

    @Test
    void findAllShouldReturnExpectedLessonsWhenInputLimitAndOffset() {
        assertThat(lessonDao.findAll(1, 0)).containsExactly(CreatorTestEntities.createLessons().get(0));
    }

    @Test
    void findAllShouldReturnEmptyListWhenInputOffsetMoreTableSize() {
        assertThat(lessonDao.findAll(2, 10)).isEmpty();
    }

    @Test
    void deleteByIdShouldDeleteLessonWithInputIdWhenThisLessonExists() {
        int id = CreatorTestEntities.createLessons().get(0).getId();
        lessonDao.deleteById(id);
        assertThat(lessonDao.findAll()).doesNotContain(CreatorTestEntities.createLessons().get(0));
    }

    @Test
    void findAllByDateAndTeacherShouldReturnExpectedLessonsWhenInputDateAndTeacherId() {
        assertThat(lessonDao.findAllByDateAndTeacher(LocalDate.of(2021, Month.OCTOBER, 19), 2)).containsExactly(
                CreatorTestEntities.createLessons().get(1), CreatorTestEntities.createLessons().get(2));
    }

    @Test
    void findAllByDateAndTeacherShouldReturnEmptyListWhenInputDateNotExist() {
        assertThat(lessonDao.findAllByDateAndTeacher(LocalDate.of(2021, Month.OCTOBER, 30), 2)).isEmpty();
    }

    @Test
    void findAllByDateAndTeacherShouldReturnEmptyListWhenInputTeacherIdNotExist() {
        assertThat(lessonDao.findAllByDateAndTeacher(LocalDate.of(2021, Month.OCTOBER, 19), 10)).isEmpty();
    }

    @Test
    void findAllByDateAndGroupShouldReturnExpectedLessonsWhenInputDateAndGroupId() {
        assertThat(lessonDao.findAllByDateAndGroup(LocalDate.of(2021, Month.OCTOBER, 19), 2)).containsExactly(
                CreatorTestEntities.createLessons().get(1), CreatorTestEntities.createLessons().get(2));
    }

    @Test
    void findAllByDateAndGroupShouldReturnEmptyListWhenInputDateNotExist() {
        assertThat(lessonDao.findAllByDateAndGroup(LocalDate.of(2021, Month.OCTOBER, 30), 2)).isEmpty();
    }

    @Test
    void findAllByDateAndGroupShouldReturnEmptyListWhenInputGroupIdNotExist() {
        assertThat(lessonDao.findAllByDateAndGroup(LocalDate.of(2021, Month.OCTOBER, 19), 10)).isEmpty();
    }

    @Test
    void findByDateAndTeacherAndGroupShouldReturnEmptyOptionalWhenInputTeacherEmailNotExists() {
        assertThat(lessonDao.findByTimeAndTeacherAndGroup(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00),
                "notexistemail", "AB-22")).isEmpty();
    }

    @Test
    void findByDateAndTeacherAndGroupShouldReturnEmptyOptionalWhenInputGroupNameNotExists() {
        assertThat(lessonDao.findByTimeAndTeacherAndGroup(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00), "Bob@mail.ru",
                "notexistegroup")).isEmpty();
    }

    @Test
    void findByDateAndTeacherAndGroupShouldReturnEmptyOptionalWhenInputDateNotExists() {
        assertThat(
                lessonDao.findByTimeAndTeacherAndGroup(LocalDateTime.of(2021, Month.OCTOBER, 26, 10, 00, 00), "Bob@mail.ru", "AB-22"))
                        .isEmpty();
    }

    @Test
    void findByIdShouldReturnExpectedLessonWhenInputExistentArguments() {
        assertThat(lessonDao.findByTimeAndTeacherAndGroup(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00), "Bob@mail.ru", "AB-22").get())
                .isEqualTo(CreatorTestEntities.createLessons().get(0));
    }

    @Test
    void findAllByMonthAndTeacherShouldReturnExpectedLessonsWhenLessonsExist() {
        executor.executeScript("addtestlessons.sql");
        assertThat(lessonDao.findAllByMonthAndTeacher(10, 1)).isEqualTo(createTestMonthLessons());
    }

    @Test
    void findAllByMonthAndTeacherShouldReturnEmptyListWhenLessonsNotExist() {
        assertThat(lessonDao.findAllByMonthAndTeacher(5, 1)).isEmpty();
    }

    @Test
    void findAllByMonthAndGroupShouldReturnExpectedLessonsWhenLessonsExist() {
        executor.executeScript("addtestlessons.sql");
        assertThat(lessonDao.findAllByMonthAndGroup(10, 1)).isEqualTo(createTestMonthLessons());
    }

    @Test
    void findAllByMonthAndGroupShouldReturnEmptyListWhenLessonsNotExist() {
        assertThat(lessonDao.findAllByMonthAndGroup(5, 1)).isEmpty();
    }

    @Test
    void findAllByDateShouldReturnExpectedLessonsWhenLessonsExist() {
        assertThat(lessonDao.findAllByDate(LocalDate.of(2021, Month.OCTOBER, 19))).isEqualTo(CreatorTestEntities.createLessons());
    }

    @Test
    void findAllByDateShouldReturnEmptyListWhenLessonsNotExist() {
        assertThat(lessonDao.findAllByDate(LocalDate.of(2021, Month.OCTOBER, 25))).isEmpty();
    }

    @Test
    void findAllByWeekAndTeacherShouldReturnExpectedLessonsWhenLessonsExist() {
        executor.executeScript("addtestlessons.sql");
        assertThat(lessonDao.findAllByWeekAndTeacher(LocalDate.of(2021, Month.JULY, 12), LocalDate.of(2021, Month.JULY, 19), 2))
                .containsExactly(createTestWeekLessons().get(1));
    }

    @Test
    void findAllByWeekAndGroupShouldReturnExpectedLessonsWhenLessonsExist() {
        executor.executeScript("addtestlessons.sql");
        assertThat(lessonDao.findAllByWeekAndGroup(LocalDate.of(2021, Month.JULY, 12), LocalDate.of(2021, Month.JULY, 19), 1)).isEqualTo(createTestWeekLessons());
    }

    @Test
    void updateShouldUpdateLessonWithInputData() {
        Lesson existLesson = CreatorTestEntities.createLessons().get(0);
        Lesson updatedLesson = Lesson.builder().withId(existLesson.getId())
                .withStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 21, 10, 00, 00))
                .withEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 21, 12, 00, 00)).withOnlineLesson(true)
                .withLessonLink("new link").withClassroom(CreatorTestEntities.createClassrooms().get(1))
                .withCourse(CreatorTestEntities.createCourses().get(1))
                .withTeacher(CreatorTestEntities.createTeachers().get(1))
                .withGroup(CreatorTestEntities.createGroups().get(1)).build();
        lessonDao.update(updatedLesson);
        assertThat(lessonDao.findById(1).get()).isEqualTo(updatedLesson);
    }

    private List<Lesson> createTestMonthLessons() {
        List<Lesson> monthLessons = new ArrayList<>();
        Lesson lesson = Lesson.builder().withId(4).withStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 2, 10, 00, 00))
                .withEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 2, 12, 00, 00)).withOnlineLesson(false)
                .withLessonLink(null).withClassroom(CreatorTestEntities.createClassrooms().get(0))
                .withCourse(CreatorTestEntities.createCourses().get(0))
                .withTeacher(CreatorTestEntities.createTeachers().get(0))
                .withGroup(CreatorTestEntities.createGroups().get(0)).build();
        monthLessons.add(lesson);
        monthLessons.add(CreatorTestEntities.createLessons().get(0));
        lesson = Lesson.builder().withId(5).withStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 30, 10, 00, 00))
                .withEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 30, 12, 00, 00)).withOnlineLesson(false)
                .withLessonLink(null).withClassroom(CreatorTestEntities.createClassrooms().get(0))
                .withCourse(CreatorTestEntities.createCourses().get(0))
                .withTeacher(CreatorTestEntities.createTeachers().get(0))
                .withGroup(CreatorTestEntities.createGroups().get(0)).build();
        monthLessons.add(lesson);
        return monthLessons;
    }

    private List<Lesson> createTestWeekLessons() {
        List<Lesson> weekLessons = new ArrayList<>();
        Lesson lesson = Lesson.builder().withId(7).withStartLesson(LocalDateTime.of(2021, Month.JULY, 12, 21, 00, 00))
                .withEndLesson(LocalDateTime.of(2021, Month.JULY, 12, 22, 00, 00)).withOnlineLesson(false)
                .withLessonLink(null).withClassroom(CreatorTestEntities.createClassrooms().get(0))
                .withCourse(CreatorTestEntities.createCourses().get(0))
                .withTeacher(CreatorTestEntities.createTeachers().get(0))
                .withGroup(CreatorTestEntities.createGroups().get(0)).build();
        weekLessons.add(lesson);
        lesson = Lesson.builder().withId(9).withStartLesson(LocalDateTime.of(2021, Month.JULY, 15, 21, 00, 00))
                .withEndLesson(LocalDateTime.of(2021, Month.JULY, 15, 22, 00, 00)).withOnlineLesson(false)
                .withLessonLink(null).withClassroom(CreatorTestEntities.createClassrooms().get(0))
                .withCourse(CreatorTestEntities.createCourses().get(0))
                .withTeacher(CreatorTestEntities.createTeachers().get(1))
                .withGroup(CreatorTestEntities.createGroups().get(0)).build();
        weekLessons.add(lesson);
        return weekLessons;
    }
}
