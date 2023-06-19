package org.university.service.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.university.entity.Lesson;
import org.university.service.CalendarService;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

@Service
@PropertySource("classpath:googlecalendar.properties")
public class CalendarServiceImpl implements CalendarService {

    private final Calendar calendar;

    private static final String LESSON_INFO_PATTERN = "Group-%s Teacher-%s Course-%s";
    private static final String LESSON_ID_PATTERN = "lesson%s";

    @Value("${google.calendar.id}")
    private String calendarId;

    @Value("${google.calendar.time.zone}")
    private String timeZone;

    public CalendarServiceImpl(Calendar calendar) {
        this.calendar = calendar;
    }

    @Override
    public void createLesson(Lesson lesson) throws IOException, GeneralSecurityException {
        String lessonInfo = createEventInfo(lesson);
        String location = lesson.getClassroom().getAddress();
        String lessonId = Integer.toString(lesson.getId());
        Event event = new Event().setSummary(lessonInfo).setLocation(location);
        event.setStart(createEventTime(lesson.getStartLesson()));
        event.setEnd(createEventTime(lesson.getEndLesson()));
        event.setId(formatEventId(lessonId));
        calendar.events().insert(calendarId, event).execute();        
    }

    private String createEventInfo(Lesson lesson) {
        String groupName = lesson.getGroup().getName();
        String teacherName = lesson.getTeacher().getName();
        String courseName = lesson.getCourse().getName();
        return String.format(LESSON_INFO_PATTERN, groupName, teacherName, courseName);
    }

    private EventDateTime createEventTime(LocalDateTime time) {
        DateTime eventTime = formatInputTime(time);
        return new EventDateTime().setDateTime(eventTime).setTimeZone(timeZone);
    }

    private DateTime formatInputTime(LocalDateTime time) {
        return new DateTime(time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:00.000'Z'")));
    }

    private String formatEventId(String eventId) {
        return String.format(LESSON_ID_PATTERN, eventId);
    }

    @Override
    public void deleteLesson(String lessonId) throws IOException, GeneralSecurityException {
        calendar.events().delete(calendarId, formatEventId(lessonId)).execute();
    }

    @Override
    public void updateLesson(Lesson lesson) throws IOException, GeneralSecurityException {
        String lessonInfo = createEventInfo(lesson);
        String location = lesson.getClassroom().getAddress();
        String lessonId = Integer.toString(lesson.getId());
        Event event = new Event().setSummary(lessonInfo).setLocation(location);
        event.setStart(createEventTime(lesson.getStartLesson()));
        event.setEnd(createEventTime(lesson.getEndLesson()));
        calendar.events().update(calendarId, formatEventId(lessonId), event).execute();
    }
}
