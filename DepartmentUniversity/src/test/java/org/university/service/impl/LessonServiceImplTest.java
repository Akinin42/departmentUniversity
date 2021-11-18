package org.university.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.university.dao.LessonDao;
import org.university.dto.LessonDto;
import org.university.entity.Group;
import org.university.entity.Lesson;
import org.university.entity.Teacher;
import org.university.exceptions.ClassroomBusyException;
import org.university.exceptions.EntityAlreadyExistException;
import org.university.exceptions.EntityNotExistException;
import org.university.exceptions.InvalidLessonTimeException;
import org.university.service.CalendarService;
import org.university.service.mapper.LessonDtoMapper;
import org.university.service.validator.LessonValidator;
import org.university.utils.CreatorTestEntities;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

class LessonServiceImplTest {

    private static LessonServiceImpl lessonService;
    private static LessonDao lessonDaoMock;
    private static Lesson lessonMock;
    private static LessonDtoMapper mapperMock;
    private static CalendarService calendarServiceMock;

    @BeforeAll
    static void init() {
        lessonDaoMock = createLessonDaoMock();
        mapperMock = mock(LessonDtoMapper.class);
        calendarServiceMock = mock(CalendarServiceImpl.class);
        lessonService = new LessonServiceImpl(lessonDaoMock, mock(LessonValidator.class), mapperMock,
                calendarServiceMock);
        lessonMock = createLessonMock();
    }

