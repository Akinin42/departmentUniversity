package org.university.service;

import org.university.entity.Lesson;

public interface LessonService {

    Lesson createLesson(String startLesson, String teacherEmail, String groupName);

    void addLesson(Lesson lesson);

    void delete(Lesson lesson);
}
