package org.university.service.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.university.entity.Lesson;
import org.university.utils.CreatorTestEntities;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.Calendar.Events;
import com.google.api.services.calendar.Calendar.Events.Delete;
import com.google.api.services.calendar.Calendar.Events.Insert;
import com.google.api.services.calendar.Calendar.Events.Update;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

class CalendarServiceImplTest {

    private Calendar calendar;

    private CalendarServiceImpl calendarService;

    @BeforeEach
    public void initMocks() {
        calendar = mock(Calendar.class);
        calendarService = new CalendarServiceImpl(calendar);
    }

    @Test
    void createLessonShouldInsertExpectedEventToCalendar() throws IOException, GeneralSecurityException {
        ReflectionTestUtils.setField(calendarService, "calendarId", "testcalendarid");
        ReflectionTestUtils.setField(calendarService, "timeZone", "Asia/Krasnoyarsk");
        Lesson inputLesson = CreatorTestEntities.createLessons().get(0);
        String lessonInfo = "Group-AB-22 Teacher-Bob Moren Course-Law";
        String lessonLocation = "Test-address";
        String lessonId = "lesson1";
        DateTime startEvent = new DateTime("2021-10-19T10:00:00.000Z");
        DateTime endEvent = new DateTime("2021-10-19T12:00:00.000Z");
        Event expectedEvent = new Event().setSummary(lessonInfo).setLocation(lessonLocation);
        expectedEvent.setStart(new EventDateTime().setDateTime(startEvent).setTimeZone("Asia/Krasnoyarsk"));
        expectedEvent.setEnd(new EventDateTime().setDateTime(endEvent).setTimeZone("Asia/Krasnoyarsk"));
        expectedEvent.setId(lessonId);
        Events calendarEvents = mock(Events.class);
        Insert calendarInsert = mock(Insert.class);
        when(calendar.events()).thenReturn(calendarEvents);
        when(calendarEvents.insert("testcalendarid", expectedEvent)).thenReturn(calendarInsert);
        calendarService.createLesson(inputLesson);
        verify(calendarEvents).insert("testcalendarid", expectedEvent);
        verify(calendarInsert).execute();
    }
    
    @Test
    void deleteLessonShouldDeleteEventFromCalendar() throws IOException, GeneralSecurityException {
        ReflectionTestUtils.setField(calendarService, "calendarId", "testcalendarid");       
        String lessonId = "lesson1";
        Events calendarEvents = mock(Events.class);
        Delete calendarDelete = mock(Delete.class);
        when(calendar.events()).thenReturn(calendarEvents);
        when(calendarEvents.delete("testcalendarid", lessonId)).thenReturn(calendarDelete);
        calendarService.deleteLesson(Integer.toString(1));
        verify(calendarEvents).delete("testcalendarid", lessonId);
        verify(calendarDelete).execute();
    }
    
    @Test
    void updateLessonShouldUpdateExpectedEventToCalendar() throws IOException, GeneralSecurityException {
        ReflectionTestUtils.setField(calendarService, "calendarId", "testcalendarid");
        ReflectionTestUtils.setField(calendarService, "timeZone", "Asia/Krasnoyarsk");
        Lesson inputLesson = CreatorTestEntities.createLessons().get(0);
        String lessonInfo = "Group-AB-22 Teacher-Bob Moren Course-Law";
        String lessonLocation = "Test-address";
        String lessonId = "lesson1";
        DateTime startEvent = new DateTime("2021-10-19T10:00:00.000Z");
        DateTime endEvent = new DateTime("2021-10-19T12:00:00.000Z");
        Event expectedEvent = new Event().setSummary(lessonInfo).setLocation(lessonLocation);
        expectedEvent.setStart(new EventDateTime().setDateTime(startEvent).setTimeZone("Asia/Krasnoyarsk"));
        expectedEvent.setEnd(new EventDateTime().setDateTime(endEvent).setTimeZone("Asia/Krasnoyarsk"));
        Events calendarEvents = mock(Events.class);
        Update calendarUpdate = mock(Update.class);
        when(calendar.events()).thenReturn(calendarEvents);
        when(calendarEvents.update("testcalendarid", lessonId, expectedEvent)).thenReturn(calendarUpdate);
        calendarService.updateLesson(inputLesson);
        verify(calendarEvents).update("testcalendarid", lessonId, expectedEvent);
        verify(calendarUpdate).execute();
    }
}
