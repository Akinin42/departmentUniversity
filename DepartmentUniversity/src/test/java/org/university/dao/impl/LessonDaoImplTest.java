package org.university.dao.impl;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.Month;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.university.config.ApplicationContextInjector;
import org.university.dao.ScriptExecutor;
import org.university.entity.Lesson;
import org.university.utils.CreatorTestEntities;

class LessonDaoImplTest {

    private static LessonDaoImpl lessonDao;
    private static ScriptExecutor executor;

    @BeforeAll
    static void init() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                ApplicationContextInjector.class);
        lessonDao = context.getBean(LessonDaoImpl.class);
        executor = context.getBean(ScriptExecutor.class);
    }

    @BeforeEach
    void createTablesAndData() {
        executor.executeScript("inittestdb.sql");
    }

    @Test
    void saveShouldSaveLessonWhenInputValidLesson() {       
        Lesson lesson = Lesson.builder()
                .withId(1)
                .withStartLesson(LocalDateTime.of(2021, Month.JULY, 9, 11, 00, 00))
                .withEndLesson(LocalDateTime.of(2021, Month.JULY, 9, 13, 00, 00))
                .withOnlineLesson(false)
                .withLessonLink(null)
                .withCourse(CreatorTestEntities.createCourses().get(0))
                .withGroup(CreatorTestEntities.createGroups().get(0))
                .withClassroom(CreatorTestEntities.createClassrooms().get(0))
                .withTeacher(CreatorTestEntities.createTeachers().get(0))
                .build();
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
        assertThat(lessonDao.findAllByDateAndTeacher("2021-10-19", 2)).containsExactly(
                CreatorTestEntities.createLessons().get(1), CreatorTestEntities.createLessons().get(2));
    }

    @Test
    void findAllByDateAndTeacherShouldReturnEmptyListWhenInputDateNotExist() {
        assertThat(lessonDao.findAllByDateAndTeacher("2021-10-30", 2)).isEmpty();
    }

    @Test
    void findAllByDateAndTeacherShouldReturnEmptyListWhenInputTeacherIdNotExist() {
        assertThat(lessonDao.findAllByDateAndTeacher("2021-10-19", 10)).isEmpty();
    }

    @Test
    void findAllByDateAndGroupShouldReturnExpectedLessonsWhenInputDateAndGroupId() {
        assertThat(lessonDao.findAllByDateAndGroup("2021-10-19", 2)).containsExactly(
                CreatorTestEntities.createLessons().get(1), CreatorTestEntities.createLessons().get(2));
    }

    @Test
    void findAllByDateAndGroupShouldReturnEmptyListWhenInputDateNotExist() {
        assertThat(lessonDao.findAllByDateAndGroup("2021-10-30", 2)).isEmpty();
    }

    @Test
    void findAllByDateAndGroupShouldReturnEmptyListWhenInputGroupIdNotExist() {
        assertThat(lessonDao.findAllByDateAndGroup("2021-10-19", 10)).isEmpty();
    }
}
