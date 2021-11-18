package org.university.service.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.university.entity.Classroom;
import org.university.entity.Group;
import org.university.entity.Lesson;
import org.university.exceptions.InvalidClassroomCapacityException;
import org.university.exceptions.InvalidLessonTimeException;
import org.university.exceptions.InvalidLinkException;

class LessonValidatorTest {

    private static LessonValidator validator;

    @BeforeAll
    static void init() {
        validator = new LessonValidator();
    }

    @Test
    void validateShouldThrowInvalidClassroomCapacityExceptionWhenInputLessonHasInvalidClassroomCapacityForGroup() {
        Group groupMock = mock(Group.class);
        Classroom classroomMock = mock(Classroom.class);
        Set students = mock(Set.class);
        when(groupMock.getStudents()).thenReturn(students);
        when(students.size()).thenReturn(10);
        when(classroomMock.getCapacity()).thenReturn(5);
        Lesson lesson = Lesson.builder()
                                .withGroup(groupMock)
                                .withClassroom(classroomMock)
                                .build();
        assertThatThrownBy(() -> validator.validate(lesson)).isInstanceOf(InvalidClassroomCapacityException.class);
    }

    @Test
    void validateShouldThrowInvalidLessonTimeExceptionWhenInputLessonStartTimeIsSunday() {
        Lesson lesson = createTestLesson(LocalDateTime.of(2021, Month.MAY, 30, 10, 00, 00),
                LocalDateTime.of(2021, Month.MAY, 30, 15, 00, 00));
        assertThatThrownBy(() -> validator.validate(lesson)).isInstanceOf(InvalidLessonTimeException.class);
    }
    
    @Test
    void validateShouldThrowInvalidLessonTimeExceptionWhenInputLessonStartTimeBeforeNineAM() {
        Lesson lesson = createTestLesson(LocalDateTime.of(2021, Month.MAY, 28, 8, 00, 00),
                LocalDateTime.of(2021, Month.MAY, 28, 11, 00, 00));
        assertThatThrownBy(() -> validator.validate(lesson)).isInstanceOf(InvalidLessonTimeException.class);
    }
    
    @Test
    void validateShouldThrowInvalidLessonTimeExceptionWhenInputLessonStartTimeAfterSixPM() {
        Lesson lesson = createTestLesson(LocalDateTime.of(2021, Month.MAY, 28, 19, 00, 00),
                LocalDateTime.of(2021, Month.MAY, 28, 21, 00, 00));
        assertThatThrownBy(() -> validator.validate(lesson)).isInstanceOf(InvalidLessonTimeException.class);
    }
    
    @Test
    void validateShouldThrowInvalidLessonTimeExceptionWhenInputLessonStartLaterLessonEnd() {
        Lesson lesson = createTestLesson(LocalDateTime.of(2021, Month.MAY, 28, 15, 00, 00),
                LocalDateTime.of(2021, Month.MAY, 28, 13, 00, 00));
        assertThatThrownBy(() -> validator.validate(lesson)).isInstanceOf(InvalidLessonTimeException.class);
    }
    
    @Test
    void validateShouldThrowInvalidLessonTimeExceptionWhenInputLessonStartEqualLessonEnd() {
        Lesson lesson = createTestLesson(LocalDateTime.of(2021, Month.MAY, 28, 15, 00, 00),
                LocalDateTime.of(2021, Month.MAY, 28, 15, 00, 00));
        assertThatThrownBy(() -> validator.validate(lesson)).isInstanceOf(InvalidLessonTimeException.class);
    }
    
    @Test
    void validateShouldThrowInvalidLinkExceptionWhenInputLinkNullEndLessonOnline() {
        Group groupMock = mock(Group.class);
        Classroom classroomMock = mock(Classroom.class);
        Set students = mock(Set.class);
        when(groupMock.getStudents()).thenReturn(students);
        when(students.size()).thenReturn(10);
        when(classroomMock.getCapacity()).thenReturn(15);
        Lesson lesson = Lesson.builder()
                                .withGroup(groupMock)
                                .withClassroom(classroomMock)
                                .withStartLesson(LocalDateTime.of(2021, Month.MAY, 28, 13, 00, 00))
                                .withEndLesson(LocalDateTime.of(2021, Month.MAY, 28, 15, 00, 00))
                                .withOnlineLesson(true)
                                .withLessonLink(null)
                                .build();
        assertThatThrownBy(() -> validator.validate(lesson)).isInstanceOf(InvalidLinkException.class);
    }
    
    @Test
    void validateShouldThrowInvalidLinkExceptionWhenInputLinkInvalidEndLessonOnline() {
        Group groupMock = mock(Group.class);
        Classroom classroomMock = mock(Classroom.class);
        Set students = mock(Set.class);
        when(groupMock.getStudents()).thenReturn(students);
        when(students.size()).thenReturn(10);
        when(classroomMock.getCapacity()).thenReturn(15);
        Lesson lesson = Lesson.builder()
                                .withGroup(groupMock)
                                .withClassroom(classroomMock)
                                .withStartLesson(LocalDateTime.of(2021, Month.MAY, 28, 13, 00, 00))
                                .withEndLesson(LocalDateTime.of(2021, Month.MAY, 28, 15, 00, 00))
                                .withOnlineLesson(true)
                                .withLessonLink("   ")
                                .build();
        assertThatThrownBy(() -> validator.validate(lesson)).isInstanceOf(InvalidLinkException.class);
    }    

    private Lesson createTestLesson(LocalDateTime start, LocalDateTime end) {
        Group groupMock = mock(Group.class);
        Classroom classroomMock = mock(Classroom.class);
        Set students = mock(Set.class);
        when(groupMock.getStudents()).thenReturn(students);
        when(students.size()).thenReturn(10);
        when(classroomMock.getCapacity()).thenReturn(15);
        return Lesson.builder()
                        .withGroup(groupMock)
                        .withClassroom(classroomMock)
                        .withStartLesson(start)
                        .withEndLesson(end)
                        .build();
    }
}
