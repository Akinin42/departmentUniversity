package org.university.dto;

import javax.validation.constraints.NotEmpty;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class LessonDto {    
    private Integer id;
    private String courseName;
    private String teacherEmail;
    private String groupName;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @NotEmpty
    private String startLesson;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @NotEmpty
    private String endLesson;
    
    private Integer classroomNumber;
    private Boolean onlineLesson;
    private String lessonLink;
}
