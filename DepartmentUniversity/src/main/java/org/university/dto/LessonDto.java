package org.university.dto;

import org.springframework.format.annotation.DateTimeFormat;
import lombok.Data;

@Data
public class LessonDto {    
    Integer id;
    String courseName;
    String teacherEmail;
    String groupName;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    String startLesson;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    String endLesson;
    Integer classroomNumber;
    Boolean onlineLesson;
    String lessonLink;
}