    @Test
    void createLessonShouldReturnExpectedLessonWhenItExistsInDatabase() {
        Lesson lesson = CreatorTestEntities.createLessons().get(0);
        assertThat(lessonService.createLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00), 1, 1))
                .isEqualTo(lesson);
    }

    @Test
    void createLessonShouldThrowEntityNotExistExceptionWhenLessonWithInputDataNotExist() {
        assertThatThrownBy(
                () -> lessonService.createLesson(LocalDateTime.of(2021, Month.OCTOBER, 30, 10, 00, 00), 25, 25))
                        .isInstanceOf(EntityNotExistException.class);
    }

    @Test
    void addLessonShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> lessonService.addLesson(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addLessonShouldThrowEntityAlreadyExistExceptionWhenInputLessonExist() {
        Lesson lessonMock = mock(Lesson.class);
        when(lessonMock.getId()).thenReturn(1);
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.addLesson(lessonDto)).isInstanceOf(EntityAlreadyExistException.class);
    }

    @Test
    void addLessonShouldNotThrowEntityAlreadyExistExceptionWhenInputLessonIdNull() {
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getId()).thenReturn(null);
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        lessonService.addLesson(lessonDto);
        verify(lessonDaoMock).save(lessonMock);
    }

    @Test
    void addLessonShouldThrowInvalidLessonTimeExceptionWhenTeacherBusyThisTime() {
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 15, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 16, 00, 00));
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        when(lessonDaoMock.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(
                LocalDateTime.of(2021, Month.OCTOBER, 19, 0, 0), LocalDateTime.of(2021, Month.OCTOBER, 19, 23, 0), 2))
                        .thenReturn(lessons);
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.addLesson(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void addLessonShouldThrowInvalidLessonTimeExceptionWhenGroupBusyThisTime() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 12, 00, 00));
        when(lessonMock.getOnlineLesson()).thenReturn(false);
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        lessons.remove(2);
        lessons.remove(1);
        when(lessonDaoMock.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(
                LocalDateTime.of(2021, Month.OCTOBER, 19, 0, 0), LocalDateTime.of(2021, Month.OCTOBER, 19, 23, 0), 2))
                        .thenReturn(lessons);
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.addLesson(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }
    
    @Test
    void addLessonShouldThrowInvalidLessonTimeExceptionWhenGroupAndTeacherBusyThisTime() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 12, 00, 00));
        when(lessonMock.getOnlineLesson()).thenReturn(false);
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        lessons.remove(2);
        lessons.remove(1);
        when(lessonDaoMock.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(
                LocalDateTime.of(2021, Month.OCTOBER, 19, 0, 0), LocalDateTime.of(2021, Month.OCTOBER, 19, 23, 0), 2))
                        .thenReturn(lessons);
        when(lessonDaoMock.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(
                LocalDateTime.of(2021, Month.OCTOBER, 19, 0, 0), LocalDateTime.of(2021, Month.OCTOBER, 19, 23, 0), 2))
                        .thenReturn(lessons);
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.addLesson(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void addLessonShouldSaveLessonWhenTeqcherAndGroupFree() throws IOException, GeneralSecurityException {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 18, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 19, 00, 00));
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        lessonService.addLesson(lessonDto);
        verify(lessonDaoMock).save(lessonMock);
        verify(calendarServiceMock).createLesson(lessonMock);
    }

    @Test
    void addLessonShouldThrowInvalidLessonTimeExceptionWhenLessonAfterLastButNotBeforeNext() {
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 18, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 21, 00, 00));
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        when(lessonDaoMock.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(
                LocalDateTime.of(2021, Month.OCTOBER, 19, 0, 0), LocalDateTime.of(2021, Month.OCTOBER, 19, 23, 0), 2))
                        .thenReturn(lessons);
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.addLesson(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }

    @Test
    void addLessonShouldSaveLessonWhenLessonAfterLast() {
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 22, 30, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 22, 55, 00));
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        when(lessonDaoMock.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(
                LocalDateTime.of(2021, Month.OCTOBER, 19, 0, 0), LocalDateTime.of(2021, Month.OCTOBER, 19, 23, 0), 2))
                        .thenReturn(lessons);
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        lessonService.addLesson(lessonDto);
        verify(lessonDaoMock).save(lessonMock);
    }

    @Test
    void addLessonShouldSaveLessonWhenLessonValidAndBeforeOutherLessonOnDay() {
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 9, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 9, 30, 00));
        LessonDto lessonDto = new LessonDto();
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        when(lessonDaoMock.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(
                LocalDateTime.of(2021, Month.OCTOBER, 19, 0, 0), LocalDateTime.of(2021, Month.OCTOBER, 19, 23, 0), 2))
                        .thenReturn(lessons);
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        lessonService.addLesson(lessonDto);
        verify(lessonDaoMock).save(lessonMock);
    }

    @Test
    void addLessonShouldThrowClassroomBusyExceptionWhenForTeacherClassroomBusy() {
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 12, 00, 00));
        when(lessonMock.getClassroom()).thenReturn(CreatorTestEntities.createClassrooms().get(0));
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        lessons.remove(0);
        when(lessonDaoMock.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(
                LocalDateTime.of(2021, Month.OCTOBER, 19, 0, 0), LocalDateTime.of(2021, Month.OCTOBER, 19, 23, 0), 2))
                        .thenReturn(lessons);
        when(lessonDaoMock.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(
                LocalDateTime.of(2021, Month.OCTOBER, 19, 0, 0), LocalDateTime.of(2021, Month.OCTOBER, 19, 23, 0), 2))
                        .thenReturn(lessons);
        LessonDto lessonDto = new LessonDto();
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        assertThatThrownBy(() -> lessonService.addLesson(lessonDto)).isInstanceOf(ClassroomBusyException.class);
    }
    
    @Test
    void addLessonShouldLoggingErrorMessageWhenAddToCalendarFailed() throws IOException, GeneralSecurityException {
        LessonServiceImpl lessonService = new LessonServiceImpl(lessonDaoMock, mock(LessonValidator.class), mapperMock,
                calendarServiceMock);
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 9, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 9, 30, 00));
        LessonDto lessonDto = new LessonDto();
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        when(lessonDaoMock.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(
                LocalDateTime.of(2021, Month.OCTOBER, 19, 0, 0), LocalDateTime.of(2021, Month.OCTOBER, 19, 23, 0), 2))
                        .thenReturn(lessons);
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        doThrow(new GeneralSecurityException()).when(calendarServiceMock).createLesson(lessonMock);
        Logger lessonServiceLogger = (Logger) LoggerFactory.getLogger(LessonServiceImpl.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        lessonServiceLogger.addAppender(listAppender);
        List<ILoggingEvent> logsList = listAppender.list;
        lessonService.addLesson(lessonDto);
        verify(lessonDaoMock).save(lessonMock);
        assertEquals("Add in calendar failed", logsList.get(0).getMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
    }

    @Test
    void deleteShouldDeleteLessonWhenLessonExists() throws IOException, GeneralSecurityException {
        LessonDao lessonDaoMock = createLessonDaoMock();
        CalendarService calendarServiceMock = mock(CalendarServiceImpl.class);
        LessonServiceImpl lessonService = new LessonServiceImpl(lessonDaoMock, mock(LessonValidator.class), mapperMock,
                calendarServiceMock);        
        LessonDto lessonDtoMock = mock(LessonDto.class);
        when(lessonDtoMock.getId()).thenReturn(1);
        lessonService.delete(lessonDtoMock);
        verify(lessonDaoMock).deleteById(1);
        verify(calendarServiceMock).deleteLesson(Integer.toString(1));
    }
    
    @Test
    void deleteShouldLoggingErrorMessageWhenDeleteFromCalendarFailed() throws IOException, GeneralSecurityException {
        LessonDao lessonDaoMock = createLessonDaoMock();
        LessonServiceImpl lessonService = new LessonServiceImpl(lessonDaoMock, mock(LessonValidator.class), mapperMock,
                calendarServiceMock);
        LessonDto lessonDtoMock = mock(LessonDto.class);
        when(lessonDtoMock.getId()).thenReturn(1);
        doThrow(new GeneralSecurityException()).when(calendarServiceMock).deleteLesson(Integer.toString(1));
        Logger lessonServiceLogger = (Logger) LoggerFactory.getLogger(LessonServiceImpl.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        lessonServiceLogger.addAppender(listAppender);
        List<ILoggingEvent> logsList = listAppender.list;
        lessonService.delete(lessonDtoMock);
        verify(lessonDaoMock).deleteById(1);
        assertEquals("Delete from calendar failed", logsList.get(0).getMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
    }

    @Test
    void deleteShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> lessonService.delete(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void editShouldThrowIllegalArgumentExceptionWhenInputNull() {
        assertThatThrownBy(() -> lessonService.edit(null)).isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void editShouldThrowInvalidLessonTimeExceptionWhenTeacherBusyThisTime() {
        LessonDto lessonDto = createLessonDto();
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 12, 00, 00));
        Group group = Group.builder()
                .withId(2)
                .withName("FR-33")
                .withStudents(new HashSet<>(CreatorTestEntities.createStudents()))
                .build();
        when(lessonMock.getGroup()).thenReturn(group);
        when(lessonMock.getTeacher()).thenReturn(CreatorTestEntities.createTeachers().get(1)); 
        LessonDao lessonDaoMock = createLessonDaoMock();    
        when(lessonDaoMock.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19,0,0),LocalDateTime.of(2021, Month.OCTOBER, 19,23,0), 2)).thenReturn(CreatorTestEntities.createLessons());
        when(lessonDaoMock.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19,0,0),LocalDateTime.of(2021, Month.OCTOBER, 19,23,0), 2)).thenReturn(new ArrayList<>());        
        when(lessonDaoMock.findById(1)).thenReturn(Optional.ofNullable(lessonMock));        
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        LessonServiceImpl lessonService = new LessonServiceImpl(lessonDaoMock, new LessonValidator(), mapperMock, calendarServiceMock);
        assertThatThrownBy(() -> lessonService.edit(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }
    
    @Test
    void editShouldThrowInvalidLessonTimeExceptionWhenGroupBusyThisTime() {
        LessonDto lessonDto = createLessonDto();                
        Lesson mappedLesson = createLessonMock();
        when(mappedLesson.getId()).thenReturn(1);
        when(mappedLesson.getGroup()).thenReturn(CreatorTestEntities.createGroups().get(0));
        when(mappedLesson.getTeacher()).thenReturn(CreatorTestEntities.createTeachers().get(1));
        when(mappedLesson.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00));
        when(mappedLesson.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 12, 00, 00));
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(mappedLesson);        
        LessonDao lessonDaoMock = createLessonDaoMock();
        Lesson lessonFromDb = createLessonMock();
        when(lessonFromDb.getId()).thenReturn(1);
        when(lessonFromDb.getGroup()).thenReturn(CreatorTestEntities.createGroups().get(1));
        when(lessonFromDb.getTeacher()).thenReturn(CreatorTestEntities.createTeachers().get(1));
        when(lessonFromDb.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00));
        when(lessonFromDb.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 12, 00, 00));
        when(lessonDaoMock.findById(1)).thenReturn(Optional.ofNullable(lessonFromDb));        
        List<Lesson> lessons = CreatorTestEntities.createLessons();        
        when(lessonDaoMock.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19,0,0),LocalDateTime.of(2021, Month.OCTOBER, 19,23,0), 2)).thenReturn(new ArrayList<>());
        when(lessonDaoMock.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19,0,0),LocalDateTime.of(2021, Month.OCTOBER, 19,23,0), 1)).thenReturn(lessons);
        LessonServiceImpl lessonService = new LessonServiceImpl(lessonDaoMock, mock(LessonValidator.class), mapperMock, calendarServiceMock);
        assertThatThrownBy(() -> lessonService.edit(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }
    
    @Test
    void editShouldEditLessonWhenNotChangeTimesTeacherGroupAndClassroom() {
        LessonDto lessonDto = new LessonDto();
        lessonDto.setId(2);
        lessonDto.setStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 13, 00, 00).toString());
        lessonDto.setEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 14, 00, 00).toString());
        lessonDto.setGroupName("FR-33");
        lessonDto.setTeacherEmail("Ann@mail.ru");
        LessonDao lessonDaoMock = createLessonDaoMock();
        Group group = Group.builder()
                .withId(2)
                .withName("FR-33")
                .withStudents(new HashSet<>(CreatorTestEntities.createStudents()))
                .build();
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getGroup()).thenReturn(group);
        when(lessonMock.getTeacher()).thenReturn(CreatorTestEntities.createTeachers().get(1));
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 13, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 14, 00, 00));
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        List<Lesson> lessons = CreatorTestEntities.createLessons();        
        when(lessonDaoMock.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19,0,0),LocalDateTime.of(2021, Month.OCTOBER, 19,23,0), 2)).thenReturn(lessons);
        when(lessonDaoMock.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19,0,0),LocalDateTime.of(2021, Month.OCTOBER, 19,23,0), 2)).thenReturn(lessons);        
        when(lessonDaoMock.findById(2)).thenReturn(Optional.ofNullable(lessonMock));
        LessonServiceImpl lessonService = new LessonServiceImpl(lessonDaoMock, new LessonValidator(), mapperMock, calendarServiceMock);
        lessonService.edit(lessonDto);
        verify(lessonDaoMock).save(lessonMock);
    }
    
    @Test
    void editShouldEditLessonWhenChangeTimesTeacherGroupAndClassroom() {
        LessonDto lessonDto = new LessonDto();
        lessonDto.setId(2);
        lessonDto.setStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 29, 13, 00, 00).toString());
        lessonDto.setEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 29, 14, 00, 00).toString());
        lessonDto.setGroupName("AB-22");
        lessonDto.setTeacherEmail("Bob@mail.ru");
        lessonDto.setClassroomNumber(1);
        LessonDao lessonDaoMock = createLessonDaoMock();
        Group group = Group.builder()
                .withId(2)
                .withName("FR-33")
                .withStudents(new HashSet<>(CreatorTestEntities.createStudents()))
                .build();
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getGroup()).thenReturn(group);
        when(lessonMock.getTeacher()).thenReturn(CreatorTestEntities.createTeachers().get(1));
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 29, 13, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 29, 16, 00, 00));
        when(lessonMock.getClassroom()).thenReturn(CreatorTestEntities.createClassrooms().get(1));
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        List<Lesson> lessons = new ArrayList<>();        
        when(lessonDaoMock.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 29,0,0),LocalDateTime.of(2021, Month.OCTOBER, 29,23,0), 2)).thenReturn(lessons);
        when(lessonDaoMock.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 29,0,0),LocalDateTime.of(2021, Month.OCTOBER, 29,23,0), 2)).thenReturn(lessons);        
        when(lessonDaoMock.findById(2)).thenReturn(Optional.ofNullable(lessonMock));
        LessonServiceImpl lessonService = new LessonServiceImpl(lessonDaoMock, new LessonValidator(), mapperMock, calendarServiceMock);
        lessonService.edit(lessonDto);
        verify(lessonDaoMock).save(lessonMock);
    }
    
    @Test
    void editShouldEditLessonWhenChangeTimesTeacherGroupButNotClassroom() {
        LessonDto lessonDto = new LessonDto();
        lessonDto.setId(2);
        lessonDto.setStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 29, 13, 00, 00).toString());
        lessonDto.setEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 29, 14, 00, 00).toString());
        lessonDto.setGroupName("AB-22");
        lessonDto.setTeacherEmail("Bob@mail.ru");
        lessonDto.setClassroomNumber(2);
        LessonDao lessonDaoMock = createLessonDaoMock();
        Group group = Group.builder()
                .withId(2)
                .withName("FR-33")
                .withStudents(new HashSet<>(CreatorTestEntities.createStudents()))
                .build();
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getGroup()).thenReturn(group);
        when(lessonMock.getTeacher()).thenReturn(CreatorTestEntities.createTeachers().get(1));
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 29, 13, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 29, 14, 00, 00));
        when(lessonMock.getClassroom()).thenReturn(CreatorTestEntities.createClassrooms().get(1));
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        List<Lesson> lessons = new ArrayList<>();        
        when(lessonDaoMock.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 29,0,0),LocalDateTime.of(2021, Month.OCTOBER, 29,23,0), 2)).thenReturn(lessons);
        when(lessonDaoMock.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 29,0,0),LocalDateTime.of(2021, Month.OCTOBER, 29,23,0), 2)).thenReturn(lessons);        
        when(lessonDaoMock.findById(2)).thenReturn(Optional.ofNullable(lessonMock));
        LessonServiceImpl lessonService = new LessonServiceImpl(lessonDaoMock, new LessonValidator(), mapperMock, calendarServiceMock);
        lessonService.edit(lessonDto);
        verify(lessonDaoMock).save(lessonMock);
    }
    
    @Test
    void editShouldLoggingErrorMessageWhenEditCalendarFailed() throws IOException, GeneralSecurityException {
        LessonDto lessonDto = new LessonDto();
        lessonDto.setId(2);
        lessonDto.setStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 29, 13, 00, 00).toString());
        lessonDto.setEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 29, 14, 00, 00).toString());
        lessonDto.setGroupName("AB-22");
        lessonDto.setTeacherEmail("Bob@mail.ru");
        lessonDto.setClassroomNumber(2);
        LessonDao lessonDaoMock = createLessonDaoMock();
        Group group = Group.builder()
                .withId(2)
                .withName("FR-33")
                .withStudents(new HashSet<>(CreatorTestEntities.createStudents()))
                .build();
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getGroup()).thenReturn(group);
        when(lessonMock.getTeacher()).thenReturn(CreatorTestEntities.createTeachers().get(1));
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 29, 13, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 29, 14, 00, 00));
        when(lessonMock.getClassroom()).thenReturn(CreatorTestEntities.createClassrooms().get(1));
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        List<Lesson> lessons = new ArrayList<>();        
        when(lessonDaoMock.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 29,0,0),LocalDateTime.of(2021, Month.OCTOBER, 29,23,0), 2)).thenReturn(lessons);
        when(lessonDaoMock.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 29,0,0),LocalDateTime.of(2021, Month.OCTOBER, 29,23,0), 2)).thenReturn(lessons);        
        when(lessonDaoMock.findById(2)).thenReturn(Optional.ofNullable(lessonMock));
        LessonServiceImpl lessonService = new LessonServiceImpl(lessonDaoMock, new LessonValidator(), mapperMock, calendarServiceMock);
        doThrow(new GeneralSecurityException()).when(calendarServiceMock).updateLesson(lessonMock);
        Logger lessonServiceLogger = (Logger) LoggerFactory.getLogger(LessonServiceImpl.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        lessonServiceLogger.addAppender(listAppender);
        List<ILoggingEvent> logsList = listAppender.list;
        lessonService.edit(lessonDto);
        verify(lessonDaoMock).save(lessonMock);
        assertEquals("Edit in calendar failed", logsList.get(0).getMessage());
        assertEquals(Level.ERROR, logsList.get(0).getLevel());
    }
    
    @Test
    void editShouldThrowClassroomBusyExceptionWhenNotChangeTimesTeacherGroupButClassroomChange() {
        LessonDto lessonDto = new LessonDto();
        lessonDto.setId(2);
        lessonDto.setStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 13, 00, 00).toString());
        lessonDto.setEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 14, 00, 00).toString());
        lessonDto.setGroupName("FR-33");
        lessonDto.setTeacherEmail("Ann@mail.ru");
        lessonDto.setClassroomNumber(1);
        LessonDao lessonDaoMock = createLessonDaoMock();
        Group group = Group.builder()
                .withId(2)
                .withName("FR-33")
                .withStudents(new HashSet<>(CreatorTestEntities.createStudents()))
                .build();
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getGroup()).thenReturn(group);
        when(lessonMock.getTeacher()).thenReturn(CreatorTestEntities.createTeachers().get(1));
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 13, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 14, 00, 00));
        when(lessonMock.getClassroom()).thenReturn(CreatorTestEntities.createClassrooms().get(1));
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        List<Lesson> lessons = CreatorTestEntities.createLessons();        
        when(lessonDaoMock.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19,0,0),LocalDateTime.of(2021, Month.OCTOBER, 19,23,0), 2)).thenReturn(lessons);
        when(lessonDaoMock.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19,0,0),LocalDateTime.of(2021, Month.OCTOBER, 19,23,0), 2)).thenReturn(lessons);        
        when(lessonDaoMock.findById(2)).thenReturn(Optional.ofNullable(lessonMock));
        List<Lesson> classroomLessons = new ArrayList<>();
        classroomLessons.add(lessonMock);
        when(lessonDaoMock.findAllByStartLessonBetweenOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19,0,0),LocalDateTime.of(2021, Month.OCTOBER, 19,23,0))).thenReturn(classroomLessons);
        LessonServiceImpl lessonService = new LessonServiceImpl(lessonDaoMock, new LessonValidator(), mapperMock, calendarServiceMock);
        assertThatThrownBy(() -> lessonService.edit(lessonDto)).isInstanceOf(ClassroomBusyException.class);
    }
    
    @Test
    void editShouldThrowClassroomBusyExceptionWhenChangeTimesTeacherGroupButClassroomNotChange() {
        LessonDto lessonDto = new LessonDto();
        lessonDto.setId(2);
        lessonDto.setStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 13, 00, 00).toString());
        lessonDto.setEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 14, 00, 00).toString());
        lessonDto.setGroupName("FR-33");
        lessonDto.setTeacherEmail("Ann@mail.ru");
        lessonDto.setClassroomNumber(1);
        LessonDao lessonDaoMock = createLessonDaoMock();
        Group group = Group.builder()
                .withId(2)
                .withName("FR-33")
                .withStudents(new HashSet<>(CreatorTestEntities.createStudents()))
                .build();
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getGroup()).thenReturn(group);
        when(lessonMock.getTeacher()).thenReturn(CreatorTestEntities.createTeachers().get(1));
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 15, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 16, 00, 00));
        when(lessonMock.getClassroom()).thenReturn(CreatorTestEntities.createClassrooms().get(0));
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        List<Lesson> lessons = new ArrayList<>();        
        when(lessonDaoMock.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19,0,0),LocalDateTime.of(2021, Month.OCTOBER, 19,23,0), 2)).thenReturn(lessons);
        when(lessonDaoMock.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19,0,0),LocalDateTime.of(2021, Month.OCTOBER, 19,23,0), 2)).thenReturn(lessons);        
        when(lessonDaoMock.findById(2)).thenReturn(Optional.ofNullable(lessonMock));
        List<Lesson> classroomLessons = new ArrayList<>();
        classroomLessons.add(lessonMock);
        when(lessonDaoMock.findAllByStartLessonBetweenOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19,0,0),LocalDateTime.of(2021, Month.OCTOBER, 19,23,0))).thenReturn(classroomLessons);
        LessonServiceImpl lessonService = new LessonServiceImpl(lessonDaoMock, new LessonValidator(), mapperMock, calendarServiceMock);
        assertThatThrownBy(() -> lessonService.edit(lessonDto)).isInstanceOf(ClassroomBusyException.class);
    }
    
    @Test
    void editShouldThrowClassroomBusyExceptionWhenChangeTimesTeacherGroupAndClassroom() {
        LessonDto lessonDto = new LessonDto();
        lessonDto.setId(2);
        lessonDto.setStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 13, 00, 00).toString());
        lessonDto.setEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 14, 00, 00).toString());
        lessonDto.setGroupName("FR-33");
        lessonDto.setTeacherEmail("Ann@mail.ru");
        lessonDto.setClassroomNumber(2);
        LessonDao lessonDaoMock = createLessonDaoMock();
        Group group = Group.builder()
                .withId(2)
                .withName("FR-33")
                .withStudents(new HashSet<>(CreatorTestEntities.createStudents()))
                .build();
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getGroup()).thenReturn(group);
        when(lessonMock.getTeacher()).thenReturn(CreatorTestEntities.createTeachers().get(1));
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 15, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 16, 00, 00));
        when(lessonMock.getClassroom()).thenReturn(CreatorTestEntities.createClassrooms().get(0));
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        List<Lesson> lessons = new ArrayList<>();        
        when(lessonDaoMock.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19,0,0),LocalDateTime.of(2021, Month.OCTOBER, 19,23,0), 2)).thenReturn(lessons);
        when(lessonDaoMock.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19,0,0),LocalDateTime.of(2021, Month.OCTOBER, 19,23,0), 2)).thenReturn(lessons);        
        when(lessonDaoMock.findById(2)).thenReturn(Optional.ofNullable(lessonMock));
        List<Lesson> classroomLessons = new ArrayList<>();
        classroomLessons.add(lessonMock);
        when(lessonDaoMock.findAllByStartLessonBetweenOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19,0,0),LocalDateTime.of(2021, Month.OCTOBER, 19,23,0))).thenReturn(classroomLessons);
        LessonServiceImpl lessonService = new LessonServiceImpl(lessonDaoMock, new LessonValidator(), mapperMock, calendarServiceMock);
        assertThatThrownBy(() -> lessonService.edit(lessonDto)).isInstanceOf(ClassroomBusyException.class);
    }

    @Test
    void editShouldThrowInvalidLessonTimeExceptionWhenTimeChangesAndTeacherBusyThisTime(){
        LessonDto lessonDto = new LessonDto();
        lessonDto.setId(1);
        lessonDto.setStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 15, 00, 00).toString());
        lessonDto.setEndLesson(LocalDateTime.of(2021, Month.OCTOBER, 19, 17, 00, 00).toString());
        lessonDto.setGroupName("FR-33");
        lessonDto.setTeacherEmail("Bob@mail.ru");
        lessonDto.setClassroomNumber(1);
        Lesson lessonMock = createLessonMock();
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 15, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 18, 00, 00));
        when(mapperMock.mapDtoToEntity(lessonDto)).thenReturn(lessonMock);
        List<Lesson> lessons = CreatorTestEntities.createLessons();
        LessonDao lessonDaoMock = createLessonDaoMock();
        when(lessonDaoMock.findAllByStartLessonBetweenAndTeacherIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19,0,0),LocalDateTime.of(2021, Month.OCTOBER, 19,23,0), 2)).thenReturn(lessons);
        when(lessonDaoMock.findAllByStartLessonBetweenAndGroupIdOrderByStartLesson(LocalDateTime.of(2021, Month.OCTOBER, 19,0,0),LocalDateTime.of(2021, Month.OCTOBER, 19,23,0), 2)).thenReturn(lessons);
        LessonServiceImpl lessonService = new LessonServiceImpl(lessonDaoMock, new LessonValidator(), mapperMock, calendarServiceMock);
        assertThatThrownBy(() -> lessonService.edit(lessonDto)).isInstanceOf(InvalidLessonTimeException.class);
    }

    private static Lesson createLessonMock() {
        Lesson lessonMock = mock(Lesson.class);
        Group groupMock = mock(Group.class);
        Teacher teacherMock = mock(Teacher.class);
        Set students = mock(Set.class);
        when(lessonMock.getGroup()).thenReturn(groupMock);
        when(lessonMock.getTeacher()).thenReturn(teacherMock);
        when(lessonMock.getClassroom()).thenReturn(CreatorTestEntities.createClassrooms().get(1));
        when(lessonMock.getStartLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 13, 00, 00));
        when(lessonMock.getEndLesson()).thenReturn(LocalDateTime.of(2021, Month.OCTOBER, 19, 14, 00, 00));
        when(groupMock.getStudents()).thenReturn(students);
        when(groupMock.getId()).thenReturn(2);
        when(teacherMock.getId()).thenReturn(2);
        when(students.size()).thenReturn(2);
        when(lessonMock.getOnlineLesson()).thenReturn(true);
        when(lessonMock.getLessonLink()).thenReturn("valid lesson link");
        return lessonMock;
    }

    private static LessonDto createLessonDto() {
        LessonDto lessonDto = new LessonDto();
        lessonDto.setId(1);
        lessonDto.setStartLesson("2021-10-19T10:00");
        lessonDto.setEndLesson("2021-10-19T12:00");
        lessonDto.setGroupName("FR-33");
        lessonDto.setTeacherEmail("Bob@mail.ru");
        return lessonDto;
    }

    private static LessonDao createLessonDaoMock() {
        LessonDao lessonDaoMock = mock(LessonDao.class);
        when(lessonDaoMock
                .findByStartLessonAndTeacherIdAndGroupId(LocalDateTime.of(2021, Month.OCTOBER, 19, 10, 00, 00), 1, 1))
                        .thenReturn(Optional.ofNullable(CreatorTestEntities.createLessons().get(0)));
        when(lessonDaoMock
                .findByStartLessonAndTeacherIdAndGroupId(LocalDateTime.of(2021, Month.OCTOBER, 30, 10, 00, 00), 25, 25))
                        .thenReturn(Optional.empty());
        when(lessonDaoMock.findById(1)).thenReturn(Optional.ofNullable(CreatorTestEntities.createLessons().get(0)));
        when(lessonDaoMock.findById(10)).thenReturn(Optional.empty());
        when(lessonDaoMock.findAllByStartLessonBetweenOrderByStartLesson(
                LocalDateTime.of(2021, Month.OCTOBER, 19, 0, 0), LocalDateTime.of(2021, Month.OCTOBER, 19, 23, 0)))
                        .thenReturn(CreatorTestEntities.createLessons());
        return lessonDaoMock;
    }
}
