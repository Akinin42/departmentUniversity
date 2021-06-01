package org.university.entity;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@EqualsAndHashCode
@Builder(setterPrefix = "with")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Lesson {

    Integer id;
    Course course;
    Teacher teacher;
    Group group;
    LocalDateTime startLesson;
    LocalDateTime endLesson;
    Classroom classroom;
    Boolean onlineLesson;
    String lessonLink;
}
