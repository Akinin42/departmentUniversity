package org.university.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class LessonDto {    
    Integer id;
    String courseName;
    String teacherEmail;
    String groupName;
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
    String testStart;
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
    String testEnd;
    LocalDateTime startLesson;
    LocalDateTime endLesson;
    Integer classroomNumber;
    Boolean onlineLesson;
    String lessonLink;
}
