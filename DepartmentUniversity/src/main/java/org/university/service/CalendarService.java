package org.university.service;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.university.entity.Lesson;

public interface CalendarService {
    
    void createLesson(Lesson lesson) throws IOException, GeneralSecurityException;
    
    void deleteLesson(String lessonId) throws IOException, GeneralSecurityException;
    
    void updateLesson(Lesson lesson) throws IOException, GeneralSecurityException;
}
