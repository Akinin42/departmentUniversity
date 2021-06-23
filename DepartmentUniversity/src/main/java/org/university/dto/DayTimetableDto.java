package org.university.dto;

import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.university.entity.Lesson;

import lombok.Data;

@Data
public class DayTimetableDto {
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    String day;
    List<Lesson> lessons;
    String groupName;
    String teacherEmail;
}
