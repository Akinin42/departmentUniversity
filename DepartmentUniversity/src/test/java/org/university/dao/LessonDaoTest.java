package org.university.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.university.entity.Lesson;
import org.university.utils.CreatorTestEntities;

@DataJpaTest
class LessonDaoTest {

    @Autowired
    private LessonDao lessonDao;

    @Test
    void saveShouldSaveLessonWhenInputValidLesson() {
        Lesson lesson = CreatorTestEntities.createTestLesson();
        lessonDao.save(lesson);
        assertThat(lessonDao.findAll()).contains(lesson);
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
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        for (int i = 4; i < 10; i++) {
            lessons.add(lessonDao.findById(i).get());
        }
        assertThat(lessonDao.findAll()).isEqualTo(lessons);
    }

    @Test
    void deleteByIdShouldDeleteLessonWithInputIdWhenThisLessonExists() {
        int id = CreatorTestEntities.createLessons().get(0).getId();
        lessonDao.deleteById(id);
        assertThat(lessonDao.findById(id)).isEmpty();
    }

    @Test
    void findAllByStartLessonBetweenAndTeacherIdOrderByStartLessonShouldReturnExpectedLessonsWhenInputDateAndTeacherId() {
        assertThat(lessonDao.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(
                LocalDateTime.of(2021, Month.OCTOBER, 19, 0, 0), LocalDateTime.of(2021, Month.OCTOBER, 19, 23, 0), 2))
                        .containsExactly(CreatorTestEntities.createLessons().get(1),
                                CreatorTestEntities.createLessons().get(2));
    }

    @Test
    void findAllByStartLessonBetweenAndTeacherIdOrderByStartLessonShouldReturnEmptyListWhenInputDateNotExist() {
        assertThat(lessonDao.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(
                LocalDateTime.of(2021, Month.OCTOBER, 30, 0, 0), LocalDateTime.of(2021, Month.OCTOBER, 30, 23, 0), 2))
                        .isEmpty();
    }

    @Test
    void findAllByStartLessonBetweenAndTeacherIdOrderByStartLessonShouldReturnEmptyListWhenInputTeacherIdNotExist() {
        assertThat(lessonDao.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(
                LocalDateTime.of(2021, Month.OCTOBER, 19, 0, 0), LocalDateTime.of(2021, Month.OCTOBER, 19, 23, 0), 10))
                        .isEmpty();
    }

    @Test
    void findAllByStartLessonBetweenAndGroupIdOrderByStartLessonShouldReturnExpectedLessonsWhenInputDateAndGroupId() {
        assertThat(lessonDao.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(
                LocalDateTime.of(2021, Month.OCTOBER, 19, 0, 0), LocalDateTime.of(2021, Month.OCTOBER, 19, 23, 0), 2))
                        .containsExactly(CreatorTestEntities.createLessons().get(1),
                                CreatorTestEntities.createLessons().get(2));
    }

    @Test
    void findAllByStartLessonBetweenAndGroupIdOrderByStartLessonShouldReturnEmptyListWhenInputDateNotExist() {
        assertThat(lessonDao.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(
                LocalDateTime.of(2021, Month.OCTOBER, 30, 0, 0), LocalDateTime.of(2021, Month.OCTOBER, 30, 23, 0), 2))
                        .isEmpty();
    }

    @Test
    void findAllByStartLessonBetweenAndGroupIdOrderByStartLessonShouldReturnEmptyListWhenInputGroupIdNotExist() {
        assertThat(lessonDao.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(
                LocalDateTime.of(2021, Month.OCTOBER, 19, 0, 0), LocalDateTime.of(2021, Month.OCTOBER, 19, 23, 0), 10))
                        .isEmpty();
    }

