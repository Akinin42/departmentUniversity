package org.university.service;

import org.university.dto.LessonDto;
import org.university.entity.Lesson;

public interface LessonService {

    Lesson createLesson(String startLesson, String teacherEmail, String groupName);

    void addLesson(LessonDto lessonDto);

    void delete(Lesson lesson);
}
