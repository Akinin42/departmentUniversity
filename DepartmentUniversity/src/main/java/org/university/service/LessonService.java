package org.university.service;

import java.time.LocalDateTime;
import org.university.dto.LessonDto;
import org.university.entity.Lesson;

public interface LessonService {

    Lesson createLesson(LocalDateTime startLesson, String teacherEmail, String groupName);

    void addLesson(LessonDto lessonDto);

    void delete(LessonDto lessonDto);
    
    void edit(LessonDto lessonDto);
}