    @Test
    void findByStartLessonAndTeacherIdAndGroupIdShouldReturnEmptyOptionalWhenInputTeacherEmailNotExists() {
        assertThat(lessonDao
                .findByStartLessonAndTeacherIdAndGroupId(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00), 45, 1))
                .isEmpty();
    }

    @Test
    void findByStartLessonAndTeacherIdAndGroupIdShouldReturnEmptyOptionalWhenInputGroupNameNotExists() {
        assertThat(lessonDao
                .findByStartLessonAndTeacherIdAndGroupId(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00), 1, 45))
                .isEmpty();
    }

    @Test
    void findByStartLessonAndTeacherIdAndGroupIdShouldReturnEmptyOptionalWhenInputDateNotExists() {
        assertThat(lessonDao
                .findByStartLessonAndTeacherIdAndGroupId(LocalDateTime.of(2021, Month.OCTOBER, 26, 10, 00, 00), 1, 1))
                .isEmpty();
    }

    @Test
    void findByStartLessonAndTeacherIdAndGroupIdShouldReturnExpectedLessonWhenInputExistentArguments() {
        assertThat(lessonDao
                .findByStartLessonAndTeacherIdAndGroupId(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00), 1, 1).get())
                .isEqualTo(CreatorTestEntities.createLessons().get(0));
    }

    @Test
    void findAllByStartLessonBetweenAndTeacherIdOrderByStartLessonShouldReturnExpectedLessonsWhenInputFullMonth() {
        assertThat(lessonDao.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(
                LocalDateTime.of(2021, Month.OCTOBER, 1, 0, 00, 00),
                LocalDateTime.of(2021, Month.OCTOBER, 31, 23, 00, 00), 1)).isEqualTo(createTestMonthLessons());
    }

    @Test
    void findAllByStartLessonBetweenAndGroupIdOrderByStartLessonShouldReturnExpectedLessonsWhenInputFullMonth() {
        assertThat(lessonDao.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(
                LocalDateTime.of(2021, Month.OCTOBER, 1, 0, 00, 00),
                LocalDateTime.of(2021, Month.OCTOBER, 31, 23, 00, 00), 1)).isEqualTo(createTestMonthLessons());
    }

    @Test
    void findAllByStartLessonBetweenOrderByStartLessonShouldReturnExpectedLessonsWhenLessonsExist() {
        assertThat(lessonDao.findAllByStartLessonBetweenOrderByStartLesson(
                LocalDateTime.of(2021, Month.OCTOBER, 19, 0, 0), LocalDateTime.of(2021, Month.OCTOBER, 19, 23, 0)))
                        .isEqualTo(CreatorTestEntities.createLessons());
    }

    @Test
    void findAllByDateShouldReturnEmptyListWhenLessonsNotExist() {
        assertThat(lessonDao.findAllByStartLessonBetweenOrderByStartLesson(
                LocalDateTime.of(2021, Month.OCTOBER, 25, 0, 0), LocalDateTime.of(2021, Month.OCTOBER, 25, 0, 0)))
                        .isEmpty();
    }

    @Test
    void findAllByStartLessonBetweenAndTeacherIdOrderByStartLessonShouldReturnExpectedLessonsWhenInputFullWeek() {
        assertThat(lessonDao.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(
                LocalDateTime.of(2021, Month.JULY, 12, 0, 0), LocalDateTime.of(2021, Month.JULY, 19, 0, 0), 2))
                        .containsExactly(createTestWeekLessons().get(1));
    }

    @Test
    void findAllByStartLessonBetweenAndGroupIdOrderByStartLessonShouldReturnExpectedLessonsWhenInputFullWeek() {
        assertThat(lessonDao.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(
                LocalDateTime.of(2021, Month.JULY, 12, 0, 0), LocalDateTime.of(2021, Month.JULY, 19, 0, 0), 1))
                        .isEqualTo(createTestWeekLessons());
    }

    @Test
    void saveShouldUpdateLessonWithInputData() {
        Lesson existLesson = CreatorTestEntities.createLessons().get(0);
        Lesson updatedLesson = Lesson.builder().withId(existLesson.getId())
                .withStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 21, 10, 00, 00))
                .withEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 21, 12, 00, 00))
                .withOnlineLesson(true)
                .withLessonLink("new link")
                .withClassroom(CreatorTestEntities.createClassrooms().get(1))
                .withCourse(CreatorTestEntities.createCourses().get(1))
                .withTeacher(CreatorTestEntities.createTeachers().get(1))
                .withGroup(CreatorTestEntities.createGroups().get(1))
                .build();
        lessonDao.save(updatedLesson);
        assertThat(lessonDao.findById(1).get()).isEqualTo(updatedLesson);
    }

    private List<Lesson> createTestMonthLessons() {
        List<Lesson> monthLessons = new ArrayList<>();
        Lesson lesson = Lesson.builder()
                .withId(4)
                .withStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 2, 10, 00, 00))
                .withEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 2, 12, 00, 00))
                .withOnlineLesson(false)
                .withLessonLink(null)
                .withClassroom(CreatorTestEntities.createClassrooms().get(0))
                .withCourse(CreatorTestEntities.createCourses().get(0))
                .withTeacher(CreatorTestEntities.createTeachers().get(0))
                .withGroup(CreatorTestEntities.createGroups().get(0))
                .build();
        monthLessons.add(lesson);
        monthLessons.add(CreatorTestEntities.createLessons().get(0));
        lesson = Lesson.builder()
                .withId(5)
                .withStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 30, 10, 00, 00))
                .withEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 30, 12, 00, 00))
                .withOnlineLesson(false)
                .withLessonLink(null)
                .withClassroom(CreatorTestEntities.createClassrooms().get(0))
                .withCourse(CreatorTestEntities.createCourses().get(0))
                .withTeacher(CreatorTestEntities.createTeachers().get(0))
                .withGroup(CreatorTestEntities.createGroups().get(0))
                .build();
        monthLessons.add(lesson);
        return monthLessons;
    }

    private List<Lesson> createTestWeekLessons() {
        List<Lesson> weekLessons = new ArrayList<>();
        Lesson lesson = Lesson.builder()
                .withId(7)
                .withStartLesson(LocalDateTime.of(2021, Month.JULY, 12, 21, 00, 00))
                .withEndLesson(LocalDateTime.of(2021, Month.JULY, 12, 22, 00, 00))
                .withOnlineLesson(false)
                .withLessonLink(null)
                .withClassroom(CreatorTestEntities.createClassrooms().get(0))
                .withCourse(CreatorTestEntities.createCourses().get(0))
                .withTeacher(CreatorTestEntities.createTeachers().get(0))
                .withGroup(CreatorTestEntities.createGroups().get(0))
                .build();
        weekLessons.add(lesson);
        lesson = Lesson.builder()
                .withId(9)
                .withStartLesson(LocalDateTime.of(2021, Month.JULY, 15, 21, 00, 00))
                .withEndLesson(LocalDateTime.of(2021, Month.JULY, 15, 22, 00, 00))
                .withOnlineLesson(false)
                .withLessonLink(null).withClassroom(CreatorTestEntities.createClassrooms().get(0))
                .withCourse(CreatorTestEntities.createCourses().get(0))
                .withTeacher(CreatorTestEntities.createTeachers().get(1))
                .withGroup(CreatorTestEntities.createGroups().get(0))
                .build();
        weekLessons.add(lesson);
        return weekLessons;
    }
}
